package eu.captech.digitalization.commons.basic.fs;

import eu.captech.digitalization.commons.basic.api.AbstractOperationsTest;
import eu.captech.digitalization.commons.basic.doc.Preamble;
import eu.captech.digitalization.commons.basic.file.operations.fs.FSOperations;

import org.junit.Test;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Paths;

import static eu.captech.digitalization.commons.basic.api.IOperations.OPERATIONS_PROTOCOL_FS;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

@Preamble(
        lastModifiedBy = "Eduardo Melgar",
        creationDate = "10/24/12",
        creationTime = "10:49 AM",
        lastModified = "10/24/12"
)
public class FSOperationsTest extends AbstractOperationsTest{

    public void setUp() throws Exception {
        super.setUp();
        logger = LoggerFactory.getLogger(this.getClass());
        remoteDirectory = "target";
        if (!Files.exists(Paths.get(remoteDirectory))) {
            Files.createDirectories(Paths.get(remoteDirectory));
        }
        operations = new FSOperations(OPERATIONS_PROTOCOL_FS);
        CopyOption[] copyOptions = new CopyOption[]{REPLACE_EXISTING};
        ((FSOperations) operations).setCopyOptions(copyOptions);
        if (logger.isInfoEnabled()) {
            logger.info("FSOperations created: " + operations.toString());
        }
    }

    @Override
    @Test
    public void testSendPath() throws FileNotFoundException {
        super.testSendPath();
    }

    @Override
    @Test
    public void testSend() throws FileNotFoundException {
        super.testSend();
    }

    @Override
    @Test
    public void testGet() throws IOException {
        super.testGet();
    }

    @Override
    @Test
    public void testDelete() throws FileNotFoundException {
        super.testDelete();
    }

    @Override
    @Test
    public void testDeleteIfExists() throws FileNotFoundException {
        super.testDeleteIfExists();
    }

    @Override
    @Test
    public void testListRemoteDirectory() {
        super.testListRemoteDirectory();
    }
}
