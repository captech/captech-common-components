package eu.captech.digitalization.commons.basic.operations.sniff;

import eu.captech.digitalization.commons.basic.api.AbstractSnifferUtils;
import eu.captech.digitalization.commons.basic.doc.Preamble;

import java.io.File;

@Preamble(
        lastModifiedBy = "Eduardo Melgar",
        creationDate = "1/23/13",
        creationTime = "4:25 PM",
        lastModified = "1/23/13"
)
public class SnifferUtils extends AbstractSnifferUtils {

    private static SnifferUtils snifferUtils = null;
    private static final String JAVA_LIBRARY_PATH = "java.library.path";

    public synchronized static final SnifferUtils getInstance() {
        if (snifferUtils == null) {
            snifferUtils = new SnifferUtils();
        }
        return snifferUtils;
    }

    private SnifferUtils() {
        super();
    }

    @Override
    public void setLibraryPathProperty() {
        if (LIBRARY_PROPERTY_SET.compareAndSet(false, true)) {
            logger.info("Global OS Dependencies <<: " + System.getProperty(JAVA_LIBRARY_PATH));
            if (!System.getProperty(JAVA_LIBRARY_PATH).contains("bin/os-dependencies")) {
            	System.setProperty(JAVA_LIBRARY_PATH, System.getProperty(JAVA_LIBRARY_PATH) + File.pathSeparator + "bin/os-dependencies");
            }
        }
        if (logger.isInfoEnabled()) {
            logger.info("Global OS Dependencies: " + System.getProperty(JAVA_LIBRARY_PATH));
        }
    }
}
