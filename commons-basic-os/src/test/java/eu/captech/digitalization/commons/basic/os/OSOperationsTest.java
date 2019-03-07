package eu.captech.digitalization.commons.basic.os;

import eu.captech.digitalization.commons.basic.BasicOsCommonsTest;
import eu.captech.digitalization.commons.basic.api.IOperations;
import eu.captech.digitalization.commons.basic.exception.ExecutionException;
import eu.captech.digitalization.commons.basic.operations.OsOperations;
import eu.captech.digitalization.commons.basic.operations.sniff.CpuSniffer;
import eu.captech.digitalization.commons.basic.operations.sniff.DfSniffer;
import eu.captech.digitalization.commons.basic.operations.sniff.JvmSniffer;
import eu.captech.digitalization.commons.basic.operations.sniff.MemSniffer;
import eu.captech.digitalization.commons.basic.operations.sniff.SnifferUtils;
import org.apache.commons.lang.SystemUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.rules.ExpectedException.none;

public class OSOperationsTest extends BasicOsCommonsTest {
    private static final String JAVA_LIBRARY_PATH = "java.library.path";
    private SnifferUtils snifferUtils;
    @Rule
    public ExpectedException expectedException = none();

    @Before
    public void setUp() throws Exception {
        logger = getLoggerFor(this.getClass());
        super.preMethodSetup();
        logger = LoggerFactory.getLogger(OSOperationsTest.class);
        Path osDependencies = Paths.get(testResources.toString(), "osDependencies");
        System.setProperty(JAVA_LIBRARY_PATH, System.getProperty(JAVA_LIBRARY_PATH) +
                File.pathSeparator + osDependencies.toString());
        logger.info("OS Dependencies: " + osDependencies);
        logger.info("Global OS Dependencies: {}", System.getProperty(JAVA_LIBRARY_PATH));
        snifferUtils = SnifferUtils.getInstance();
    }

    @After
    public void tearDown() {
        super.postMethodSetup();
    }

    @Test
    public void checkOperations() {
        String method = Thread.currentThread().getStackTrace()[1].getMethodName();
        try {
            startingTestMethod(method);
            IOperations operations = new OsOperations();
            logger.info("Result:\n{}", operations.getOperationsInfo(JvmSniffer.JVM_PID));
        }
        finally {
            finishingTestMethod(method);
        }
    }

    @Test
    public void getNumberOfCPUs() {
        String method = Thread.currentThread().getStackTrace()[1].getMethodName();
        try {
            startingTestMethod(method);
            logger.info("Number of CPUs: {}", snifferUtils.getNumberOfCPU());
        }
        finally {
            finishingTestMethod(method);
        }
    }

    @Test
    public void checkDiskUsage() throws ExecutionException {
        String method = Thread.currentThread().getStackTrace()[1].getMethodName();
        try {
            startingTestMethod(method);
            Path home = Paths.get(".");
            long diskUsage = snifferUtils.getDiskUsage(home);
            Assert.assertNotEquals("Directory file should return size bigger than 0.", 0, diskUsage);
            logger.info(home.toAbsolutePath().toString() + " - " + diskUsage);
            home = Paths.get("pom.xml");
            diskUsage = snifferUtils.getDiskUsage(home);
            Assert.assertEquals("Regular file should return 0 size.", 0, diskUsage);
            logger.info(home.toAbsolutePath().toString() + " - " + diskUsage);
        }
        finally {
            finishingTestMethod(method);
        }
    }

//    @Test
//    @Ignore("For testing given PIDs")
//    public void checkPSInfo() throws Exception {
//        logger.info("Starting method " + Thread.currentThread().getStackTrace()[1].getMethodName());
//        Map<String, String> info = snifferUtils.getPSInfo(8852);
//        for (String s : info.keySet()) {
//            logger.info(s + ":" + info.get(s));
//        }
//    }

//    @Test
//    public void checkPSInfoError() throws Exception {
//        logger.info("Starting method " + Thread.currentThread().getStackTrace()[1].getMethodName());
//        expectedException.expect(ExecutionException.class);
//        snifferUtils.getPSInfo(-1);
//    }

    @Test
    public void getDeviceInfo() {
        String method = Thread.currentThread().getStackTrace()[1].getMethodName();
        try {
            startingTestMethod(method);
            logger.info("percentage: {}", snifferUtils.getDeviceDiskUsage("/", DfSniffer.DF_PERCENTAGE));
            logger.info("used: {}", snifferUtils.getDeviceDiskUsage("/", DfSniffer.DF_USED));
            logger.info("available: {}", snifferUtils.getDeviceDiskUsage("/", DfSniffer.DF_AVAILABLE));
            logger.info("total: {}", snifferUtils.getDeviceDiskUsage("/", DfSniffer.DF_TOTAL));
            expectedException.expect(UnsupportedOperationException.class);
            snifferUtils.getDeviceDiskUsage("/", DfSniffer.DF_DEV_NAME);
        }
        finally {
            finishingTestMethod(method);
        }
    }

    @Test
    public void getJVMMemoryInfo() {
        String method = Thread.currentThread().getStackTrace()[1].getMethodName();
        try {
            startingTestMethod(method);
            OsOperations osOperations = new OsOperations();
            String operationsInfo = osOperations.getOperationsInfo(MemSniffer.JVM_MEMORY_TYPE);

            String[] jvmMemoryInfo = operationsInfo.split(SystemUtils.LINE_SEPARATOR);
            for (String jvmInfo : jvmMemoryInfo) {
                if (jvmInfo.contains(MemSniffer.MEM_USED_PERCENTAGE_TAG)) {
                    logger.info("used % : " + jvmInfo.split(":")[1]);
                }
            }
        }
        finally {
            finishingTestMethod(method);
        }
    }

    @Test
    public void getJVMCPUInfo() {
        String method = Thread.currentThread().getStackTrace()[1].getMethodName();
        try {
            startingTestMethod(method);
            OsOperations osOperations = new OsOperations();
            String operationsInfo = osOperations.getOperationsInfo(CpuSniffer.CPU_PID_PERCENTAGE);
            logger.info(operationsInfo);
            logger.info("{}", Double.valueOf(operationsInfo.split(":")[1]));
        }
        finally {
            finishingTestMethod(method);
        }
    }
}
