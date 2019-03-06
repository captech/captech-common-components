package eu.captech.digitalization.commons.basic;

import eu.captech.digitalization.commons.basic.doc.Preamble;
import eu.captech.digitalization.commons.basic.prop.AbstractCommonsTest;

@Preamble (
        lastModifiedBy = "Eduardo Melgar",
        creationDate = "30/09/14",
        creationTime = "14:38",
        lastModified = "30/09/14"
)
public class BasicCommonsTest extends AbstractCommonsTest{
    private static final String ARTIFACT_NAME = "commons-basic";

    public void preMethodSetup() throws Exception {
        logger.info("****************************************************************");
        logger.info("Starting preMethodSetup for {}", this.getClass().getSimpleName());
        setArtifactName(ARTIFACT_NAME);
        super.setUp();
        logger.info("PreMethodSetup for {} finished", this.getClass().getSimpleName());
    }

    @Override
    public void postMethodSetup() {
        logger.info("Finishing postMethodSetup for {}", this.getClass().getSimpleName());
    }

    @Override
    public void setArtifactName(String artifactName){
        this.artifactName = artifactName;
    }
}
