package eu.captech.digitalization.commons.basic.file;

import eu.captech.digitalization.commons.basic.doc.Preamble;
import eu.captech.digitalization.commons.basic.function.Consumer;
import eu.captech.digitalization.commons.basic.function.Predicate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

@Preamble (
        lastModifiedBy = "Eduardo Melgar",
        creationDate = "23/01/15",
        creationTime = "10:01",
        lastModified = "23/01/15"
)
public class FileDirectoryWatcher{

    private static final Logger logger = LoggerFactory.getLogger(FileDirectoryWatcher.class);
    private final Executor executor = new ThreadPoolExecutor(0, Integer.MAX_VALUE,
                                                             60L, TimeUnit.MINUTES,
                                                             new SynchronousQueue<Runnable>());
    private final Map<WatchKey, Path> keys = new HashMap<>();
    private final boolean recursive;
    private final String os_name;
    private final WatchService watcher;
    private final Predicate<Path> filterPredicate;
    private Consumer<Path> handler;
    private boolean running = true;

    public FileDirectoryWatcher(@NotNull List<Path> paths, @NotNull Consumer<Path> handler,
                                @Nullable Predicate<Path> filterPredicate, boolean recursive) throws IOException{
        this(filterPredicate, recursive);
        this.handler = handler;
        registerPaths(paths);
    }

    protected FileDirectoryWatcher(Predicate<Path> filterPredicate, boolean recursive) throws IOException {
        this.recursive = recursive;
        this.os_name = System.getProperty("os.name");
        this.watcher = FileSystems.getDefault().newWatchService();
        this.filterPredicate = filterPredicate;
    }


    public final void start() {
        Executors.newSingleThreadExecutor().execute(
                new Runnable(){
                    @Override
                    public void run(){
                        processEvents();
                    }
                }
        );
    }

    public void registerPaths(List<Path> paths) throws IOException {
        for (Path path : paths) {
            if (!keys.containsValue(path)) {
                logger.info("Registering Path " + path);
                if (!recursive) {
                    register(path);
                }
                else {
                    registerAll(path);
                }
            }
        }
    }

    public void destroy() {
        running = false;
    }

    /**
     * Process all events for keys queued to the watcher
     */
    private void processEvents(){
        if(logger.isInfoEnabled()) {
            logger.info("About to run processEvents...");
        }

        while(running) {
            // wait for key to be signalled
            WatchKey key;
            try {
                key = watcher.take();
            }
            catch(InterruptedException x) {
                return;
            }
            Path dir = keys.get(key);
            if(dir == null) {
                logger.warn("WatchKey not recognized: " + key);
                continue;
            }
            for(WatchEvent<?> event : key.pollEvents()) {
                WatchEvent.Kind kind = event.kind();
                if(kind == OVERFLOW) {
                    continue;
                }
                // Context for directory entry event is the file name of entry
                WatchEvent<Path> ev = cast(event);
                Path name = ev.context();
                final Path child = dir.resolve(name);
                // print out event
                if(!Files.isDirectory(child, NOFOLLOW_LINKS)) {
                    if(filterPredicate == null || filterPredicate.test(child)) {
                        logger.debug("Event: " + event.kind().name() + ", child: " + (child.toString()));
                        manageEvent(event, child);
                    }
                }
            }
            // reset key and remove from set if directory no longer accessible
            boolean valid = key.reset();
            if(!valid) {
                keys.remove(key);
                // all directories are inaccessible
                if(keys.isEmpty()) {
                    break;
                }
            }
        }
    }

    private void manageEvent(WatchEvent<?> event, final Path child){
        if (event.kind().equals(ENTRY_CREATE)) {
            logger.debug("Handling ENTRY_MODIFY for file " + child + " on " + os_name);
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    handle(child);
                }
            });
        }
    }

    protected void handle(Path file) {
        if (handler != null) handler.accept(file);
    }


    /**
     * Register the given directory, and all its sub-directories, with the WatchService.
     *
     * @param startPath The Path Object where the recursive registration shall start
     * @throws java.io.IOException If an I/O error occurs
     */
    private void registerAll(final Path startPath) throws IOException {
        // registerService directory and sub-directories
        Files.walkFileTree(startPath, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
                    throws IOException {
                register(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    /**
     * Register the given directory with the WatchService
     *
     * @param directory The directory to registerService
     * @throws java.io.IOException If an I/O error occurs
     */
    private void register(Path directory) throws IOException {
        if (logger.isInfoEnabled()) {
            logger.info("Registering paths");
            logger.info("Path to input folder: " + directory.toString() + " : Exists? " + Files.exists(directory));
        }
        if (!Files.exists(directory)) {
            try {
                Files.createDirectories(directory);
            }
            catch (IOException e) {
                throw new IOException("Not able to create default Pile Input Directory: " + e.getMessage(), e);
            }
        }
        WatchKey key = directory.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
        if (logger.isDebugEnabled()) {
            Path prev = keys.get(key);
            if (prev == null) {
                logger.debug("Register new directory: " + directory);
            }
            else if (!directory.equals(prev)) {
                logger.debug("Updating registered directory " + prev + " to " + directory);
            }
        }
        keys.put(key, directory);
    }

    @SuppressWarnings ("unchecked")
    static <T> WatchEvent<T> cast(WatchEvent<?> event){
        return (WatchEvent<T>) event;
    }

}
