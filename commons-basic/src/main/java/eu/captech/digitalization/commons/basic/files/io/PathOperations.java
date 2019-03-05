package eu.captech.digitalization.commons.basic.files.io;

import eu.captech.digitalization.commons.basic.api.IPathOperations;
import eu.captech.digitalization.commons.basic.doc.Preamble;
import eu.captech.digitalization.commons.basic.exception.ExecutionException;
import eu.captech.digitalization.commons.basic.exception.FileException;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.CopyOption;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Preamble(
        lastModifiedBy = "Eduardo Melgar",
        creationDate = "11/29/12",
        creationTime = "3:25 PM",
        lastModified = "11/29/12"
)
public class PathOperations implements IPathOperations {
    private static final Logger logger = LoggerFactory.getLogger(PathOperations.class);

    @Override
    public synchronized void createDirectory(Path directory) throws ExecutionException {
        try {
            Files.createDirectories(directory);
        }
        catch (IOException e) {
            throw new ExecutionException("Not able to create Directory '" + directory.toString() + "': " + e.getMessage(), e);
        }
    }

    @Override
    public synchronized void deleteDir(Path path, boolean deleteThisParentDirectory) throws FileException {
        if (deleteThisParentDirectory) {
            deleteDir(path);
        }
        else {
            Path[] paths;
            try {
                paths = listPaths(path);
                for (Path p : paths) {
                    if (Files.isDirectory(p)) {
                        deleteDir(p);
                    }
                    else {
                        try {
                            Files.delete(p);
                        }
                        catch (IOException e) {
                            throw new FileException("The path " + path.toString() + "does not exist or cannot be opened for any other reason: "
                                    + e.getMessage(), e);
                        }
                    }
                }
            }
            catch (Exception e) {
                throw new FileException("Not able to list path " + path, e);
            }
        }
    }

    @Override
    public synchronized void deleteDir(Path path) throws FileException {
        if (path != null) {
            if (Files.exists(path) && Files.isDirectory(path)) {
                try (DirectoryStream<Path> stream = Files.newDirectoryStream(path, getPathFilterForRegularAndDirectoryFiles(path))) {
                    logger.debug("About to delete " + path.toAbsolutePath().toString());
                    for (Path single : stream) {
                        if (Files.isRegularFile(single)) {
                            Files.delete(single);
                        }
                        else {
                            deleteDir(single);
                        }
                    }
                    Files.delete(path);
                }
                catch (Exception e) {
                    throw new FileException("The path " + path.toString() + " cannot be opened. Possible reason: "
                            + e.getMessage(), e);
                }
            }
        }
        else {
            throw new FileException("The path file doesn't exists: Object File is null. " +
                    "Please check the path and try again.");
        }
    }

    @Override
    public String getExtension(Path regularFile) {
        if (Files.isRegularFile(regularFile)) {
            String name = regularFile.getFileName().toString();
            if (name.contains(DOT_STRING)) {
                return name.substring(name.lastIndexOf(DOT_STRING), name.length());
            }
        }
        return EMPTY_STRING;
    }

    /**
     * Copies files to a predefined directory.
     *
     * @param directoryDestination The destination directory
     * @param sources              The source files to copy
     * @return A list of Path objects that could not be copied
     */
    @Override
    public List<Path> copyToDirectoryNonAtomicWithTempBuffer(Path directoryDestination, Path... sources) {
        List<Path> paths = new ArrayList<>();
        for (Path path : sources) {
            try {
                copyToDirectoryNonAtomicWithTempBuffer(directoryDestination, path);
            }
            catch (ExecutionException e) {
                paths.add(path);
            }
        }
        return paths;
    }

    @Override
    public void copyToDirectoryNonAtomicWithTempBuffer(Path directoryDestination, Path source) throws ExecutionException {
        String filename = source.getFileName().toString();
        Path temp = Paths.get(directoryDestination.toString(), filename + TEMP_SUFFIX);
        Path destination = Paths.get(directoryDestination.toString(), filename);
        try {
            Files.copy(source, temp);
            try {
                Files.move(temp, destination);
            }
            catch (IOException e) {
                throw new ExecutionException("Not able to rename " + temp + " to " + destination, e);
            }
        }
        catch (IOException e) {
            throw new ExecutionException("Not able to copy " + source + " to " + temp, e);
        }
    }

    @Override
    public Path copyToDirectoryWithTempBuffer(Path directoryDestination, String filename, @NotNull Path source, CopyOption... copyOptions) throws ExecutionException {
        Path temp = Paths.get(directoryDestination.toString(), filename + TEMP_SUFFIX);
        Path destination = Paths.get(directoryDestination.toString(), filename);
        try {
            if (Files.exists(source) && Files.isReadable(source)) {
                Files.copy(source, temp);
            }
            else {
                throw new ExecutionException("Source Path " + source + " doesn't exists or is no readable");
            }
            try {
                Files.move(temp, destination, copyOptions);
                return destination;
            }
            catch (IOException e) {
                throw new ExecutionException("Not able to rename " + temp + " to " + destination, e);
            }
        }
        catch (IOException e) {
            throw new ExecutionException("Not able to copy " + source + " to " + temp, e);
        }
    }

    @Override
    public Path copyToDirectoryWithTempBuffer(Path directoryDestination, String filename, @NotNull InputStream inputStream, CopyOption... copyOptions) throws ExecutionException {
        Path temp = Paths.get(directoryDestination.toString(), filename + TEMP_SUFFIX);
        Path destination = Paths.get(directoryDestination.toString(), filename);
        try {
            if (inputStream != null) {
                Files.copy(inputStream, temp);
            }
            else {
                throw new ExecutionException("Unable to read InputStream Object.");
            }
            try {
                Files.move(temp, destination, copyOptions);
                return destination;
            }
            catch (IOException e) {
                throw new ExecutionException("Not able to rename " + temp + " to " + destination, e);
            }
        }
        catch (IOException e) {
            throw new ExecutionException("Not able to copy InputStream to " + temp, e);
        }
    }

    @Override
    public Set<Path> getListAsSet(List<Path> paths) {
        return new HashSet<>(paths);
    }

    @Override
    public Set<Path> listPathsAsSet(Path directory) throws ExecutionException {
        Set<Path> paths = new HashSet<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory)) {
            if (logger.isDebugEnabled()) {
                logger.debug("About to traverse directory on " + directory.toAbsolutePath().toString());
            }
            for (Path path : stream) {
                paths.add(path);
            }
        }
        catch (Exception e) {
            throw new ExecutionException("The path " + directory.toString() + " does not exist or cannot be opened for any other reason: " +
                    e.getMessage(), e);
        }
        return paths;
    }

    @Override
    public ArrayList<Path> listPathsAsList(Path directory) throws ExecutionException {
        return listPathsAsList(directory, false);
    }

    @Override
    public ArrayList<Path> listPathsAsList(Path directory, boolean onlyRegularFiles) throws ExecutionException {
        ArrayList<Path> paths = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory)) {
            if (logger.isDebugEnabled()) {
                logger.debug("About to traverse directory on " + directory.toAbsolutePath().toString());
            }
            for (Path path : stream) {
                if (onlyRegularFiles) {
                    if (Files.isRegularFile(path)) {
                        paths.add(path);
                    }
                }
                else {
                    paths.add(path);
                }
            }
        }
        catch (Exception e) {
            throw new ExecutionException("The path " + directory.toString() + " does not exist or cannot be opened for any other reason: " +
                                         e.getMessage(), e);
        }
        return paths;
    }

    @Override
    public Path[] listPaths(Path directory) throws ExecutionException {
        ArrayList<Path> paths = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory)) {
            if (logger.isDebugEnabled()) {
                logger.debug("About to traverse directory on " + directory.toAbsolutePath().toString());
            }
            for (Path path : stream) {
                paths.add(path);
            }
        }
        catch (Exception e) {
            throw new ExecutionException("The path " + directory.toString() + " does not exist or cannot be opened for any other reason: " +
                    e.getMessage(), e);
        }
        return paths.toArray(new Path[paths.size()]);
    }

    @Override
    public Path[] listPathsWithFilter(Path directory, DirectoryStream.Filter<Path> filter) throws ExecutionException {
        ArrayList<Path> paths = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory, filter)) {
            if (logger.isDebugEnabled()) {
                logger.debug("About to traverse directory on " + directory.toAbsolutePath().toString());
            }
            for (Path path : stream) {
                paths.add(path);
            }
        }
        catch (Exception e) {
            throw new ExecutionException("The path " + directory.toString() + " does not exist or cannot be opened for any other reason: " +
                    e.getMessage(), e);
        }
        return paths.toArray(new Path[paths.size()]);
    }

    @Override
    public Path[] listPaths(Path directory, String regex) throws ExecutionException {
        ArrayList<Path> paths = new ArrayList<>();
        Pattern pattern = Pattern.compile(regex);
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory)) {
            if (logger.isDebugEnabled()) {
                logger.debug("About to traverse directory on " + directory.toAbsolutePath().toString());
            }
            for (Path path : stream) {
                if (pattern.matcher(path.getFileName().toString()).matches()) {
                    paths.add(path);
                }
            }
        }
        catch (Exception e) {
            throw new ExecutionException("The path " + directory.toString() + " does not exist or cannot be opened for any other reason: " +
                    e.getMessage(), e);
        }
        return paths.toArray(new Path[paths.size()]);
    }

    @Override
    public boolean contains(Path directory, String regex) throws ExecutionException {
        ArrayList<Path> paths = new ArrayList<>();
        Pattern pattern = Pattern.compile(regex);
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory)) {
            if (logger.isDebugEnabled()) {
                logger.debug("About to traverse directory on " + directory.toAbsolutePath().toString());
            }
            for (Path path : stream) {
                if (pattern.matcher(path.getFileName().toString()).matches()) {
                    paths.add(path);
                }
            }
        }
        catch (Exception e) {
            throw new ExecutionException("The path " + directory.toString() + " does not exist or cannot be opened for any other reason: " +
                    e.getMessage(), e);
        }
        return paths.size() > 0;
    }

    @Override
    public Path[] listPathsWithFilter(Path directory, DirectoryStream.Filter<Path> filter, String regex) throws ExecutionException {
        ArrayList<Path> paths = new ArrayList<>();
        Pattern pattern = Pattern.compile(regex);
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory, filter)) {
            if (logger.isDebugEnabled()) {
                logger.debug("About to traverse directory on " + directory.toAbsolutePath().toString());
            }
            for (Path path : stream) {
                if (pattern.matcher(path.getFileName().toString()).matches()) {
                    paths.add(path);
                }
            }
        }
        catch (Exception e) {
            throw new ExecutionException("The path " + directory.toString() + " does not exist or cannot be opened for any other reason: " +
                    e.getMessage(), e);
        }
        return paths.toArray(new Path[paths.size()]);
    }

    @Override
    public String toUnixPath(String path) {
        return path.replace('\\', '/');
    }

    @Override
    public String entryMatchingIndex(CharSequence inputStr, Pattern pattern) {
        Matcher matcher = pattern.matcher(inputStr);
        boolean matchFound = matcher.find();
        if (matchFound) {
            return matcher.group();
        }
        return null;
    }

    @Override
    public DirectoryStream.Filter<Path> getPathFilterForRegularAndDirectoryFiles(final Path path) {
        return new DirectoryStream.Filter<Path>() {
            public boolean accept(Path file) throws IOException {
                return ((Files.isDirectory(path) || Files.isRegularFile(path)) && Files.isReadable(path) && Files.isWritable(path));
            }
        };
    }

    @Override
    public DirectoryStream.Filter<Path> getPathFilterForDirectoryFilesOnly() {
        return new DirectoryStream.Filter<Path>() {
            public boolean accept(Path file) throws IOException {
                return (Files.isDirectory(file) && Files.isReadable(file) && Files.isWritable(file));
            }
        };
    }

    @Override
    public DirectoryStream.Filter<Path> getPathFilterForDirectoryFilesOnlyWithLinkNotFollowed() {
        return new DirectoryStream.Filter<Path>() {
            public boolean accept(Path file) throws IOException {
                return (Files.isDirectory(file, LinkOption.NOFOLLOW_LINKS) && Files.isReadable(file) && Files.isWritable(file));
            }
        };
    }


    @Override
    public DirectoryStream.Filter<Path> getPathFilterForRegularFilesOnly(final Path path) {
        return new DirectoryStream.Filter<Path>() {
            public boolean accept(Path file) throws IOException {
                return (Files.isRegularFile(path) && Files.isReadable(path) && Files.isWritable(path));
            }
        };
    }

    public String removeExtension(@NotNull String fileName) {
        return FilenameUtils.removeExtension(fileName);
    }
}
