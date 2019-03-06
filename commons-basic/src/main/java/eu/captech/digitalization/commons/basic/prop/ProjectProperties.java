package eu.captech.digitalization.commons.basic.prop;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

public class ProjectProperties {
    private static final Logger logger = LoggerFactory.getLogger(ProjectProperties.class);
    private static final String PROJECT_PROPERTIES = "/projects/commons-basic-artifact.properties";

    private static final Properties properties = new Properties();

    static {
        registerProjectProperties(ProjectProperties.class, PROJECT_PROPERTIES);
    }

    public synchronized static void registerProjectProperties(Class clazz, String path) {
        try {
            try (InputStream inputStream = clazz.getResourceAsStream(path)) {
                if (logger.isInfoEnabled()) {
                    logger.info("Loading project properties for " + path);
                }
                properties.load(inputStream);
            }
        }
        catch (IOException e) {
            logger.warn("Not able to read properties: " + e.getMessage(), e);
            System.exit(100);
        }
    }

    public synchronized static void printProperties() {
        Properties properties = getProperties();
        List<String> enumeration = new ArrayList<>(properties.stringPropertyNames());
        Collections.sort(enumeration);
        for (String key : enumeration) {
            logger.info(key + "\t\t::\t\t" + properties.get(key));
        }
    }

    public static Properties getProperties() {
        return properties;
    }
}
