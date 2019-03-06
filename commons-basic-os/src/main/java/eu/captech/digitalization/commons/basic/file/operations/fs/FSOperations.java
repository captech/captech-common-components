package eu.captech.digitalization.commons.basic.file.operations.fs;

import eu.captech.digitalization.commons.basic.files.io.PathOperations;
import eu.captech.digitalization.commons.basic.api.AbstractFileSystemOperations;
import eu.captech.digitalization.commons.basic.doc.Preamble;
import eu.captech.digitalization.commons.basic.exception.ExecutionException;
import eu.captech.digitalization.commons.basic.exceptions.OperationRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

@Preamble(
        lastModifiedBy = "Eduardo Melgar",
        creationDate = "10/24/12",
        creationTime = "10:19 AM",
        lastModified = "10/24/12"
)
public class FSOperations extends AbstractFileSystemOperations {

    private static final Logger LOGGER = LoggerFactory.getLogger(FSOperations.class);
    private CopyOption[] copyOptions = new CopyOption[]{REPLACE_EXISTING};
    private PathOperations pathUtils = new PathOperations();

    public static String checkProtocolPatternsCorrectness(String evaluate) {
        return (new FSOperations()).checkProtocolPatterns(evaluate);
    }

    private FSOperations() {
        super(OPERATIONS_PROTOCOL_FS);
        this.protocol = OPERATIONS_PROTOCOL_FS;
        printOperation();
    }

    public FSOperations(String initialParameter) {
        super(initialParameter);
        String errorMessage;
        if (!(errorMessage = checkProtocolPatterns(this.initialParameter)).isEmpty()) {
            throw new OperationRuntimeException("Unable to parse FS constructing parameter. Passed parameter is '" +
                    initialParameter + "'. Error Message: " + errorMessage);
        }
        this.protocol = OPERATIONS_PROTOCOL_FS;
        printOperation();
    }

    @Override
    public boolean mkdir(String remoteDirectory) {
        try {
            pathUtils.createDirectory(Paths.get(remoteDirectory));
            return Files.exists(Paths.get(remoteDirectory));
        } catch (Exception t) {
            throw new OperationRuntimeException("Unable to create file " + Paths.get(remoteDirectory) + ": " + t.getMessage(), t);
        }
    }

    @Override
    public String getOperationPatternForm() {
        return OPERATIONS_PROTOCOL_FS;
    }

    @Override
    public String getOperationsInfo(String parameter) {
        // Todo:
        return null;
    }

    @Override
    public Set<String> sendMultipleStreams(String remoteDirectory, Map<String, InputStream> files) {
        return sendMultipleStreams(remoteDirectory, files, EMPTY_STRING);
    }

    @Override
    public Set<String> sendMultipleStreams(String remoteDirectory, Map<String, InputStream> files, String tag) {
        Set<String> fileNames = files.keySet();
        Set<String> unSendFiles = new HashSet<>();
        for (String filename : fileNames) {
            if (!send(remoteDirectory, filename, files.get(filename))) {
                unSendFiles.add(filename);
            }
        }
        if (!unSendFiles.isEmpty()) {
            LOGGER.warn(tag + "Some files were not send: " + unSendFiles);
        }
        return unSendFiles;
    }

    @Override
    public Set<String> sendMultiplePaths(String remoteDirectory, Set<Path> files) {
        return sendMultiplePaths(remoteDirectory, files, EMPTY_STRING);
    }

    @Override
    public Set<String> sendMultiplePaths(String remoteDirectory, Set<Path> files, String tag) {
        Set<String> unSendFiles = new HashSet<>();
        for (Path path : files) {
            if (!send(remoteDirectory, path.getFileName().toString(), path)) {
                unSendFiles.add(path.getFileName().toString());
            }
        }
        if (!unSendFiles.isEmpty()) {
            LOGGER.warn(tag + "Some files were not send: " + unSendFiles);
        }
        return unSendFiles;
    }

    @Override
    public boolean send(String remoteDirectory, String filename, InputStream inputStream) {
        return send(remoteDirectory, filename, inputStream, EMPTY_STRING);
    }

    @Override
    public boolean send(String remoteDirectory, String filename, InputStream inputStream, String tag) {
        try {
            Path path = pathUtils.copyToDirectoryWithTempBuffer(Paths.get(remoteDirectory), filename, inputStream, REPLACE_EXISTING);
            return Files.exists(path);
        } catch (Exception e) {
            throw new OperationRuntimeException(tag + "Unable to copy InputStream to " + remoteDirectory + " with filename " + filename + ": " + e.getMessage(), e);
        }
    }

    @Override
    public boolean send(String remoteDirectory, String filename, Path source) {
        return send(remoteDirectory, filename, source, EMPTY_STRING);
    }

    @Override
    public boolean send(String remoteDirectory, String filename, Path source, String tag) {
        try {
            Path path = pathUtils.copyToDirectoryWithTempBuffer(Paths.get(remoteDirectory), filename, source, copyOptions);
            return Files.exists(path);
        } catch (Exception t) {
            throw new OperationRuntimeException(tag + "Unable to copy InputStream to " + remoteDirectory + " with filename " + filename + ": " + t.getMessage(), t);
        }
    }

    @Override
    public boolean sendIfNotExists(String remoteDirectory, String filename, InputStream inputStream) {
        return !exists(remoteDirectory, filename) && send(remoteDirectory, filename, inputStream);
    }

    @Override
    public boolean sendIfNotExists(String remoteDirectory, String filename, Path path) {
        return !exists(remoteDirectory, filename) && send(remoteDirectory, filename, path);
    }

    @Override
    public boolean sendIfNotExists(String remoteDirectory, String filename, Path path, String tag) {
        return !exists(remoteDirectory, filename) && send(remoteDirectory, filename, path, tag);
    }

    @Override
    public long get(String remoteDirectory, String filename, @NotNull OutputStream outputStream) {
        try {
            return Files.copy(Paths.get(remoteDirectory, filename), outputStream);
        } catch (Exception t) {
            throw new OperationRuntimeException("Unable to get file: " + Paths.get(remoteDirectory, filename).toAbsolutePath() + ": " + t.getMessage(), t);
        }
    }

    @Override
    public long get(String remoteDirectory, String filename, @NotNull Path directoryOutput) {
        try {
            Path path = pathUtils.copyToDirectoryWithTempBuffer(directoryOutput, filename, Paths.get(remoteDirectory, filename), copyOptions);
            try {
                return Files.size(path);
            } catch (IOException e) {
                throw new OperationRuntimeException("Unable to get size of existing file: " + Paths.get(remoteDirectory,
                        filename).toAbsolutePath() + ": " + e.getMessage(), e);
            }
        } catch (ExecutionException e) {
            throw new OperationRuntimeException("Unable to get existing file: " +
                    Paths.get(remoteDirectory, filename).toAbsolutePath() + ": " + e.getMessage(), e);
        } catch (Exception t) {
            throw new OperationRuntimeException("Unable to get file: " +
                    Paths.get(remoteDirectory, filename).toAbsolutePath() + ": " + t.getMessage(), t);
        }
    }

    @Override
    public long getUnique(String remoteDirectory, String fileNameRegex, @NotNull OutputStream outputStream) {
        Path[] paths;
        try {
            paths = pathUtils.listPaths(Paths.get(remoteDirectory), fileNameRegex);
        } catch (ExecutionException e) {
            throw new OperationRuntimeException("Unable to list files (regex=" + fileNameRegex + ") on directory: " +
                    remoteDirectory + ": " + e.getMessage(), e);
        }
        if (paths.length > 0) {
            return get(remoteDirectory, paths[0].getFileName().toString(), outputStream);
        } else {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Unable to get file (regex=" + fileNameRegex + ") from directory: " +
                        remoteDirectory + ". Not found.");
            }
            return 0;
        }
    }

    @Override
    public long getUnique(String remoteDirectory, String fileNameRegex, @NotNull Path path) {
        Path[] paths;
        try {
            paths = pathUtils.listPaths(Paths.get(remoteDirectory), fileNameRegex);
        } catch (ExecutionException e) {
            throw new OperationRuntimeException("Unable to list files (regex=" + fileNameRegex + ") on directory: " +
                    remoteDirectory + ": " + e.getMessage(), e);
        }
        if (paths.length > 0) {
            return get(remoteDirectory, paths[0].getFileName().toString(), path);
        } else {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Unable to get file (regex=" + fileNameRegex + ") from directory: " +
                        remoteDirectory + ".");
            }
            return 0;
        }
    }

    @Override
    public boolean delete(String remoteDirectory, String filename) {
        try {
            Files.delete(Paths.get(remoteDirectory, filename));
            return !exists(remoteDirectory, filename);
        } catch (IOException e) {
            LOGGER.warn("Unable to delete file: " + Paths.get(remoteDirectory, filename).toAbsolutePath() + ": " + e.getMessage(), e);
            return false;
        } catch (Exception t) {
            throw new OperationRuntimeException("Unable to delete file: " + Paths.get(remoteDirectory, filename).toAbsolutePath() + ": " + t.getMessage(), t);
        }
    }

    @Override
    public boolean deleteIfExists(String remoteDirectory, String filename) {
        try {
            return Files.deleteIfExists(Paths.get(remoteDirectory, filename));
        } catch (IOException e) {
            LOGGER.warn("Unable to delete existing file: " + Paths.get(remoteDirectory, filename).toAbsolutePath() + ": " + e.getMessage(), e);
            return false;
        } catch (Exception t) {
            throw new OperationRuntimeException("Unable to delete file: " + Paths.get(remoteDirectory, filename).toAbsolutePath() + ": " + t.getMessage(), t);
        }
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
    public String checkProtocolPatterns(String evaluate) {
        if (!evaluate.equals(OPERATIONS_PROTOCOL_FS)) {
            return "No valid protocol operation key: " + evaluate + ". " + FSOperations.class.getSimpleName() +
                    " supports only '" + OPERATIONS_PROTOCOL_FS + "' as protocol key";
        }
        return EMPTY_STRING;
    }

    @Override
    public List<String> listRemoteDirectory(String remoteDirectory) {
        Path[] paths;
        try {
            paths = pathUtils.listPaths(Paths.get(remoteDirectory));
        } catch (ExecutionException e) {
            throw new OperationRuntimeException("Unable to list Paths on " + Paths.get(remoteDirectory).toAbsolutePath() + ": " + e.getMessage(), e);
        }
        List<String> list = new ArrayList<>();
        for (Path path : paths) {
            list.add(path.getFileName().toString());
        }
        return list;
    }

    @Override
    public boolean exists(String remoteDirectory, String filename) {
        return exists(remoteDirectory, filename, EMPTY_STRING);
    }

    @Override
    public boolean exists(String remoteDirectory, String filename, String tag) {
        try {
            return Files.exists(Paths.get(remoteDirectory, filename));
        } catch (Exception t) {
            throw new OperationRuntimeException(tag + "Unable to access file: " + Paths.get(remoteDirectory, filename).toAbsolutePath() + ": " + t.getMessage(), t);
        }
    }

    @Override
    public boolean exists(String username, String directory, String filename, String tag) {
        throw new UnsupportedOperationException("Not supported");
    }

    public void setCopyOptions(CopyOption[] copyOptions) {
        this.copyOptions = copyOptions;
    }

    @Override
    public String toString() {
        return "FSOperations{" +
                "copyOptions=" + (copyOptions == null ? null : Arrays.asList(copyOptions)) +
                '}';
    }
}
