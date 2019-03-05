package eu.captech.digitalization.commons.basic.prop;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.ConsoleAppender;
import eu.captech.digitalization.commons.basic.doc.Preamble;
import eu.captech.digitalization.commons.basic.files.io.PathOperations;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Preamble(
        lastModifiedBy = "Eduardo Melgar",
        creationDate = "14.01.13",
        creationTime = "09:36",
        lastModified = "14.01.13"
)
public abstract class AbstractCommonsTest {
    private ConsoleAppender<ILoggingEvent> consoleAppender;
    protected static final Path TARGET = Paths.get("target");
    protected String artifactName;
    protected Path testResources = Paths.get("src", "test", "resources");
    protected Path mainResources = Paths.get("src", "main", "resources");
    protected Logger logger;
    protected boolean idea = false;

    public void setUp() throws Exception {
        consoleAppender = getLoggingEventConsoleAppender();
        logger = getLoggerFor(this.getClass());
        logger.info("*** " + this.getClass().getSimpleName() + " :: Starting setup.");
        PathOperations pathOperations = new PathOperations();
        List<Path> paths = pathOperations.listPathsAsList(Paths.get("."));
        for (Path path : paths) {
            if (path.getFileName().toString().equals(artifactName)) {
                logger.debug("Running inside IDEA, with artifact name " + artifactName);
                idea = true;
            }
        }
        if (idea) {
            testResources = Paths.get(artifactName, testResources.toString());
            mainResources = Paths.get(artifactName, mainResources.toString());
        }
        logger.debug("artifactName: " + artifactName + "\ttestResources: " + testResources + "\tmainResources: " + mainResources);
        if (!Files.exists(TARGET)) {
            Files.createDirectories(TARGET);
        }
    }

    public abstract void setArtifactName(String artifactName);

    public void tearDown() throws Exception {
        logger = LoggerFactory.getLogger(AbstractCommonsTest.class);
        logger.info("*** " + this.getClass().getSimpleName() + " :: Finishing tear down.");
        consoleAppender.stop();
    }

    public void startingTestMethod(String methodName) {
        logger.info("  * Starting method '" + methodName + "'");
    }

    public void finishingTestMethod(String methodName) {
        logger.info("  * Finishing method '" + methodName + "'");
    }

    protected Logger getLoggerFor(Class clazz) {
        return getLogger(clazz, getConsoleAppender());
    }

    private ConsoleAppender<ILoggingEvent> getConsoleAppender() {
        return consoleAppender;
    }

    @NotNull
    private static ConsoleAppender<ILoggingEvent> getLoggingEventConsoleAppender() {
        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
        PatternLayoutEncoder ple = new PatternLayoutEncoder();
        ple.setPattern("%d{ISO8601} %highlight([%p]) [%file:%line] %m%n");
        ple.setContext(lc);
        ple.start();
        ConsoleAppender<ILoggingEvent> consoleAppender = new ConsoleAppender<>();
        consoleAppender.setEncoder(ple);
        consoleAppender.setContext(lc);
        consoleAppender.start();
        return consoleAppender;
    }

    @NotNull
    private static ch.qos.logback.classic.Logger getLogger(Class clazz, ConsoleAppender<ILoggingEvent> consoleAppender) {
        ch.qos.logback.classic.Logger logger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(clazz);
        logger.addAppender(consoleAppender);
        logger.setLevel(Level.INFO);
        logger.setAdditive(false); /* set to true if root should log too */
        return logger;
    }
}
