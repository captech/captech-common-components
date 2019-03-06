package eu.captech.digitalization.commons.basic.prop;

import eu.captech.digitalization.commons.basic.doc.Preamble;
import eu.captech.digitalization.commons.basic.files.io.PathOperations;
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
    protected static final Path TARGET = Paths.get("target");
    protected String artifactName;
    protected Path testResources = Paths.get("src", "test", "resources");
    protected Path mainResources = Paths.get("src", "main", "resources");
    protected Logger logger;
    protected boolean idea = false;

    public void setUp() throws Exception {
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

    public abstract void preMethodSetup()
            throws Exception;

    public abstract void postMethodSetup();

    public void startingTestMethod(String methodName) {
        logger.info("  *** Starting method '" + methodName + "'");
    }

    public void finishingTestMethod(String methodName) {
        logger.info("  *** Finishing method '" + methodName + "'");
    }

    public Logger getLoggerFor(Class clazz) {
        return LoggerFactory.getLogger(clazz);
    }

}
