package eu.captech.digitalization.commons.basic;

import eu.captech.digitalization.commons.basic.doc.Preamble;
import eu.captech.digitalization.commons.basic.prop.AbstractCommonsTest;

@Preamble(
        lastModifiedBy = "Eduardo Melgar",
        creationDate = "8/11/14",
        creationTime = "11:07 AM",
        lastModified = "8/11/14"
)
public abstract class BasicOsCommonsTest extends AbstractCommonsTest {
    private static final String ARTIFACT_NAME = "commons-basic-os";
    protected static final char SPACE_CHAR = ' ';

    @Override
    public void preMethodSetup()
            throws Exception {
        logger = getLoggerFor(this.getClass());
        logger.info("****************************************************************");
        logger.info("Starting preMethodSetup for {}", this.getClass().getSimpleName());
        setArtifactName(ARTIFACT_NAME);
        super.setUp();
        logger.info("PreMethodSetup for {} finished", this.getClass().getSimpleName());
    }

    @Override
    public void postMethodSetup() {
        logger = getLoggerFor(this.getClass());
        logger.info("Finishing postMethodSetup for {}", this.getClass().getSimpleName());
    }

    @Override
    public void setArtifactName(String artifactName) {
        this.artifactName = artifactName;
    }
}
