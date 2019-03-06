package eu.captech.digitalization.commons.basic.sftp;

import eu.captech.digitalization.commons.basic.api.AbstractOperationsTest;
import eu.captech.digitalization.commons.basic.doc.Preamble;
import eu.captech.digitalization.commons.basic.exceptions.OperationRuntimeException;
import eu.captech.digitalization.commons.basic.file.operations.sftp.SftpOperations;

import org.junit.Test;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;

import static eu.captech.digitalization.commons.basic.file.operations.sftp.SftpOperations.*;
import static java.net.InetAddress.getLocalHost;

@Preamble(
        lastModifiedBy = "Eduardo Melgar",
        creationDate = "10/22/12",
        creationTime = "2:20 PM",
        lastModified = "10/22/12"
)
public class SftpOperationsTest extends AbstractOperationsTest{
    private static final String ARTIFACT_NAME = "commons-basic-os";

    public void setUp() throws Exception {
        super.setUp();
        logger = LoggerFactory.getLogger(this.getClass());
        String host = getLocalHost().getHostName();
        String loginUser = System.getProperty("user.name");
        int port = 22;
        String idRsaPath = ".ssh/id_rsa";
        if (IDEA) {
            idRsaPath = Paths.get(ARTIFACT_NAME, idRsaPath).toString();
        }
        String initialParameter = OPERATIONS_PROTOCOL_SFTP + SPACE_CHAR + OPTION_I_STRING + SPACE_CHAR + idRsaPath +
                SPACE_CHAR + OPTION_P_STRING + SPACE_CHAR + port + SPACE_CHAR + loginUser + AT_SIGN_STRING + host;
        operations = new SftpOperations(initialParameter);
        if (logger.isInfoEnabled()) {
            logger.info("SftpOperations created: " + operations.toString());
        }
    }

    @Override
    @Test
    public void testSendPath() throws FileNotFoundException {
        try {
            super.testSendPath();
        }
        catch (OperationRuntimeException e) {
            if (!e.getMessage().startsWith("Unable to connect ")) {
                throw e;
            }
            else {
                logger.warn("Unable to connect via " + operations.toString());
            }
        }
    }

    @Override
    @Test
    public void testSend() throws FileNotFoundException {
        try {
            super.testSend();
        }
        catch (OperationRuntimeException e) {
            if (!e.getMessage().startsWith("Unable to connect ")) {
                throw e;
            }
            else {
                logger.warn("Unable to connect via " + operations.toString());
            }
        }
    }

    @Override
    @Test
    public void testGet() throws IOException {
        try {
            super.testGet();
        }
        catch (OperationRuntimeException e) {
            if (!e.getMessage().startsWith("Unable to connect ")) {
                throw e;
            }
            else {
                logger.warn("Unable to connect via " + operations.toString());
            }
        }
    }

    @Override
    @Test
    public void testDelete() throws FileNotFoundException {
        try {
            super.testDelete();
        }
        catch (OperationRuntimeException e) {
            if (!e.getMessage().startsWith("Unable to connect ")) {
                throw e;
            }
            else {
                logger.warn("Unable to connect via " + operations.toString());
            }
        }
    }

    @Override
    @Test
    public void testDeleteIfExists() throws FileNotFoundException {
        try {
            super.testDeleteIfExists();
        }
        catch (OperationRuntimeException e) {
            if (!e.getMessage().startsWith("Unable to connect ")) {
                throw e;
            }
            else {
                logger.warn("Unable to connect via " + operations.toString());
            }
        }
    }

    @Override
    @Test
    public void testListRemoteDirectory() {
        try {
            super.testListRemoteDirectory();
        }
        catch (OperationRuntimeException e) {
            if (!e.getMessage().startsWith("Unable to connect ")) {
                throw e;
            }
            else {
                logger.warn("Unable to connect via " + operations.toString());
            }
        }
    }

    // Todo: Make this work!
//    @Test
//    public void testMkDir() {
//        try {
//            operations.mkdir("target/tBANCSE/FakturaOutbound");
//        }
//        catch (OperationRuntimeException e) {
//            if (!e.getMessage().startsWith("Unable to connect ")) {
//                throw e;
//            }
//            else {
//                logger.warn("Unable to connect via " + operations.toString());
//            }
//        }
//    }
}
