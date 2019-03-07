package eu.captech.digitalization.commons.basic.files.io;

import eu.captech.digitalization.commons.basic.BasicCommonsTest;
import eu.captech.digitalization.commons.basic.exception.ExecutionException;
import eu.captech.digitalization.commons.basic.exception.FileException;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.apache.commons.io.FileUtils.touch;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@Ignore
public class PathOperationsTest extends BasicCommonsTest{

    private static final PathOperations PATH_OPERATIONS = new PathOperations();

    @Before
    public void setUp() throws Exception{
        logger = getLoggerFor(this.getClass());
        super.preMethodSetup();
    }

    @After
    public void tearDown() {
        super.postMethodSetup();
    }

    @Test
    public void testCreateDirectory() throws Exception{
        logger.info("****** " + Thread.currentThread().getStackTrace()[1].getMethodName() + " :: Starting method.");
        Path nova = Paths.get(TARGET.toString(), "nova");
        createDirectory(nova);
        assertTrue("Path " + nova + " should exists", Files.exists(nova));
        assertTrue("Path " + nova + " should be a directory", Files.isDirectory(nova));
        deleteDirectory(nova);
        logger.info("****** " + Thread.currentThread().getStackTrace()[1].getMethodName() + " :: Method done.");
    }

    @Test (expected = ExecutionException.class)
    public void testCreateDirectoryWithException() throws Exception{
        try {
            logger.info("****** " + Thread.currentThread().getStackTrace()[1].getMethodName() + " :: Starting method.");
            Path nova = Paths.get("/", "nova");
            createDirectory(nova);
        }
        finally {
            logger.info("Thrown expected " + ExecutionException.class.getName());
            logger.info("****** " + Thread.currentThread().getStackTrace()[1].getMethodName() + " :: Method done.");
        }
    }

    @Test
    public void testCreateExistingDirectory() throws Exception{
        logger.info("****** " + Thread.currentThread().getStackTrace()[1].getMethodName() + " :: Starting method.");
        Path nova = Paths.get(TARGET.toString(), "nonExisting");
        createDirectory(nova);
        assertTrue("Path " + nova + " should exists", Files.exists(nova));
        assertTrue("Path " + nova + " should be a directory", Files.isDirectory(nova));
        logger.info("Creating directory " + nova);
        PATH_OPERATIONS.createDirectory(nova);
        assertTrue("Path " + nova + " should exists", Files.exists(nova));
        assertTrue("Path " + nova + " should be a directory", Files.isDirectory(nova));
        deleteDirectory(nova);
        logger.info("****** " + Thread.currentThread().getStackTrace()[1].getMethodName() + " :: Method done.");
    }

    @Test (expected = FileException.class)
    public void testDeleteDirsNullFalse() throws Exception{
        try {
            logger.info("****** " + Thread.currentThread().getStackTrace()[1].getMethodName() + " :: Starting method.");
            PATH_OPERATIONS.deleteDir(null, false);
        }
        finally {
            logger.info("Thrown expected " + FileException.class.getName());
            logger.info("****** " + Thread.currentThread().getStackTrace()[1].getMethodName() + " :: Method done.");
        }
    }

    @Test (expected = FileException.class)
    public void testDeleteDirsNullTrue() throws Exception{
        try {
            logger.info("****** " + Thread.currentThread().getStackTrace()[1].getMethodName() + " :: Starting method.");
            PATH_OPERATIONS.deleteDir(null, true);
        }
        finally {
            logger.info("Thrown expected " + FileException.class.getName());
            logger.info("****** " + Thread.currentThread().getStackTrace()[1].getMethodName() + " :: Method done.");
        }
    }

    @Test (expected = FileException.class)
    public void testDeleteNonExistingDirFalse() throws Exception{
        try {
            logger.info("****** " + Thread.currentThread().getStackTrace()[1].getMethodName() + " :: Starting method.");
            Path nova = Paths.get(TARGET.toString(), "nonExisting");
            assertFalse("Path " + nova + " should not exists", Files.exists(nova));
            logger.info("Trying to delete non existing directory " + nova + " recursively, excluding the parent folder.");
            PATH_OPERATIONS.deleteDir(nova, false);
        }
        finally {
            logger.info("Thrown expected " + FileException.class.getName());
            logger.info("****** " + Thread.currentThread().getStackTrace()[1].getMethodName() + " :: Method done.");
        }
    }

    @Test (expected = FileException.class)
    public void testDeleteNonExistingDirFalse_Secured() throws Exception{
        try {
            logger.info("****** " + Thread.currentThread().getStackTrace()[1].getMethodName() + " :: Starting method.");
            Path nova = Paths.get("/root");
            assertTrue("Path " + nova + " should exists", Files.exists(nova));
            logger.info("Trying to delete non existing directory " + nova + " recursively, excluding the parent folder.");
            PATH_OPERATIONS.deleteDir(nova, false);
        }
        finally {
            logger.info("Thrown expected " + FileException.class.getName());
            logger.info("****** " + Thread.currentThread().getStackTrace()[1].getMethodName() + " :: Method done.");
        }
    }

    @Test
    public void testDeleteNonExistingDirTrue() throws Exception{
        logger.info("****** " + Thread.currentThread().getStackTrace()[1].getMethodName() + " :: Starting method.");
        Path nova = Paths.get(TARGET.toString(), "nonExisting");
        assertFalse("Path " + nova + " should not exists", Files.exists(nova));
        logger.info("Trying to delete non existing directory " + nova + " recursively, including the parent folder.");
        PATH_OPERATIONS.deleteDir(nova, true);
        assertFalse("Path " + nova + " should not exists", Files.exists(nova));
        touch(nova.toFile());
        assertTrue("Path " + nova + " should exists", Files.exists(nova));
        logger.info("Trying to delete existing regular file " + nova);
        PATH_OPERATIONS.deleteDir(nova);
        assertTrue("Path " + nova + " should exists", Files.exists(nova));
        Files.deleteIfExists(nova);
        assertFalse("Path " + nova + " should not exists", Files.exists(nova));
        logger.info("****** " + Thread.currentThread().getStackTrace()[1].getMethodName() + " :: Method done.");
    }

    @Test (expected = FileException.class)
    public void testDeleteNullDir() throws Exception{
        try {
            logger.info("****** " + Thread.currentThread().getStackTrace()[1].getMethodName() + " :: Starting method.");
            deleteDirectory(null);
        }
        finally {
            logger.info("Thrown expected " + FileException.class.getName());
            logger.info("****** " + Thread.currentThread().getStackTrace()[1].getMethodName() + " :: Method done.");
        }
    }

    @Test
    public void testDeleteNonExistingDir() throws Exception{
        logger.info("****** " + Thread.currentThread().getStackTrace()[1].getMethodName() + " :: Starting method.");
        Path nova = Paths.get(TARGET.toString(), "nonExisting");
        assertFalse("Path " + nova + " should not exists", Files.exists(nova));
        logger.info("Trying to delete non existing file " + nova);
        PATH_OPERATIONS.deleteDir(nova);
        assertFalse("Path " + nova + " should not exists", Files.exists(nova));
        touch(nova.toFile());
        assertTrue("Path " + nova + " should exists", Files.exists(nova));
        logger.info("Trying to delete existing regular file " + nova);
        PATH_OPERATIONS.deleteDir(nova);
        assertTrue("Path " + nova + " should exists", Files.exists(nova));
        Files.deleteIfExists(nova);
        assertFalse("Path " + nova + " should not exists", Files.exists(nova));
        logger.info("****** " + Thread.currentThread().getStackTrace()[1].getMethodName() + " :: Method done.");
    }

    @Test (expected = FileException.class)
    public void testDeleteSecuredDir() throws Exception{
        logger.info("****** " + Thread.currentThread().getStackTrace()[1].getMethodName() + " :: Starting method.");
        Path nova = Paths.get("/root");
        try {
            assertTrue("Path " + nova + " should exists", Files.exists(nova));
            deleteDirectory(nova);
        }
        finally {
            logger.info("Thrown expected " + FileException.class.getName());
            logger.info("****** " + Thread.currentThread().getStackTrace()[1].getMethodName() + " :: Method done.");
        }
    }

    @Test
    public void testGetExtension() throws Exception{
        logger.info("****** " + Thread.currentThread().getStackTrace()[1].getMethodName() + " :: Starting method.");
        Path nova = Paths.get(TARGET.toString(), "nonExisting");
        createDirectory(nova);
        assertTrue("File extension should be 0 char length", PATH_OPERATIONS.getExtension(nova).length() == 0);
        PATH_OPERATIONS.deleteDir(nova);
        touch(nova.toFile());
        assertTrue("File extension should be 0 char length", PATH_OPERATIONS.getExtension(nova).length() == 0);
        Files.deleteIfExists(nova);
        nova = Paths.get(TARGET.toString(), "nonExisting.txt");
        touch(nova.toFile());
        assertTrue("File extension should be 4 char length", PATH_OPERATIONS.getExtension(nova).length() == 4);
        logger.info("****** " + Thread.currentThread().getStackTrace()[1].getMethodName() + " :: Method done.");
    }

    @Test
    public void testCopiesToDirectoryNonAtomicWithTempBuffer() throws Exception{
        logger.info("****** " + Thread.currentThread().getStackTrace()[1].getMethodName() + " :: Starting method.");
        Path nova = Paths.get(TARGET.toString(), "nonExisting");
        Path pom = Paths.get("pom.xml");
        Path readme = Paths.get("README.md");
        Path copy = Paths.get(nova.toString(), pom.getFileName().toString());
        Path copy2 = Paths.get(nova.toString(), readme.getFileName().toString());
        createDirectory(nova);
        logger.info("Copying file from " + pom + " to " + copy);
        logger.info("Copying file from " + readme + " to " + copy2);
        PATH_OPERATIONS.copyToDirectoryNonAtomicWithTempBuffer(nova, pom, readme);
        assertTrue(Files.exists(copy));
        assertTrue(Files.exists(copy2));
        deleteDirectory(nova);
        logger.info("****** " + Thread.currentThread().getStackTrace()[1].getMethodName() + " :: Method done.");
    }

    @Test
    public void testCopiesToDirectoryNonAtomicWithTempBuffer_Secured() throws Exception{
        logger.info("****** " + Thread.currentThread().getStackTrace()[1].getMethodName() + " :: Starting method.");
        Path nova = Paths.get("/root");
        Path pom = Paths.get("pom.xml");
        Path readme = Paths.get("README.md");
        Path copy = Paths.get(nova.toString(), readme.getFileName().toString());
        Path copy2 = Paths.get(nova.toString(), readme.getFileName().toString());
        logger.info("Copying file from " + pom + " to " + copy);
        logger.info("Copying file from " + readme + " to " + copy2);
        List<Path> paths = PATH_OPERATIONS.copyToDirectoryNonAtomicWithTempBuffer(nova, pom, readme);
        logger.info("Unable to copy to following paths: " + paths);
        assertTrue(paths.size() == 2);
        assertFalse(Files.exists(copy));
        assertFalse(Files.exists(copy2));
        logger.info("****** " + Thread.currentThread().getStackTrace()[1].getMethodName() + " :: Method done.");
    }

    @Test
    public void testCopyToDirectoryNonAtomicWithTempBuffer() throws Exception{
        logger.info("****** " + Thread.currentThread().getStackTrace()[1].getMethodName() + " :: Starting method.");
        Path nova = Paths.get(TARGET.toString(), "nonExisting");
        Path pom = Paths.get("pom.xml");
        Path copy = Paths.get(nova.toString(), pom.getFileName().toString());
        createDirectory(nova);
        logger.info("Copying file from " + pom + " to " + copy);
        PATH_OPERATIONS.copyToDirectoryNonAtomicWithTempBuffer(nova, pom);
        assertTrue(Files.exists(copy));
        deleteDirectory(nova);
        logger.info("****** " + Thread.currentThread().getStackTrace()[1].getMethodName() + " :: Method done.");
    }

    @Test (expected = ExecutionException.class)
    public void testCopyToDirectoryNonAtomicWithTempBuffer_Secured() throws Exception{
        try {
            logger.info("****** " + Thread.currentThread().getStackTrace()[1].getMethodName() + " :: Starting method.");
            Path nova = Paths.get("/root");
            Path pom = Paths.get("pom.xml");
            Path copy = Paths.get(nova.toString(), pom.getFileName().toString());
            logger.info("Copying file from " + pom + " to " + copy);
            PATH_OPERATIONS.copyToDirectoryNonAtomicWithTempBuffer(nova, pom);
        }
        finally {
            logger.info("Thrown expected " + ExecutionException.class.getName());
            logger.info("****** " + Thread.currentThread().getStackTrace()[1].getMethodName() + " :: Method done.");
        }
    }

    @Test
    public void testCopyToDirectoryNonAtomicWithTempBuffer1() throws Exception{

    }

    @Test
    public void testCopyToDirectoryWithTempBuffer() throws Exception{

    }

    @Test
    public void testCopyToDirectoryWithTempBuffer1() throws Exception{

    }

    @Test
    public void testGetListAsSet() throws Exception{

    }

    @Test
    public void testListPathsAsSet() throws Exception{

    }

    @Test
    public void testListPathsAsList() throws Exception{

    }

    @Test
    public void testListPaths() throws Exception{

    }

    @Test
    public void testListPathsWithFilter() throws Exception{

    }

    @Test
    public void testListPaths1() throws Exception{

    }

    @Test
    public void testContains() throws Exception{

    }

    @Test
    public void testListPathsWithFilter1() throws Exception{

    }

    @Test
    public void testToUnixPath() throws Exception{

    }

    @Test
    public void testEntryMatchingIndex() throws Exception{

    }

    @Test
    public void testGetPathFilterForRegularAndDirectoryFiles() throws Exception{

    }

    @Test
    public void testGetPathFilterForDirectoryFilesOnly() throws Exception{

    }

    @Test
    public void testGetPathFilterForDirectoryFilesOnlyWithLinkNotFollowed() throws Exception{

    }

    @Test
    public void testGetPathFilterForRegularFilesOnly() throws Exception{

    }

    @Test
    public void removeExtension() {
        String res = PATH_OPERATIONS.removeExtension("test.edoc");
        assertEquals("test", res);
    }

    private void deleteDirectory(Path nova) throws FileException{
        logger.info("Deleting directory " + nova);
        PATH_OPERATIONS.deleteDir(nova);
        assertFalse("Path " + nova + " should not exists", Files.exists(nova));
    }
    private void createDirectory(Path nova) throws ExecutionException{
        assertFalse("Path " + nova + " should not exists", Files.exists(nova));
        logger.info("Creating directory " + nova);
        PATH_OPERATIONS.createDirectory(nova);
    }
}