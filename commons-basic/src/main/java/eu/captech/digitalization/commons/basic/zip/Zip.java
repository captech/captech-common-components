package eu.captech.digitalization.commons.basic.zip;

import eu.captech.digitalization.commons.basic.doc.Preamble;
import eu.captech.digitalization.commons.basic.exception.FileException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.*;

@Preamble(
        lastModifiedBy = "Eduardo Melgar",
        creationDate = "1/10/13",
        creationTime = "12:39 PM",
        lastModified = "1/10/13"
)
public class Zip {
    private static final Logger logger = LoggerFactory.getLogger(Zip.class);
    private static final int BUFFER = 2048;

    public void pack(Path pathToZip, Path outputPath, boolean includeRootDirectory) throws FileException {
        Path parentDirectory = outputPath.getParent();
        validateDirectoryPath(parentDirectory);
        try (OutputStream fileOutputStream = Files.newOutputStream(outputPath)) {
            CheckedOutputStream checksum = new CheckedOutputStream(fileOutputStream, new Adler32());
            try (ZipOutputStream zipOutputStream = new ZipOutputStream(new BufferedOutputStream(checksum))) {
                zipOutputStream.setMethod(ZipOutputStream.DEFLATED);
                int index;
                String absPath = pathToZip.toAbsolutePath().toString();
                if (includeRootDirectory) {
                    String baseName = absPath.substring(0, (absPath.lastIndexOf(File.separator)) + 1);
                    index = baseName.length();
                }
                else {
                    index = absPath.length() + 1;
                }
                appendPathsToZip(pathToZip, zipOutputStream, index);
            }
            if (logger.isDebugEnabled()) {
                logger.info("Zip file created with checksum " + checksum.getChecksum().getValue());
            }
        }
        catch (IOException e) {
            throw new FileException("Error opening path for writing: " + e.getMessage(), e);
        }
    }

    public void pack(Path outputPath, Path... pathsToZip) throws FileException {
        Path parentDirectory = outputPath.getParent();
        validateDirectoryPath(parentDirectory);
        Path[] paths = pathsToZip.clone();
        try (OutputStream fileOutputStream = Files.newOutputStream(outputPath)) {
            CheckedOutputStream checksum = new CheckedOutputStream(fileOutputStream, new Adler32());
            try (ZipOutputStream zipOutputStream = new ZipOutputStream(new BufferedOutputStream(checksum))) {
                zipOutputStream.setMethod(ZipOutputStream.DEFLATED);
                String absPath = paths[0].toString();
                int baseNameLength = (absPath.substring(0, (absPath.lastIndexOf(File.separator)) + 1)).length();
                for (Path single : paths) {
                    appendPathToZip(single, zipOutputStream, baseNameLength);
                }
            }
            if (logger.isDebugEnabled()) {
                logger.info("Zip file created with checksum " + checksum.getChecksum().getValue());
            }

        }
        catch (IOException e) {
            throw new FileException("Error opening path for writing: " + e.getMessage(), e);
        }
    }

    /**
     * This operation should be used to zip only the files and not the directories
     * @param outputPath
     * @param pathsToZip
     * @throws FileException
     */
    public void packWithoutDirectoryTree(Path outputPath, Path... pathsToZip) throws FileException {
        Path parentDirectory = outputPath.getParent();
        validateDirectoryPath(parentDirectory);
        Path[] paths = pathsToZip.clone();
        try (OutputStream fileOutputStream = Files.newOutputStream(outputPath)) {
            CheckedOutputStream checksum = new CheckedOutputStream(fileOutputStream, new Adler32());
            try (ZipOutputStream zipOutputStream = new ZipOutputStream(new BufferedOutputStream(checksum))) {
                zipOutputStream.setMethod(ZipOutputStream.DEFLATED);
                for (Path single : paths) {
                    appendPathToZipWithFileName(single, zipOutputStream);
                }
            }
            if (logger.isDebugEnabled()) {
                logger.info("Zip file created with checksum " + checksum.getChecksum().getValue());
            }

        }
        catch (IOException e) {
            throw new FileException("Error opening path for writing: " + e.getMessage(), e);
        }
    }
    
    public void unpack(Path inputZipPath, Path outputDirectory) throws FileException {
        validateDirectoryPath(outputDirectory);
        try (InputStream fileInputStream = Files.newInputStream(inputZipPath)) {
            CheckedInputStream checkedInputStream = new CheckedInputStream(fileInputStream, new Adler32());
            try (ZipInputStream zipInputStream = new ZipInputStream(new BufferedInputStream(checkedInputStream))) {
                ZipEntry zipEntry;
                Path directoryEntry;
                int counter = 1;
                while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                    String zipName = zipEntry.getName();
                    if (logger.isInfoEnabled()) {
                        logger.info(counter++ + "\tProcessing: " + zipEntry + "\t[" + zipName + "]");
                    }
                    if (zipEntry.isDirectory()) {
                        directoryEntry = Files.createDirectories(Paths.get(outputDirectory.toString(), zipName));
                        if (Files.exists(directoryEntry)) {
                            if (logger.isInfoEnabled()) {
                                logger.info("Directory " + zipName + " successfully created.");
                            }
                        }
                        else {
                            if (logger.isInfoEnabled()) {
                                logger.info("Error creating directory " + zipName + ".");
                            }
                            throw new FileException("Not able to create " + directoryEntry.toString() + " on target directory");
                        }
                    }
                    else {
                        if (zipName.contains(File.separator)) {
                            if (logger.isInfoEnabled()) {
                                logger.info("The file to be extracted has full path. Directories will be created 'on the fly'");
                            }
                            directoryEntry = Paths.get(outputDirectory.toString(), zipName.substring(0, zipName.lastIndexOf(File.separator)));
                            if (!Files.exists(directoryEntry)) {
                                directoryEntry = Files.createDirectories(directoryEntry);
                                if (Files.exists(directoryEntry)) {
                                    if (logger.isInfoEnabled()) {
                                        logger.info("Directory " + directoryEntry + " successfully created.");
                                    }
                                }
                                else {
                                    if (logger.isInfoEnabled()) {
                                        logger.info("Error creating directory " + directoryEntry.toString() + ".");
                                    }
                                    throw new FileException("Not able to create " + directoryEntry.toString() + " on target directory");
                                }
                            }
                        }
                        extractZipEntry(outputDirectory, zipInputStream, zipName);
                    }
                }

            }
        }
        catch (IOException e) {
            throw new FileException("The path does not exist or cannot be opened for any other reason."
                    + e.getMessage(), e);
        }
    }

    private void extractZipEntry(Path outputDirectory, ZipInputStream zipInputStream, String zipName) throws FileException {
        int count;
        byte data[] = new byte[BUFFER];
        Path pathToExtract = Paths.get(outputDirectory.toAbsolutePath().toString(), zipName);
        if (logger.isInfoEnabled()) {
            logger.info("About to extract " + pathToExtract.toString());
        }
        try (OutputStream fos = Files.newOutputStream(pathToExtract)) {
            try (BufferedOutputStream dest = new BufferedOutputStream(fos, BUFFER)) {
                while ((count = zipInputStream.read(data, 0, BUFFER)) != -1) {
                    dest.write(data, 0, count);
                }
            }

        }
        catch (IOException e) {
            throw new FileException("The output path " + pathToExtract.toString() + "could not be created: " + e.getMessage(), e);
        }
        if (logger.isInfoEnabled()) {
            try {
                logger.info("Path extracted: " + pathToExtract.toString() + " with size: " + Files.size(pathToExtract));
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void validateDirectoryPath(Path zipParentDirectory) throws FileException {
        String reason = "Directory '" + zipParentDirectory.toString() + "':\t";
        boolean errorFound = false;
        if (!Files.exists(zipParentDirectory)) {
            reason += "\tThe path doesn't exists.";
            errorFound = true;
        }
        else if (!Files.isDirectory(zipParentDirectory)) {
            reason += "\tThe path is not a directory and can not be zipped as containing directory.";
            errorFound = true;
        }
        if (errorFound) {
            throw new FileException("The Zip could not be created: " + reason);
        }
    }

    private void appendPathsToZip(Path containingDirectory, ZipOutputStream zipOutputStream, int baseNameLength) throws FileException {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(containingDirectory, getPathFilter(containingDirectory))) {
            if (logger.isInfoEnabled()) {
                logger.info("About to Zip " + containingDirectory.toAbsolutePath().toString());
            }
            for (Path single : stream) {
                appendPathToZip(single, zipOutputStream, baseNameLength);
            }
        }
        catch (IOException e) {
            throw new FileException("The path " + containingDirectory.toString() + "does not exist or cannot be opened for any other reason: "
                    + e.getMessage(), e);
        }

    }

    private void appendPathToZip(Path file, ZipOutputStream zipOutputStream, int baseNameLength) throws FileException {
        BufferedInputStream bufferedInputStream;
        byte data[] = new byte[BUFFER];
        int count;
        ZipEntry zipEntry;
        InputStream fileInputStream;
        try {
            if (Files.isDirectory(file)) {
                appendPathsToZip(file, zipOutputStream, baseNameLength);
                return;
            }
            else {
                fileInputStream = Files.newInputStream(file);
            }
        }
        catch (IOException e) {
            throw new FileException("The file does not exist and cannot be created, or cannot be opened for any other reason: "
                    + e.getMessage(), e);
        }
        bufferedInputStream = new BufferedInputStream(fileInputStream, BUFFER);
        String absPath = file.toAbsolutePath().toString();
        zipEntry = new ZipEntry(absPath.substring(baseNameLength));
        if (logger.isInfoEnabled()) {
            logger.info("Appending: " + zipEntry.toString());
        }
        try {
            zipOutputStream.putNextEntry(zipEntry);
            while ((count = bufferedInputStream.read(data, 0, BUFFER)) != -1) {
                zipOutputStream.write(data, 0, count);
            }
            bufferedInputStream.close();
        }
        catch (IOException e) {
            throw new FileException("Error appending a new entry (" + zipEntry + ") to the Zip file: " + e.getMessage(), e);
        }
    }

    private void appendPathToZipWithFileName(Path file, ZipOutputStream zipOutputStream) throws FileException {
        BufferedInputStream bufferedInputStream;
        byte data[] = new byte[BUFFER];
        int count;
        ZipEntry zipEntry;
        InputStream fileInputStream;
        try {
            if (Files.isDirectory(file)) {
                throw new UnsupportedOperationException("Can not zip the directories");
            } else {
                fileInputStream = Files.newInputStream(file);
            }
        }
        catch (IOException e) {
            throw new FileException("The file does not exist and cannot be created, or cannot be opened for any other reason: "
                    + e.getMessage(), e);
        }
        bufferedInputStream = new BufferedInputStream(fileInputStream, BUFFER);
        zipEntry = new ZipEntry(file.getFileName().toString());
        if (logger.isInfoEnabled()) {
            logger.info("Appending: " + zipEntry.toString());
        }
        try {
            zipOutputStream.putNextEntry(zipEntry);
            while ((count = bufferedInputStream.read(data, 0, BUFFER)) != -1) {
                zipOutputStream.write(data, 0, count);
            }
            bufferedInputStream.close();
        }
        catch (IOException e) {
            throw new FileException("Error appending a new entry (" + zipEntry + ") to the Zip file: " + e.getMessage(), e);
        }
    }
    
    private static DirectoryStream.Filter<Path> getPathFilter(final Path path) {
        return new DirectoryStream.Filter<Path>() {
            public boolean accept(Path file) throws IOException {
                return ((Files.isDirectory(path) || Files.isRegularFile(path)) && Files.isReadable(path));
            }
        };
    }

//    public Path packArray(Path[] files, Path zipPath) throws PathException{
//        OutputStream fileOutputStream;
//        try {
//            fileOutputStream = Paths.newOutputStream(zipPath);
//        }
//        catch(IOException e) {
//            logger.error(e.getMessage(), e);
//            throw new PathException("Zip.packArray(): Not able to open a output stream to " + zipPath.toAbsolutePath().toString() +
//                                    ": " + e.getMessage());
//        }
//        CheckedOutputStream checksum = new CheckedOutputStream(fileOutputStream, new Adler32());
//        ZipOutputStream zipOutputStream = new ZipOutputStream(new BufferedOutputStream(checksum));
//        zipOutputStream.setMethod(ZipOutputStream.DEFLATED);
//        String absPath = files[0].toAbsolutePath().toString();
//        String baseName = absPath.substring(0, (absPath.lastIndexOf(Path.separator)) + 1);
//        int index = baseName.length();
//        appendPathsToZip(files, zipOutputStream, index);
//        try {
//            zipOutputStream.close();
//        }
//        catch (IOException e) {
//            logger.error(e.getMessage(), e);
//            throw new PathException("Zip.packArray(): Error closing the ZipOutputStream: " + e.getMessage());
//        }
//        setZipChecksum(checksum.getChecksum().getValue());
//        return zipPath;
//    }
//
//    public Path packArrayFromParent(Path[] files, Path zipParentDirectory, String zipName) throws PathException {
//        validateDirectoryPath(zipParentDirectory);
//        return packArray(files, Paths.get(zipParentDirectory.toString(), zipName));
//    }
//
//    private void appendPathsToZip(Path[] files, ZipOutputStream zipOutputStream, int baseNameLength) throws PathException {
//        if (logger.isInfoEnabled()) {
//            logger.info("About to Zip " + files.length + " files.");
//        }
//        for (Path single : files) {
//            appendPathToZip(single, zipOutputStream, baseNameLength);
//        }
//    }
}
