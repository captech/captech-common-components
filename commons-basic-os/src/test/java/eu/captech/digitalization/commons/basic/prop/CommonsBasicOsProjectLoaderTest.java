package eu.captech.digitalization.commons.basic.prop;

import eu.captech.digitalization.commons.basic.BasicOsCommonsTest;
import eu.captech.digitalization.commons.basic.doc.Preamble;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

@Preamble(
        lastModifiedBy = "Eduardo Melgar",
        creationDate = "1/14/13",
        creationTime = "3:50 PM",
        lastModified = "1/14/13"
)
public class CommonsBasicOsProjectLoaderTest extends BasicOsCommonsTest {
    private static final String PROJECT_PROPERTIES = "/projects/commons-basic-os-artifact.properties";

    @Override
    @Before
    public void setUp() throws Exception {
        logger = getLoggerFor(this.getClass());
        super.preMethodSetup();
        ProjectProperties.registerProjectProperties(CommonsBasicOsProjectLoader.class, PROJECT_PROPERTIES);
//        ProjectProperties.waitForRegistration();
    }

    @After
    public void tearDown() throws Exception {
        super.postMethodSetup();
    }

    @Test
    public void printRegisterProjectProperties() throws Exception {
        new CommonsBasicOsProjectLoader();
        ProjectProperties.printProperties();
    }
}
