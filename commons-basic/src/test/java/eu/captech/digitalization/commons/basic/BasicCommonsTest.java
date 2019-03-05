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
        setArtifactName(ARTIFACT_NAME);
        super.setUp();
    }

    @Override
    public void setArtifactName(String artifactName){
        this.artifactName = artifactName;
    }
}
