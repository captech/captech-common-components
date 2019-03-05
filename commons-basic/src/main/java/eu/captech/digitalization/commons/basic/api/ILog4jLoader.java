package eu.captech.digitalization.commons.basic.api;

import eu.captech.digitalization.commons.basic.doc.Preamble;
import eu.captech.digitalization.commons.basic.exception.ExecutionException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Preamble(
        lastModifiedBy = "Eduardo Melgar",
        creationDate = "11/30/12",
        creationTime = "9:53 AM",
        lastModified = "11/30/12"
)
public interface ILog4jLoader {
    void loadProperties(Class clazz);

    void loadDefaultLog4j();

    void readProperties(Properties defaultProps, InputStream inputStream) throws ExecutionException, IOException;

    String getLogPropertiesJarPath();

    void setLogPropertiesJarPath(String logPropertiesJarPath);

    File getLogPropertiesFile();

    void setLogPropertiesFile(File logPropertiesFile);

    Long getReloadDelay();

    void setReloadDelay(Long reloadDelay);
}
