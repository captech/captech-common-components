package eu.captech.digitalization.commons.basic.zip;

import eu.captech.digitalization.commons.basic.BasicCommonsTest;
import eu.captech.digitalization.commons.basic.doc.Preamble;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

@Preamble(
        lastModifiedBy = "Eduardo Melgar",
        creationDate = "1/10/13",
        creationTime = "12:51 PM",
        lastModified = "1/10/13"
)
public class ZipTest extends BasicCommonsTest {
    private static final Path TARGET = Paths.get("target");
    private static final Path ZIP_PATH = Paths.get("test.zip");
    private static Path SRC_DIR = Paths.get("src");
    private Zip zip;

    @Before
    public void setUp()
            throws Exception {
        logger = getLoggerFor(this.getClass());
        super.preMethodSetup();
        zip = new Zip();
    }

    @After
    public void tearDown() {
        super.postMethodSetup();
    }

    @Test
    public void testPackUnpack() throws Exception {
        long start = System.currentTimeMillis();
        Path zipPath = Paths.get(TARGET.toString(), ZIP_PATH.toString());
        if (logger.isInfoEnabled()) {
            logger.info("SRC_DIR: " + SRC_DIR);
            logger.info("zipPath: " + zipPath);
        }
        zip.pack(SRC_DIR, zipPath, true);
        zip.unpack(zipPath, TARGET);
        if (logger.isInfoEnabled()) {
            logger.info("Used " + (System.currentTimeMillis() - start) + " milliseconds on Zip/Unzip task");
        }
    }
}
