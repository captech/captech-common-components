package eu.captech.digitalization.commons.basic.file;

import eu.captech.digitalization.commons.basic.BasicOsCommonsTest;
import eu.captech.digitalization.commons.basic.exception.ExecutionException;
import eu.captech.digitalization.commons.basic.files.io.PathOperations;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FileDirectoryWatcherTest extends BasicOsCommonsTest{
    private static final String ORIGINAL_IMAGE_NAME = "test.tif";
    private static final String ORIGINAL_IMAGE_FOLDER = "TIFF";
    private static final String SOURCE_FOLDER_NAME = "sourceFolderName";
    private static final String WORKING_FOLDER_NAME = "workingFolderName";
    private static final List<Path> SOURCES = new ArrayList<>();
    private static final String TIF_REGEX = "(.*.tif$)";
    private PathOperations pathOperations;
    private Path workingPath;

    @Before
    public void setUp() throws Exception{
        logger = getLoggerFor(this.getClass());
        super.preMethodSetup();
        pathOperations = new PathOperations();
        Path sourcePath = Paths.get(TARGET.toString(), SOURCE_FOLDER_NAME);
        Files.deleteIfExists(sourcePath);
        pathOperations.createDirectory(sourcePath);
        SOURCES.add(sourcePath);
        workingPath = Paths.get(TARGET.toString(), WORKING_FOLDER_NAME);
        pathOperations.createDirectory(workingPath);
    }

    @Test
    public void moveFile() throws IOException, ExecutionException, InterruptedException{
        boolean running = true;
        List<Path> copiedPaths = new ArrayList<>();
        FileDirectoryWatcher fileDirectoryWatcher = new FileDirectoryWatcher(SOURCES, new MoveFile(workingPath, null), new RegexPathPredicate(TIF_REGEX), false);
        fileDirectoryWatcher.start();
        for ( Path sourcePath : SOURCES) {
            pathOperations.copyToDirectoryWithTempBuffer(sourcePath, ORIGINAL_IMAGE_NAME, Paths.get(testResources.toString(),
                                                                                                    ORIGINAL_IMAGE_FOLDER,
                                                                                                    ORIGINAL_IMAGE_NAME));
            Assert.assertTrue(Files.exists(Paths.get(sourcePath.toString(), ORIGINAL_IMAGE_NAME)));
            copiedPaths.add(Paths.get(sourcePath.toString(), ORIGINAL_IMAGE_NAME));
        }
        while (running) {
            for (Path sourcePath : SOURCES) {
                Thread.sleep(1000L);
                if (pathOperations.contains(sourcePath, TIF_REGEX)) {
                    break;
                }
                running = false;
            }
        }
        for (Path copiedPath : copiedPaths) {
            Assert.assertFalse(Files.exists(copiedPath));
        }
        fileDirectoryWatcher.destroy();
    }

    @After
    public void tearDown() {
        super.postMethodSetup();
    }
}