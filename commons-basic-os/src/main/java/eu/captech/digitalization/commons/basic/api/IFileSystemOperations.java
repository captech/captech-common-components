package eu.captech.digitalization.commons.basic.api;

import eu.captech.digitalization.commons.basic.doc.Preamble;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Preamble(
        lastModifiedBy = "Eduardo Melgar",
        creationDate = "12/6/12",
        creationTime = "12:42 PM",
        lastModified = "12/6/12"
)
public interface IFileSystemOperations extends IOperations {
    public static final String EMPTY_STRING = "";
    Set<String> sendMultipleStreams(String remoteDirectory, Map<String, InputStream> files);
    Set<String> sendMultipleStreams(String remoteDirectory, Map<String, InputStream> files, String tag);

    Set<String> sendMultiplePaths(String remoteDirectory, Set<Path> files);
    Set<String> sendMultiplePaths(String remoteDirectory, Set<Path> files, String tag);

    boolean send(String remoteDirectory, String filename, InputStream inputStream);
    boolean send(String remoteDirectory, String filename, InputStream inputStream, String tag);

    boolean send(String remoteDirectory, String filename, Path path);
    boolean send(String remoteDirectory, String filename, Path path, String tag);

    boolean sendIfNotExists(String remoteDirectory, String filename, InputStream inputStream);

    boolean sendIfNotExists(String remoteDirectory, String filename, Path path);
    boolean sendIfNotExists(String remoteDirectory, String filename, Path path, String tag);

    long get(String remoteDirectory, String filename, OutputStream outputStream);

    long get(String remoteDirectory, String filename, Path path);

    long getUnique(String remoteDirectory, String fileNameRegex, OutputStream outputStream);

    long getUnique(String remoteDirectory, String fileNameRegex, Path path);

    boolean delete(String remoteDirectory, String filename);

    List<String> listRemoteDirectory(String remoteDirectory);

    boolean exists(final String directory, final String filename);
    boolean exists(final String username, final String directory, final String filename);
    boolean exists(final String username, final String directory, final String filename, final String tag);

    boolean deleteIfExists(String remoteDirectory, String filename);

    boolean mkdir(String remoteDirectory);
}
