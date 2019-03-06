//package eu.nets.datafangst.commons.basic.log;
//
//import eu.captech.digitalization.commons.basic.api.ILog4jLoader;
//import eu.captech.digitalization.commons.basic.doc.Preamble;
//import eu.captech.digitalization.commons.basic.exception.ExecutionException;
//
//import java.io.File;
//import java.io.IOException;
//import java.io.InputStream;
//import java.util.Properties;
//
//@Preamble(
//        lastModifiedBy = "Eduardo Melgar",
//        creationDate = "11/30/12",
//        creationTime = "9:55 AM",
//        lastModified = "11/30/12"
//)
//public class Log4jLoaderImpl implements ILog4jLoader {
//
//    private Long reloadDelay = 5000L;
//    private String logPropertiesJarPath = "/properties/log4j.properties";
//    private File logPropertiesFile = new File("properties/log4j.properties");
//
//    @Override
//    public void loadProperties(Class clazz) {
//        PropertyConfigurator.configure(defaultLog4jProperties());
//        Logger logger = LoggerFactory.getLogger(clazz);
//        if (logger.isDebugEnabled()) {
//            logger.debug("Log4j default properties loaded...");
//        }
//        try {
//            InputStream inputStream = clazz.getResourceAsStream(getLogPropertiesJarPath());
//            if (inputStream != null) {
//                Properties properties = new Properties();
//                readProperties(properties, inputStream);
//                if (!properties.isEmpty()) {
//                    PropertyConfigurator.configure(properties);
//                    if (logger.isInfoEnabled()) {
//                        logger.info("Log4j Jar Properties loaded");
//                    }
//                }
//            }
//        }
//        catch (ExecutionException | IOException e) {
//            logger.warn("Log4J called from test or JAR file can be corrupt. Standard Properties could not be read, using defaults. Reason: " + e.toString(), e);
//        }
//        if (logPropertiesFile != null && logPropertiesFile.exists() && logPropertiesFile.isFile()) {
//            PropertyConfigurator.configureAndWatch(logPropertiesFile.getAbsolutePath(), reloadDelay);
//            if (logger.isInfoEnabled()) {
//                logger.info("Properties read from file " + logPropertiesFile.getAbsolutePath());
//            }
//        }
//    }
//
//    @Override
//    public void loadDefaultLog4j() {
//        PropertyConfigurator.configure(defaultLog4jProperties());
//    }
//
//    private Properties defaultLog4jProperties() {
//        Properties properties = new Properties();
//        properties.put("log4j.rootLogger", "trace, SERVERLOG");
//        properties.put("log4j.appender.SERVERLOG", "org.apache.log4j.ConsoleAppender");
//        properties.put("log4j.appender.SERVERLOG.layout", "org.apache.log4j.PatternLayout");
//        properties.put("log4j.appender.SERVERLOG.layout.ConversionPattern", "%-5p %d{[HH:mm:ss,SSS]}  [%-20.20c{1},%-10.10M] %m%n");
//        return properties;
//    }
//
//    @Override
//    public void readProperties(Properties defaultProps, InputStream inputStream) throws ExecutionException, IOException {
//        int size;
//        try {
//            size = inputStream.available();
//        }
//        catch (NullPointerException | IOException e) {
//            throw new ExecutionException("Error loading the properties file [InputStream available: 0]: " + e.getMessage());
//        }
//        if (size > 0) {
//            try {
//                defaultProps.load(inputStream);
//            }
//            catch (NullPointerException | IOException e) {
//                throw new ExecutionException("Error loading the properties file [available: " + size + "]: " + e.getMessage());
//            }
//            finally {
//                inputStream.close();
////                try {
////                }
////                catch (IOException e) {
////                    System.err.println(e.getMessage());
////                    throw new ExecutionException("Error closing the properties file [available: " + size + "]: " + e.getMessage());
////                }
//            }
//        }
////        finally {
////            if (statics.isDebug) {
////                System.out.println(sortProperties(defaultProps));
////            }
////        }
//    }
//
//    @Override
//    public String getLogPropertiesJarPath() {
//        return logPropertiesJarPath;
//    }
//
//    @Override
//    public void setLogPropertiesJarPath(String logPropertiesJarPath) {
//        this.logPropertiesJarPath = logPropertiesJarPath;
//    }
//
//    @Override
//    public File getLogPropertiesFile() {
//        return logPropertiesFile;
//    }
//
//    @Override
//    public void setLogPropertiesFile(File logPropertiesFile) {
//        this.logPropertiesFile = logPropertiesFile;
//    }
//
//    @Override
//    public Long getReloadDelay() {
//        return reloadDelay;
//    }
//
//    @Override
//    public void setReloadDelay(Long reloadDelay) {
//        this.reloadDelay = reloadDelay;
//    }
//}
