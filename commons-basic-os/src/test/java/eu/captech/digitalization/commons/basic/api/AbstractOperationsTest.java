package eu.captech.digitalization.commons.basic.api;

import eu.captech.digitalization.commons.basic.doc.Preamble;
import eu.captech.digitalization.commons.basic.file.operations.sftp.SftpOperations;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Preamble(
        lastModifiedBy = "Eduardo Melgar",
        creationDate = "10/30/12",
        creationTime = "12:46 PM",
        lastModified = "10/30/12"
)
public class AbstractOperationsTest extends Assert {
    protected static final boolean IDEA = false;
    private static String WINDOWS_OS = "Windows";
    protected static final char SPACE_CHAR = ' ';
    protected Logger logger = LoggerFactory.getLogger(this.getClass());
    protected String remoteDirectory = ".";
    private String remoteFileName = "testFile.xml";
    private String localFileName = "pom.xml";
    protected IFileSystemOperations operations;
    private String os;

    @Before
    public void setUp() throws Exception {
        os = System.getProperty("os.name");
        logger = LoggerFactory.getLogger(this.getClass());
        logger.info("****************************************************************");
        logger.info("Starting preMethodSetup for " + this.getClass().getSimpleName());
        if (!Files.exists(Paths.get("target"))) {
            Files.createDirectories(Paths.get("target"));
        }
        if (os.startsWith(WINDOWS_OS)) {
            System.gc();
        }
    }

    @After
    public void tearDown() {
        logger.info("Finishing postMethodSetup for " + this.getClass().getSimpleName());
    }

    public void testSendPath() throws FileNotFoundException {
        logger.info("Starting method " + Thread.currentThread().getStackTrace()[1].getMethodName());
        if (operations instanceof SftpOperations && os.startsWith(WINDOWS_OS)) {
            return;
        }
        assertTrue("File " + localFileName + " doesn't exists.", Files.exists(Paths.get(localFileName)));
        sendPathInternally();
        assertTrue("Error deleting file " + remoteFileName + " from remote location.", deleteOperation());
        if (logger.isDebugEnabled()) {
            logger.debug("File " + Paths.get(remoteDirectory, remoteFileName).toAbsolutePath() + " send.");
        }
    }

    public void testSend() throws FileNotFoundException {
        logger.info("Starting method " + Thread.currentThread().getStackTrace()[1].getMethodName());
        if (operations instanceof SftpOperations && os.startsWith(WINDOWS_OS)) {
            return;
        }
        assertTrue("File " + localFileName + " doesn't exists.", Files.exists(Paths.get(localFileName)));
        if (existsOperation()) {
            assertTrue("Error deleting file " + remoteFileName + " from remote location.", deleteOperation());
        }
        sendFileInternally();
        assertTrue("Error deleting file " + remoteFileName + " from remote location.", deleteOperation());
        if (logger.isDebugEnabled()) {
            logger.debug("File " + Paths.get(remoteDirectory, remoteFileName).toAbsolutePath() + " send.");
        }
    }

    public void testGet() throws IOException {
        logger.info("Starting method " + Thread.currentThread().getStackTrace()[1].getMethodName());
        if (operations instanceof SftpOperations && os.startsWith(WINDOWS_OS)) {
            return;
        }
        long count;
        String localFile = "target/" + remoteFileName;
        if (!existsOperation()) {
            sendFileInternally();
        }
        count = getOutputStreamOperation(localFile);
        assertEquals("Error retrieving file " + remoteFileName + " from remote location.", (new FileInputStream(localFile)).available(), count);
        if (!os.startsWith(WINDOWS_OS)) {
            boolean deleted = deleteOperation();
            assertTrue("Error deleting file " + remoteFileName + " from remote location.", deleted);
        }
//        if (logger.isDebugEnabled()) {
//            logger.debug("File " + Paths.get(remoteDirectory, remoteFileName).toAbsolutePath() + " retrieved.");
//        }
    }

    public void testDelete() throws FileNotFoundException {
        logger.info("Starting method " + Thread.currentThread().getStackTrace()[1].getMethodName());
        if (operations instanceof SftpOperations && os.startsWith(WINDOWS_OS)) {
            return;
        }
        if (!existsOperation()) {
            sendFileInternally();
        }
        assertTrue("Error deleting file " + remoteFileName + " from remote location.", deleteOperation());
        assertFalse("File " + remoteFileName + " from remote location should be removed in a previous task.", deleteIfExistsOperation());
        if (logger.isDebugEnabled()) {
            logger.debug("File " + Paths.get(remoteDirectory, remoteFileName).toAbsolutePath() + " removed.");
        }
    }

    public void testDeleteIfExists() throws FileNotFoundException {
        logger.info("Starting method " + Thread.currentThread().getStackTrace()[1].getMethodName());
        if (operations instanceof SftpOperations && os.startsWith(WINDOWS_OS)) {
            return;
        }
        if (!existsOperation()) {
            sendFileInternally();
        }
        assertTrue("Error deleting file " + remoteFileName + " from remote location.", deleteIfExistsOperation());
        assertFalse("File " + remoteFileName + " from remote location should be removed in a previous task.", deleteIfExistsOperation());
    }

    public void testListRemoteDirectory() {
        logger.info("Starting method " + Thread.currentThread().getStackTrace()[1].getMethodName());
        if (operations instanceof SftpOperations && os.startsWith(WINDOWS_OS)) {
            return;
        }
        String listing = "Listing remote entry:\t";
        List<String> files = listRemoteDirectoryOperation();
        for (String s : files) {
            logger.debug(listing + s);
        }
    }

    private void sendPathInternally() throws FileNotFoundException {
        Path source = Paths.get(localFileName);
        assertTrue("Error sending file " + remoteFileName + " to remote destination: not able to send.", sendPathOperation(source));
        assertTrue("Error sending file " + remoteFileName + " to remote destination: files not found remotely.", existsOperation());
    }

    private void sendFileInternally() throws FileNotFoundException {
        InputStream inputStream = new FileInputStream(localFileName);
        assertNotNull("InputStream not loaded!", inputStream);
        assertTrue("Error sending file " + remoteFileName + " to remote destination: not able to send.", sendInputStreamOperation(inputStream));
        assertTrue("Error sending file " + remoteFileName + " to remote destination: files not found remotely.", existsOperation());
    }

    protected List<String> listRemoteDirectoryOperation() {
        if (operations instanceof SftpOperations && os.startsWith(WINDOWS_OS)) {
            return new ArrayList<>();
        }
        return operations.listRemoteDirectory(remoteDirectory);
    }

    protected boolean deleteOperation() {
        return operations instanceof SftpOperations && os.startsWith(WINDOWS_OS) || operations.delete(remoteDirectory, remoteFileName);
    }

    protected boolean existsOperation() {
        return operations instanceof SftpOperations && os.startsWith(WINDOWS_OS) || operations.exists(remoteDirectory, remoteFileName);
    }

    protected boolean sendPathOperation(Path path) {
        return operations instanceof SftpOperations && os.startsWith(WINDOWS_OS) || operations.send(remoteDirectory, remoteFileName, path);
    }

    protected boolean sendInputStreamOperation(InputStream inputStream) {
        return operations instanceof SftpOperations && os.startsWith(WINDOWS_OS) || operations.send(remoteDirectory, remoteFileName, inputStream);
    }

    protected long getOutputStreamOperation(String localFile) throws FileNotFoundException {
        if (operations instanceof SftpOperations && os.startsWith(WINDOWS_OS)) {
            return 0;
        }
        return operations.get(remoteDirectory, remoteFileName, new FileOutputStream(localFile));
    }

    protected boolean deleteIfExistsOperation() {
        return operations instanceof SftpOperations && os.startsWith(WINDOWS_OS) || operations.deleteIfExists(remoteDirectory, remoteFileName);
    }
}
