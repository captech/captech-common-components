package eu.captech.digitalization.commons.basic.api;

import eu.captech.digitalization.commons.basic.exception.ExecutionException;
import eu.captech.digitalization.commons.basic.exception.FileException;

import java.io.InputStream;
import java.nio.file.CopyOption;
import java.nio.file.DirectoryStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

public interface IPathOperations {
    String EMPTY_STRING = "";
    String DOT_STRING = ".";
    String TEMP_SUFFIX = ".temp";

    void createDirectory(Path directory) throws ExecutionException;

    void deleteDir(Path path, boolean deleteThisParentDirectory) throws FileException;

    void deleteDir(Path path) throws FileException;

    String getExtension(Path regularFile);

    List<Path> copyToDirectoryNonAtomicWithTempBuffer(Path directoryDestination, Path... sources);

    void copyToDirectoryNonAtomicWithTempBuffer(Path directoryDestination, Path source) throws ExecutionException;

    Path copyToDirectoryWithTempBuffer(Path directoryDestination, String filename, Path source, CopyOption... copyOptions) throws ExecutionException;

    Path copyToDirectoryWithTempBuffer(Path directoryDestination, String filename, InputStream inputStream, CopyOption... copyOptions) throws ExecutionException;

    Set<Path> getListAsSet(List<Path> paths);

    Set<Path> listPathsAsSet(Path directory) throws ExecutionException;

    ArrayList<Path> listPathsAsList(Path directory) throws ExecutionException;

    ArrayList<Path> listPathsAsList(Path directory, boolean onlyRegularFiles) throws ExecutionException;

    Path[] listPaths(Path directory) throws ExecutionException;

    Path[] listPathsWithFilter(Path directory, DirectoryStream.Filter<Path> filter) throws ExecutionException;

    Path[] listPaths(Path directory, String regex) throws ExecutionException;

    boolean contains(Path directory, String regex) throws ExecutionException;

    Path[] listPathsWithFilter(Path directory, DirectoryStream.Filter<Path> filter, String regex) throws ExecutionException;

    String toUnixPath(String path);

    String entryMatchingIndex(CharSequence inputStr, Pattern pattern);

    DirectoryStream.Filter<Path> getPathFilterForRegularAndDirectoryFiles(Path path);

    DirectoryStream.Filter<Path> getPathFilterForDirectoryFilesOnly();

    DirectoryStream.Filter<Path> getPathFilterForDirectoryFilesOnlyWithLinkNotFollowed();

    DirectoryStream.Filter<Path> getPathFilterForRegularFilesOnly(Path path);
}
