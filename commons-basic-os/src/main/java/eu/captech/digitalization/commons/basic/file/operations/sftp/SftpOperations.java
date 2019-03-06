package eu.captech.digitalization.commons.basic.file.operations.sftp;

import ch.ethz.ssh2.*;
import com.google.common.io.ByteStreams;
import eu.captech.digitalization.commons.basic.api.AbstractFileSystemOperations;
import eu.captech.digitalization.commons.basic.doc.Preamble;
import eu.captech.digitalization.commons.basic.exceptions.OperationRuntimeException;
import eu.captech.digitalization.commons.basic.exceptions.SftpException;
import eu.captech.digitalization.commons.basic.exceptions.SftpRuntimeConnectionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.NotNull;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Pattern;

@Preamble(
        lastModifiedBy = "Eduardo Melgar",
        creationDate = "10/22/12",
        creationTime = "11:55 AM",
        lastModified = "10/22/12"
)
public class SftpOperations extends AbstractFileSystemOperations {
    private static final Logger LOGGER = LoggerFactory.getLogger(SftpOperations.class);
    private static final Charset CHARSET = Charset.forName("UTF-8");
    private static final char NEW_LINE = '\n';
    private static final char FILE_SEPARATOR = File.separatorChar;
    private static final String SFTP_PATTERN_FORM = "sftp -i <identity_file> -P <port> <user>@<host>";
    private static final int SFTP_PATTERN_LENGTH = 6;
    public static final String SPACE_STRING = " ";
    public static final String OPTION_I_STRING = "-i";
    public static final String OPTION_P_STRING = "-P";
    public static final String AT_SIGN_STRING = "@";
    private static final String SPLIT_STRING = "[\\s]";
    private static final Pattern USER_HOST_PATTERN = Pattern.compile("[\\w]+@[\\w\\.-]+");
    private static final Pattern PORT_PATTERN = Pattern.compile("[\\d]+");
    private static final Pattern IDENTITY_PATTERN = Pattern.compile("[\\w/\\\\\\.~-]+");
    private static final Map<String, Pattern> PATTERN_OPTIONS = new HashMap<>();
    private static final String CURRENT_DIRECTORY_DOT = ".";
    private static final String CURRENT_PARENT_DIRECTORY_DOT = "..";
    private static final int FROM_BYTES_TO_MB = 1024 * 1024;
    private static final int NANO_TO_SECONDS = 1000000000;

    private String host;
    private String port;
    private Path pemFile;
    private char[] pemPrivateKey;
    private String loginUser;

    static {
        PATTERN_OPTIONS.put(OPTION_I_STRING, IDENTITY_PATTERN);
        PATTERN_OPTIONS.put(OPTION_P_STRING, PORT_PATTERN);
    }

    public static String checkProtocolPatternsCorrectness(String evaluate) {
        return (new SftpOperations()).checkProtocolPatterns(evaluate);
    }

    private SftpOperations() {
        super(OPERATIONS_PROTOCOL_SFTP);
        this.protocol = OPERATIONS_PROTOCOL_SFTP;
        printOperation();
    }

    public SftpOperations(String initialParameter) {
        super(initialParameter);
        String errorMessage = checkProtocolPatterns(this.initialParameter);
        if (!errorMessage.isEmpty()) {
            throw new OperationRuntimeException("Unable to parse SFTP constructing parameter. Passed parameter is '" +
                    initialParameter + "'. Error Message: " + errorMessage);
        }
        String[] parts = initialParameter.split(SPLIT_STRING);
        int i = 0;
        if (parts[i].equals(OPERATIONS_PROTOCOL_SFTP)) {
            this.protocol = OPERATIONS_PROTOCOL_SFTP;
            i++;
            try {
                setupOptions(parts, i);
                i += 2;
                setupOptions(parts, i);
                i += 2;
            } catch (Exception e) {
                throw new OperationRuntimeException("Unable to parse SFTP constructing parameter. Passed parameter is '" +
                        initialParameter + "'. The parameter should have following entries: " + SFTP_PATTERN_FORM + ". Possible Error: " + e.getMessage(), e);
            }
            String[] uh = parts[i].split(AT_SIGN_STRING);
            setLoginUser(uh[0]);
            setHost(uh[1]);
        } else {
            throw new OperationRuntimeException("Unable to parse SFTP constructing parameter. Passed parameter is '" +
                    initialParameter + "'. The parameter should have following entries: " + SFTP_PATTERN_FORM);
        }
        printOperation();
    }

    @Override
    public String checkProtocolPatterns(String evaluate) {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Starting method " + Thread.currentThread().getStackTrace()[1].getMethodName() + " - evaluate: " + evaluate);
        }

        if (!evaluate.startsWith(OPERATIONS_PROTOCOL_SFTP)) {
            return "SFTP pattern must contain the keyword 'sftp' at the start of the command: " + SFTP_PATTERN_FORM;
        }
        if (!evaluate.contains(OPTION_I_STRING)) {
            return "SFTP pattern must contain an '-i' keyword followed by the option (path of the PEM file): " + SFTP_PATTERN_FORM;
        }
        if (!evaluate.contains(OPTION_P_STRING)) {
            return "SFTP pattern must contain a '-P' keyword followed by the option (indicating the port to be accessed): " + SFTP_PATTERN_FORM;
        }
        if (!evaluate.contains(AT_SIGN_STRING)) {
            return "SFTP pattern must contain a '@' between the user and the host: " + SFTP_PATTERN_FORM;
        }

        String[] sp = evaluate.split(SPLIT_STRING);
        if (sp.length != SFTP_PATTERN_LENGTH) {
            return "Not valid SFTP pattern. Valid SFTP pattern has the form " + SFTP_PATTERN_FORM;
        }
        int nextInt1;
        int nextInt2;
        if (checkOption(sp[(nextInt1 = getNextValidValue(1, sp))], sp[(nextInt2 = getNextValidValue(nextInt1 + 1, sp))])) {
            if (checkOption(sp[(nextInt1 = getNextValidValue(nextInt2 + 1, sp))], sp[(nextInt2 = getNextValidValue(nextInt1 + 1, sp))])) {
                if (USER_HOST_PATTERN.matcher(sp[(nextInt1 = getNextValidValue(nextInt2 + 1, sp))]).matches()) {
                    return EMPTY_STRING;
                } else {
                    return "User and host entry (" + sp[nextInt1] + ") doesn't match regular expression " + USER_HOST_PATTERN;
                }
            } else {
                return "Invalid option (" + sp[nextInt1] + "): '" + sp[nextInt2] + "'. Regex for the option is " + PATTERN_OPTIONS.get(sp[nextInt1]);
            }
        } else {
            return "Invalid option (" + sp[nextInt1] + "): '" + sp[nextInt2] + "'. Regex for the option is " + PATTERN_OPTIONS.get(sp[nextInt1]);
        }
    }

    private int getNextValidValue(int i, String[] split) {
        String value = split[i];
        while (value.equals(EMPTY_STRING)) {
            i = i + 1;
            value = split[i];
        }
        return i;
    }

    private boolean checkOption(String key, String value) {
        switch (key) {
            case OPTION_I_STRING:
                if (!IDENTITY_PATTERN.matcher(value).matches()) {
                    return false;
                }
                break;
            case OPTION_P_STRING:
                if (!PORT_PATTERN.matcher(value).matches()) {
                    return false;
                }
                break;
            default:
                return false;
        }
        return true;
    }

    private void setupOptions(String[] parts, int i) throws SftpException {
        String option = parts[i];
        switch (option) {
            case OPTION_I_STRING:
                Path pf = Paths.get(parts[i + 1]);
                if (Files.exists(pf) && Files.isReadable(pf)) {
                    setPemFile(pf);
                    break;
                } else {
                    throw new SftpException("Error defining mandatory PEM file. The file doesn't exists or is not readable: " +
                            pf.toAbsolutePath());
                }
            case OPTION_P_STRING:
                String p = parts[i + 1];
                try {
                    Integer.parseInt(p);
                    setPort(p);
                    break;
                } catch (NumberFormatException e) {
                    throw new SftpException("Error defining mandatory port. Port is not a numeric character: " + p, e);
                }
            default:
                throw new SftpException("Error defining mandatory options. Option not recognized: " + option);
        }
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public void setPemFile(Path pemFile) throws SftpException {
        this.pemFile = pemFile;
        if (!Files.exists(pemFile)) {
            throw new SftpException("File not found: " + pemFile);
        }
        StringBuilder sb = new StringBuilder();
        List<String> pem;
        try {
            pem = Files.readAllLines(pemFile, CHARSET);
        } catch (IOException e) {
            throw new SftpException("Not able to extract text from PEM file: " + pemFile);
        }
        for (String s : pem) {
            sb.append(s).append(NEW_LINE);
        }
        pemPrivateKey = (sb.toString()).toCharArray();
    }

    public void setLoginUser(String loginUser) {
        this.loginUser = loginUser;
    }

    @Override
    public Set<String> sendMultipleStreams(String remoteDirectory, Map<String, InputStream> files) {
        return sendMultipleStreams(remoteDirectory, files, EMPTY_STRING);
    }

    @Override
    public Set<String> sendMultipleStreams(String remoteDirectory, Map<String, InputStream> files, String tag) {
        try {
            return sendMultiple(loginUser, files, remoteDirectory, tag);
        } catch (Exception t) {
            throw new OperationRuntimeException(tag + "Unable to send InputStreams to remote directory " + remoteDirectory + ": " + t.getMessage(), t);
        }

    }

    @Override
    public Set<String> sendMultiplePaths(String remoteDirectory, Set<Path> paths) {
        return sendMultiplePaths(remoteDirectory, paths, EMPTY_STRING);
    }

    @Override
    public Set<String> sendMultiplePaths(String remoteDirectory, Set<Path> paths, String tag) {
        Map<String, InputStream> files = new HashMap<>();
        for (Path path : paths) {
            InputStream inputStream;
            try {
                inputStream = new FileInputStream(path.toFile());
                files.put(path.getFileName().toString(), inputStream);
            } catch (FileNotFoundException e) {
                throw new OperationRuntimeException(tag + "Unable to send InputStreams to remote directory " + remoteDirectory + ": " + e.getMessage(), e);
            }
        }
        return sendMultipleStreams(remoteDirectory, files, tag);
    }

    @Override
    public boolean send(String remoteDirectory, String filename, InputStream inputStream) {
        return send(remoteDirectory, filename, inputStream, EMPTY_STRING);
    }

    @Override
    public boolean send(String remoteDirectory, String filename, InputStream inputStream, String tag) {
        try {
            return sendSingle(loginUser, filename, inputStream, remoteDirectory, tag);
        } catch (Exception t) {
            throw new OperationRuntimeException("Unable to copy InputStream to directory " + remoteDirectory + " and file " + filename + ": " + t.getMessage(), t);
        }
    }

    @Override
    public boolean send(String remoteDirectory, String filename, Path path) {
        return send(remoteDirectory, filename, path, EMPTY_STRING);
    }

    @Override
    public boolean send(String remoteDirectory, String filename, Path path, String tag) {
        try {
            InputStream inputStream = new FileInputStream(path.toFile());
            return sendSingle(loginUser, filename, inputStream, remoteDirectory, tag);
        } catch (FileNotFoundException e) {
            throw new OperationRuntimeException(tag + "Unable to open an InputStream for " + path.toAbsolutePath() + ": " + e.getMessage(), e);
        } catch (Throwable t) {
            throw new OperationRuntimeException(tag + "Unable to copy " + path + " to " + Paths.get(remoteDirectory, filename).toAbsolutePath() + ": " + t.getMessage(), t);
        }
    }

    @Override
    public boolean sendIfNotExists(String remoteDirectory, String filename, InputStream inputStream) {
        return !exists(remoteDirectory, filename) && sendSingle(loginUser, filename, inputStream, remoteDirectory, EMPTY_STRING);
    }

    @Override
    public boolean sendIfNotExists(String remoteDirectory, String filename, Path path) {
        return sendIfNotExists(remoteDirectory, filename, path, EMPTY_STRING);
    }

    @Override
    public boolean sendIfNotExists(String remoteDirectory, String filename, Path path, String tag) {
        try {
            InputStream inputStream = new FileInputStream(path.toFile());
            return !exists(remoteDirectory, filename) && sendSingle(loginUser, filename, inputStream, remoteDirectory, tag);
        } catch (FileNotFoundException e) {
            LOGGER.warn("Unable to open an InputStream for " + path.toAbsolutePath() + ": " + e.getMessage(), e);
            return false;
        }
    }

    @Override
    public long get(String remoteDirectory, String filename, @NotNull OutputStream outputStream) {
        try {
            return get(loginUser, filename, remoteDirectory, outputStream);
        } catch (Exception t) {
            throw new OperationRuntimeException("Unable to get file " + filename + " from remote directory " + remoteDirectory + ": " + t.getMessage(), t);
        }
    }

    @Override
    public long get(String remoteDirectory, String filename, @NotNull Path path) {
        try {
            OutputStream outputStream = new FileOutputStream(path.toFile());
            return get(loginUser, filename, remoteDirectory, outputStream);
        } catch (FileNotFoundException e) {
            throw new OperationRuntimeException("Unable to open an OutputStream for " + path.toAbsolutePath() + ": " + e.getMessage(), e);
        } catch (Exception t) {
            throw new OperationRuntimeException("Unable to get file " + filename + " from remote directory " + remoteDirectory + ": " + t.getMessage(), t);
        }
    }

    @Override
    public long getUnique(String remoteDirectory, String fileNameRegex, @NotNull OutputStream outputStream) {
        List<String> fileNames;
        try {
            fileNames = listRemoteDirectory(remoteDirectory);
        } catch (Exception e) {
            throw new OperationRuntimeException("Unable to list files (regex=" + fileNameRegex + ") on directory: " +
                    remoteDirectory + ": " + e.getMessage(), e);
        }
        Pattern pattern = Pattern.compile(fileNameRegex);
        String remoteFilename = null;
        for (String filename : fileNames) {
            if (pattern.matcher(filename).matches()) {
                remoteFilename = filename;
                break;
            }
        }
        if (remoteFilename != null) {
            return get(remoteDirectory, remoteFilename, outputStream);
        } else {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Unable to get file (regex=" + fileNameRegex + ") from directory: " +
                        remoteDirectory + ".");
            }
            return 0;
        }
    }

    @Override
    public long getUnique(String remoteDirectory, String fileNameRegex, @NotNull Path path) {
        List<String> fileNames;
        try {
            fileNames = listRemoteDirectory(remoteDirectory);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Files on " + remoteDirectory + ":\t" + fileNames);
            }
        } catch (Exception e) {
            throw new OperationRuntimeException("Unable to list files (regex=" + fileNameRegex + ") on directory: " +
                    remoteDirectory + ": " + e.getMessage(), e);
        }
        Pattern pattern = Pattern.compile(fileNameRegex);
        String remoteFilename = null;
        for (String filename : fileNames) {
            if (pattern.matcher(filename).matches()) {
                remoteFilename = filename;
                break;
            }
        }
        if (remoteFilename != null) {
            return get(remoteDirectory, remoteFilename, Paths.get(path.toString(), remoteFilename));
        } else {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Unable to get file (regex=" + fileNameRegex + ") from directory: " +
                        remoteDirectory + ".");
            }
            return 0;
        }
    }

    @Override
    public boolean deleteIfExists(String remoteDirectory, String filename) {
        return exists(remoteDirectory, filename) && delete(loginUser, filename, remoteDirectory);
    }

    @Override
    public boolean isConnected(String parameter) {
        List<String> files = listRemoteDirectory(parameter);
        if (files.size() > 0 && LOGGER.isDebugEnabled()) {
            for (String file : files) {
                LOGGER.debug("-> " + file);
            }
        }
        return true;
    }

    @Override
    public boolean delete(String remoteDirectory, String filename) {
        try {
            return delete(loginUser, filename, remoteDirectory);
        } catch (Exception t) {
            throw new OperationRuntimeException("Unable to delete file " + Paths.get(remoteDirectory, filename).toAbsolutePath() + ": " + t.getMessage(), t);
        }
    }

    @Override
    public List<String> listRemoteDirectory(String remoteDirectory) {
        try {
            return list(loginUser, remoteDirectory);
        } catch (Exception t) {
            throw new OperationRuntimeException("Unable to access directory " + Paths.get(remoteDirectory) + ": " + t.getMessage(), t);
        }
    }

    @Override
    public boolean exists(String remoteDirectory, String filename) {
        try {
            return exists(loginUser, remoteDirectory, filename);
        } catch (Exception t) {
            throw new OperationRuntimeException("Unable to access file " + Paths.get(remoteDirectory, filename).toAbsolutePath() + ": " + t.getMessage(), t);
        }
    }

    @Override
    public boolean mkdir(String remoteDirectory) {
        try {
            Path dir = Paths.get(remoteDirectory);
            Iterator<Path> pathIterator = dir.iterator();
            boolean ret = false;
            while (pathIterator.hasNext()) {
                ret = makeRemoteDirectory(loginUser, pathIterator.next().toString());
                if (!ret) {
                    return false;
                }
            }
            return ret;
        } catch (Exception t) {
            throw new OperationRuntimeException("Unable to create file " + Paths.get(remoteDirectory) + ": " + t.getMessage(), t);
        }
    }

    @Override
    public String getOperationPatternForm() {
        return SFTP_PATTERN_FORM;
    }

    @Override
    public String getOperationsInfo(String parameter) {
        return this.initialParameter;
    }

    @SuppressWarnings("unchecked")
    private Set<String> sendMultiple(final String username, final Map<String, InputStream> files, final String directory,
                                     final String tag) {
        return (Set<String>) execute(username, client -> {
            Set<String> fileNames = files.keySet();
            Set<String> fileNamesSend = new HashSet<>();
            InputStream inputStream;
            String subTag = "[M] ";
            for (String filename : fileNames) {
                inputStream = files.get(filename);
                if (LOGGER.isInfoEnabled()) {
                    LOGGER.info(tag + subTag + "[" + filename + "] Sending/creating file: " + username + "@" +
                                host + ":" + directory + FILE_SEPARATOR + filename);
                }
                try {
                    send(client, directory, filename, inputStream, tag, subTag);
                    fileNamesSend.add(filename);
                } catch (IOException e) {
                    LOGGER.warn(tag + subTag + "[" + filename + "] Unable to send/create file: " + username + "@" +
                                host + ":" + directory + FILE_SEPARATOR + filename, e);
                }
            }
            fileNames.removeAll(fileNamesSend);
            return fileNames;
        });
    }

    private boolean sendSingle(final String username, final String filename, final InputStream inputStream, final String directory,
                               final String tag) {
        return (Boolean) execute(username, client -> {
            String subTag = "[S] ";
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info(tag + subTag + "[" + filename + "] Sending/creating file: " + username + "@" +
                            host + ":" + directory + FILE_SEPARATOR + filename);
            }
            try {
                send(client, directory, filename, inputStream, tag, subTag);
                return Boolean.TRUE;
            } catch(Throwable e) {
                LOGGER.warn(tag + subTag + "[" + filename + "] Unable to send/create file: " + username + "@" +
                            host + ":" + directory + FILE_SEPARATOR + filename, e);
                return Boolean.FALSE;
            }
        });
    }

    private void send(SFTPv3Client client, String directory, String filename, InputStream inputStream, String tag, String subTag) throws IOException {
        SFTPv3FileHandle handle = client.createFileTruncate(directory + FILE_SEPARATOR + filename);
        byte[] buffer = new byte[32768];
        int bytesRead = inputStream.read(buffer);
        int totalBytesRead = 0;
        int offset = 0;
        long start = System.nanoTime();
        while (bytesRead != -1) {
            client.write(handle, offset, buffer, 0, bytesRead);
            offset += bytesRead;
            totalBytesRead += bytesRead;
            bytesRead = inputStream.read(buffer);
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(tag + subTag + "[" + filename + "] Offset at end of write: " + offset);
            LOGGER.debug(tag + subTag + "[" + filename + "] Total written bytes at end of write:(OBS??) " + totalBytesRead);
        }
        client.closeFile(handle);
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info(tag + subTag + "[" + filename + "] File sent successfully. Size: " +
                    ((double) totalBytesRead / (double) FROM_BYTES_TO_MB) + " MB. Time elapsed: " +
                    ((System.nanoTime() - start) / NANO_TO_SECONDS) + " seconds");
        }
    }

    private long get(final String username, final String filename, final String directory, final OutputStream outputStream) {
        InputStream bufferedInput = new BufferedInputStream(new SFTPInputStream(username, directory, filename), 32768);
        OutputStream bufferedOutput = new BufferedOutputStream(outputStream);
        try {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Getting content from " + username + "@" + host + ":" + directory + FILE_SEPARATOR + filename);
            }
            //noinspection UnstableApiUsage
            return ByteStreams.copy(bufferedInput, bufferedOutput);
        } catch (Exception e) {
            throw new OperationRuntimeException("Unable to send file " + filename + " to remote filename " + directory +
                    ": " + e.getMessage(), e);
        } finally {
            try {
                bufferedInput.close();
                bufferedOutput.flush();
                bufferedOutput.close();
                outputStream.close();
            } catch (IOException e) {
                LOGGER.warn("Error when closing input- and outputStreams in operation 'get'.", e);
            }
        }

    }

    private boolean delete(final String username, final String filename, final String directory) {
        return (Boolean) execute(username, (SFTPConnectionCallback<Boolean>) client -> {
            LOGGER.info("Deleting file " + username + "@" + host + ":" + directory + FILE_SEPARATOR + filename);
            List<SFTPv3DirectoryEntry> list = client.ls(directory);
            for (SFTPv3DirectoryEntry entry : list) {
                if (entry.filename.equals(filename)) {
                    client.rm(filename);
                }
            }
            list = client.ls(directory);
            for (SFTPv3DirectoryEntry entry : list) {
                if (entry.filename.equals(filename)) {
                    return false;
                }
            }
            return true;
        });
    }

    private boolean makeRemoteDirectory(final String username, final String directory) {
        return (Boolean) execute(username, (SFTPConnectionCallback<Boolean>) client -> {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("creating file " + username + "@" + host + ":" + directory);
            }
            client.mkdir(directory, 700);
            List<SFTPv3DirectoryEntry> list = client.ls(directory);
            return list != null;
        });
    }

    @Override
    public boolean exists(final String username, final String directory, final String filename) {
        return exists(username, directory, filename, EMPTY_STRING);
    }

    @Override
    public boolean exists(final String username, final String directory, final String filename, final String tag) {
        return (Boolean) execute(username, client -> {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(tag + "Check exists " + username + "@" + host + ":" + directory + FILE_SEPARATOR + filename);
            }
            List<SFTPv3DirectoryEntry> list = client.ls(directory);
            for (final Object entry : list) {
                if (filename.equals(((SFTPv3DirectoryEntry) entry).filename)) {
                    return Boolean.TRUE;
                }
            }
            return Boolean.FALSE;
        });
    }

    @SuppressWarnings("unchecked")
    private List<String> list(final String username, final String directory) {
        return (List<String>) execute(username, client -> {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Listing " + username + "@" + host + ":" + directory);
            }
            List<SFTPv3DirectoryEntry>  lsList = client.ls(directory);
            List<String> list = new ArrayList<>();
            for (final Object aVector : lsList) {
                SFTPv3DirectoryEntry entry = (SFTPv3DirectoryEntry) aVector;
                if (!entry.filename.equals(CURRENT_DIRECTORY_DOT) && !entry.filename.equals(CURRENT_PARENT_DIRECTORY_DOT)) {
                    list.add(entry.filename);
                }
            }
            return list;
        });
    }


    /**
     * Handle all connectivity stuff with SFTP server and hand control to callback object.
     *
     * @param username               the username for login into the OS
     * @param sftpConnectionCallback Call back entity
     */
    private Object execute(String username, SFTPConnectionCallback sftpConnectionCallback) {

        Connection connection;
        SFTPv3Client client;
        connection = createConnection(username);
        client = createClient(connection);
        try {
            // Execute operations
            return sftpConnectionCallback.doInConnection(client);
        } catch (Exception e) {
            throw new OperationRuntimeException("Error executing SFTP call: " + e.getMessage(), e);
        } finally {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Terminating session for " + username + " on " + host + ":" + port);
            }
            closeClient(client);
            closeConnection(connection);
        }
    }

    private void closeConnection(Connection connection) {
        if (connection != null) {
            connection.close();
        }
    }

    private void closeClient(SFTPv3Client client) {
        if (client != null) {
            client.close();
        }
    }

    private SFTPv3Client createClient(Connection connection) {
        try {
            return new SFTPv3Client(connection);
//            return new SFTPv3Client(connection, new LoggerPrintStream());
        } catch (Exception e) {
            throw new SftpRuntimeConnectionException("Unable to create a SFTP Client on host:port=" + host + ":" + port + ". Possible reason: " + e.getMessage());
        }
    }

    private Connection createConnection(String username) {

        // Open connection
        Connection connection = new Connection(host, Integer.valueOf(port));
        try {
            connection.connect(null, 1000 * 15, 1000 * 30);
        } catch (Exception e) {
            throw new SftpRuntimeConnectionException("Unable to connect '" + username + "' to host:port=" + host + ":" + port + ". Possible reason: " + e.getMessage());
        }

        // Authenticate
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Logging in user " + username + " on " + host + ":" + port);
        }
        boolean authenticated;
        try {
            authenticated = connection.authenticateWithPublicKey(username, pemPrivateKey, "");
        } catch (IOException e) {
            throw new SftpRuntimeConnectionException("Unable to authenticate with username " + username + " and "
                    + "pemFile=" + pemFile + " on " + host + ":" + port, e);
        }
        if (!authenticated) {
            closeConnection(connection);
            throw new SftpRuntimeConnectionException("Authentication failed for " + username + " on " + host + ":" + port +
                    " using pemFile " + pemFile);
        }

        return connection;
    }

    /**
     * @author extljk
     */
    interface SFTPConnectionCallback<T> {
        T doInConnection(SFTPv3Client client) throws IOException;
    }

    /**
     * @author extljk
     */
    class SFTPInputStream extends InputStream {

        private final Connection connection;
        private final SFTPv3Client client;
        private final SFTPv3FileHandle handle;
        private long fileOffset = 0;

        SFTPInputStream(String username, String directory, String filename) {
            try {
                this.connection = createConnection(username);
                this.client = createClient(connection);
                this.handle = client.openFileRO(directory + FILE_SEPARATOR + filename);
            } catch (IOException e) {
                throw new OperationRuntimeException("Unable to create a SFTPInputStream: " + e.getMessage(), e);
            }
        }


        @Override
        public void close() {
            try {
                if (!handle.isClosed()) {
                    client.closeFile(handle);
                }
                closeClient(client);
                closeConnection(connection);
            } catch (IOException e) {
                throw new OperationRuntimeException("Unable to close the SFTPInputStream: " + e.getMessage(), e);
            }
        }

        @Override
        public int read() {
            throw new UnsupportedOperationException("Reading of single byte not supported. Use byte array.");
        }

        @Override
        public int read(@org.jetbrains.annotations.NotNull byte[] buffer, int bufferOffset, int bufferLength) {
            try {
                int numBytesRead = client.read(handle, fileOffset, buffer, bufferOffset, bufferLength);
                fileOffset += numBytesRead;
                return numBytesRead;
            } catch (IOException e) {
                close();
                throw new OperationRuntimeException("Error reading the SFTPInputStream: " + e.getMessage(), e);
            }
        }

    }

    @Override
    public String toString() {
        return "SftpOperations{" +
                "host='" + host + '\'' +
                ", port='" + port + '\'' +
                ", pemFile=" + pemFile +
                ", loginUser='" + loginUser + '\'' +
                '}';
    }


}
