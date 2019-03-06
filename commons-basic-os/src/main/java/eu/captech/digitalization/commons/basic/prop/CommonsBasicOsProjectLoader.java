package eu.captech.digitalization.commons.basic.prop;

import eu.captech.digitalization.commons.basic.doc.Preamble;

@Preamble(
        lastModifiedBy = "Eduardo Melgar",
        creationDate = "1/14/13",
        creationTime = "3:45 PM",
        lastModified = "1/14/13"
)
public class CommonsBasicOsProjectLoader {
    private static final String PROJECT_PROPERTIES = "/projects/commons-basic-os-artifact.properties";

    static {
        ProjectProperties.registerProjectProperties(CommonsBasicOsProjectLoader.class, PROJECT_PROPERTIES);
    }
}
