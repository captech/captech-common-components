package eu.captech.digitalization.commons.basic.prop;

import org.junit.Test;

import java.util.Properties;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class ProjectPropertiesTest {
    private static final String PROJECT_PROPERTIES = "/projects/commons-basic-artifact.properties";

    @Test
    public void testRegisterProjectProperties() throws Exception {
        try {
            ProjectProperties.registerProjectProperties(ProjectProperties.class, PROJECT_PROPERTIES);
        } catch (Exception e) {
            fail("Properties not loaded");
        }
    }

    @Test
    public void testGetProperties() throws Exception {
        ProjectProperties.registerProjectProperties(ProjectProperties.class, PROJECT_PROPERTIES);
        Properties properties = ProjectProperties.getProperties();
        assertNotNull(properties);
        assertTrue(!properties.stringPropertyNames().isEmpty());
    }
}