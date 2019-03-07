package eu.captech.digitalization.commons.basic.operations.sniff;

import eu.captech.digitalization.commons.basic.BasicOsCommonsTest;
import eu.captech.digitalization.commons.basic.doc.Preamble;
import eu.captech.digitalization.commons.basic.exception.ExecutionException;
import org.hyperic.sigar.ProcCpu;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import org.hyperic.sigar.SigarProxy;
import org.hyperic.sigar.SigarProxyCache;
import org.hyperic.sigar.cmd.Ps;
import org.hyperic.sigar.cmd.Shell;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import javax.management.MBeanServer;
import java.io.File;
import java.lang.management.CompilationMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ThreadMXBean;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static eu.captech.digitalization.commons.basic.operations.sniff.CpuSniffer.cpuSniffer;
import static eu.captech.digitalization.commons.basic.operations.sniff.DfSniffer.DF_AVAILABLE;
import static eu.captech.digitalization.commons.basic.operations.sniff.MemSniffer.JVM_MEMORY_TYPE;
import static eu.captech.digitalization.commons.basic.operations.sniff.MemSniffer.MEM_MEMORY_TYPE;
import static eu.captech.digitalization.commons.basic.operations.sniff.MemSniffer.SWAP_MEMORY_TYPE;
import static org.junit.rules.ExpectedException.none;

@Preamble(
        lastModifiedBy = "Eduardo Melgar",
        creationDate = "1/29/13",
        creationTime = "4:25 PM",
        lastModified = "1/29/13"
)
public class SnifferUtilsIntegrationTest extends BasicOsCommonsTest {
    private static final String HEADER = "PID\t\tUSER\tSTIME\t\tSIZE\t\tRSS\t\t\tSHARE\t\tSTATE\tTIME\t%CPU\t\tCOMMAND";
    private static final int SLEEP_TIME = 1000;
    private static final int ROUNDS = 20;
    private static final String JAVA_LIBRARY_PATH = "java.library.path";
    private static final int FACTOR_1024 = 1024;
    private SnifferUtils snifferUtils;
    @Rule
    public ExpectedException expectedException = none();

    @Before
    public void setUp() throws Exception {
        logger = getLoggerFor(this.getClass());
        super.preMethodSetup();
        Path osDependencies = Paths.get(testResources.toString(), "osDependencies");
        System.setProperty(JAVA_LIBRARY_PATH, System.getProperty(JAVA_LIBRARY_PATH) +
                File.pathSeparator + osDependencies.toString());
        logger.info("OS Dependencies: " + osDependencies);
        logger.info("Global OS Dependencies: " + System.getProperty(JAVA_LIBRARY_PATH));
        snifferUtils = SnifferUtils.getInstance();
    }

    @After
    public void tearDown() {
        super.postMethodSetup();
    }

    @Test
    public void testGetDiskUsage() throws Exception {
        String method = Thread.currentThread().getStackTrace()[1].getMethodName();
        try {
            startingTestMethod(method);
            Path dir = Paths.get(".");
            logger.info("Directory '" + dir + "': " + getDoubleValue(snifferUtils.getDiskUsage(dir), false) + " MB.");

        }
        finally {
            finishingTestMethod(method);
        }
    }

    @Test
    public void testGetDeviceDiskUsage() {
        String method = Thread.currentThread().getStackTrace()[1].getMethodName();
        try {
            startingTestMethod(method);
            File[] roots = File.listRoots();
            for (File root : roots) {
                logger.info("******************** '" + root.getAbsolutePath() + "' *****************");
                logger.info("Partition '" + root.getAbsolutePath() + "': " +
                        getDoubleValue(snifferUtils.getDeviceDiskUsage(root.getAbsolutePath(), DF_AVAILABLE), false) + " MB.");
            }
        }
        finally {
            finishingTestMethod(method);
        }
    }

    @Test
    public void getFileSystems() {
        String method = Thread.currentThread().getStackTrace()[1].getMethodName();
        try {
            startingTestMethod(method);
            Map<String, Map<String, String>> fileSystems = snifferUtils.getFileSystems();
            for (String fileSystem : fileSystems.keySet()) {
                logger.info("***********************************");
                for (String s : fileSystems.get(fileSystem).keySet()) {
                    boolean isPercentage = false;
                    String med = " MB.";
                    Object value = null;
                    if (s.endsWith("Percentage")) {
                        isPercentage = true;
                        med = " %.";
                    }
                    else if (s.endsWith("Name")) {
                        isPercentage = true;
                        med = "";
                    }
                    if (!isPercentage) {
                        try {
                            value = getDoubleValue(Double.parseDouble(fileSystems.get(fileSystem).get(s)), false);
                        }
                        catch (NumberFormatException e) {
                            logger.info(e.getMessage());
                        }
                    }
                    else {
                        value = fileSystems.get(fileSystem).get(s);
                    }
                    logger.info("FileSystem '" + fileSystem + "' :: " + s + ": " + value + med);
                }
            }
        }
        finally {
            finishingTestMethod(method);
        }
    }

    @Test
    public void getJvmCpuTime() throws ExecutionException {
        String method = Thread.currentThread().getStackTrace()[1].getMethodName();
        try {
            startingTestMethod(method);
            Map<String, Number> cpuUsage = snifferUtils.getJvmCpuTime();
            printCpuUsage(cpuUsage);
        }
        finally {
            finishingTestMethod(method);
        }
    }

    @Test
    public void getMemByMemoryType() throws Exception {
        String method = Thread.currentThread().getStackTrace()[1].getMethodName();
        try {
            startingTestMethod(method);
            getByMemoryType(MEM_MEMORY_TYPE);
        }
        finally {
            finishingTestMethod(method);
        }
    }

    @Test
    public void getSwapByMemoryType() throws Exception {
        String method = Thread.currentThread().getStackTrace()[1].getMethodName();
        try {
            startingTestMethod(method);
            getByMemoryType(SWAP_MEMORY_TYPE);
        }
        finally {
            finishingTestMethod(method);
        }
    }

    @Test
    public void getJvmByMemoryType() throws Exception {
        String method = Thread.currentThread().getStackTrace()[1].getMethodName();
        try {
            startingTestMethod(method);
            getByMemoryType(JVM_MEMORY_TYPE);
        }
        finally {
            finishingTestMethod(method);
        }
    }

    @Test
    public void testGetCpuTime() throws Exception {
        String method = Thread.currentThread().getStackTrace()[1].getMethodName();
        try {
            startingTestMethod(method);
            Map<String, Number> map = snifferUtils.getCpuTime();
            for (String s : map.keySet()) {
                Double value = (Double) map.get(s) * 100;
                logger.info("CPU '" + s + "': " + getDoubleValue(value, true) + " %.");
            }
        }
        finally {
            finishingTestMethod(method);
        }
    }

    @Test
    public void getCpuTimeByPid() throws ExecutionException {
        String method = Thread.currentThread().getStackTrace()[1].getMethodName();
        try {
            startingTestMethod(method);
            Map<String, Number> cpuUsage = snifferUtils.getCpuTimeByPid(snifferUtils.getApplicationPid());
            printCpuUsage(cpuUsage);
        }
        finally {
            finishingTestMethod(method);
        }
    }

    @Test
    public void testGetNumberOfCPU() {
        String method = Thread.currentThread().getStackTrace()[1].getMethodName();
        try {
            startingTestMethod(method);
            logger.info("Number of CPUs: " + snifferUtils.getNumberOfCPU());
        }
        finally {
            finishingTestMethod(method);
        }
    }

    @Test
    public void getAllMemories() throws Exception {
        String method = Thread.currentThread().getStackTrace()[1].getMethodName();
        try {
            startingTestMethod(method);
            Map<String, Map<String, Number>> memories = snifferUtils.getAllMemories();
            for (String mks : memories.keySet()) {
                logger.info("***********************************");
                for (String s : memories.get(mks).keySet()) {
                    Double value = (Double) memories.get(mks).get(s);
                    boolean isPercentage = false;
                    if (s.endsWith("Percentage")) {
                        isPercentage = true;
                    }
                    logger.info("Memory '" + mks + "' :: " + s + ": " + getDoubleValue(value, isPercentage) +
                            ((isPercentage) ? " %." : " MB."));
                }
            }
        }
        finally {
            finishingTestMethod(method);
        }
    }

    @Test
    public void getApplicationPid() throws ExecutionException {
        String method = Thread.currentThread().getStackTrace()[1].getMethodName();
        try {
            startingTestMethod(method);
            logger.info("JVM pid '" + snifferUtils.getApplicationPid() + "'");
        }
        finally {
            finishingTestMethod(method);
        }
    }

    @Test
    public void printJvmByManagementFactory() {
        String method = Thread.currentThread().getStackTrace()[1].getMethodName();
        try {
            startingTestMethod(method);
            int mb = 1024 * 1024;
            char tab = '\t';
            logger.info("*************************************");
            List memBeans = ManagementFactory.getMemoryPoolMXBeans();
            for (Object memBean : memBeans) {

                MemoryPoolMXBean mpool = (MemoryPoolMXBean) memBean;
                MemoryUsage usage = mpool.getUsage();

                String name = mpool.getName();
                float init = usage.getInit() / mb;
                float used = usage.getUsed() / mb;
                float committed = usage.getCommitted() / mb;
                float max = usage.getMax() / mb;
                float pctUsed = (used / max) * 100;
                float pctCommitted = (committed / max) * 100;
                logger.info(name
                        + tab
                        + init
                        + tab
                        + used
                        + tab
                        + committed
                        + tab
                        + max
                        + tab
                        + pctUsed
                        + tab
                        + pctCommitted);
            }
            logger.info("*************************************");
            MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
            logger.info(mBeanServer.getDefaultDomain());
            logger.info("*************************************");
            final MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
            memoryMXBean.setVerbose(true);
            logger.info("Used Memory:" + (memoryMXBean.getHeapMemoryUsage().getUsed()) / mb);
            logger.info("Init:" + memoryMXBean.getHeapMemoryUsage().getInit() / mb);
            logger.info("Max:" + memoryMXBean.getHeapMemoryUsage().getMax() / mb);
            logger.info("Commited:" + memoryMXBean.getHeapMemoryUsage().getCommitted() / mb);
            logger.info("HeapMemoryUsage --> " + memoryMXBean.getHeapMemoryUsage().toString());
            logger.info(memoryMXBean.getNonHeapMemoryUsage().toString());
            logger.info("*************************************");
            ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
            logger.info(Arrays.toString(threadMXBean.findDeadlockedThreads()));
            logger.info(Arrays.toString(threadMXBean.findMonitorDeadlockedThreads()));
            logger.info("{}", threadMXBean.getCurrentThreadCpuTime());
            logger.info("{}", threadMXBean.getCurrentThreadUserTime());
            logger.info("{}", threadMXBean.getPeakThreadCount());
            logger.info("*************************************");
            RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
            logger.info(runtimeMXBean.getBootClassPath());
            logger.info(runtimeMXBean.getClassPath());
            logger.info("{}", runtimeMXBean.getInputArguments());
            logger.info(runtimeMXBean.getLibraryPath());
            logger.info(runtimeMXBean.getName());
            logger.info(runtimeMXBean.getSpecName());
            logger.info(runtimeMXBean.getSpecVersion());
            logger.info(runtimeMXBean.getSpecVendor());
            logger.info("{}", runtimeMXBean.getStartTime());
            logger.info("{}", runtimeMXBean.getSystemProperties());
            logger.info("{}", runtimeMXBean.getUptime());
            logger.info(runtimeMXBean.getVmName());
            logger.info(runtimeMXBean.getVmVendor());
            logger.info(runtimeMXBean.getVmVersion());
            logger.info("*************************************");
            OperatingSystemMXBean operatingSystemMXBean = ManagementFactory.getOperatingSystemMXBean();
            logger.info(operatingSystemMXBean.getArch());
            logger.info("{}", operatingSystemMXBean.getAvailableProcessors());
            logger.info(operatingSystemMXBean.getName());
            logger.info("{}", operatingSystemMXBean.getSystemLoadAverage());
            logger.info(operatingSystemMXBean.getVersion());
            logger.info("*************************************");
            CompilationMXBean compilationMXBean = ManagementFactory.getCompilationMXBean();
            logger.info(compilationMXBean.getName());
            logger.info("{}", compilationMXBean.getTotalCompilationTime());
            logger.info("{}", compilationMXBean.isCompilationTimeMonitoringSupported());
        }
        finally {
            finishingTestMethod(method);
        }
    }

    @Test
    @Ignore("Proof of concept")
    @SuppressWarnings("unchecked")
    public void testCpu() throws Exception {
        String method = Thread.currentThread().getStackTrace()[1].getMethodName();
        try {
            startingTestMethod(method);
            Sigar sigarImpl = new Sigar();

            SigarProxy sigar = SigarProxyCache.newInstance(sigarImpl, SLEEP_TIME);

            logger.info(HEADER);
            boolean running = true;
            int counter = 0;
            double averageCpuPercentage = 0.0;
            double cpuPercentage;
            while (running) {
                long[] pids = Shell.getPids(sigar, new String[0]);
                List info;
                try {
                    info = Ps.getInfo(sigar, pids[0]);
                }
                catch (SigarException e) {
                    continue; //process may have gone away
                }
                try {
                    ProcCpu cpu = sigar.getProcCpu(pids[0]);
                    cpuPercentage = cpu.getPercent();
                    averageCpuPercentage += cpuPercentage;
                    info.add(info.size() - 1, String.valueOf(cpuSniffer.getFormattedTime(cpuPercentage)));
                }
                catch (SigarException ignore) {
                }
                logger.info(join(info));

                Thread.sleep(SLEEP_TIME);
                SigarProxyCache.clear(sigar);
                counter++;
                if (counter >= ROUNDS) {
                    running = false;
                }
            }
            logger.info("Average CPU Usage after %d is %s %",
                    ROUNDS, cpuSniffer.getFormattedTime(averageCpuPercentage / ROUNDS) + " %");
        }
        finally {
            finishingTestMethod(method);
        }
    }

    private void getByMemoryType(String memoryType) throws ExecutionException {
        Map<String, Number> map = snifferUtils.getByMemoryType(memoryType);
        for (String s : map.keySet()) {
            Double value = (Double) map.get(s);
            boolean isPercentage = false;
            if (s.endsWith("Percentage")) {
                isPercentage = true;
            }
            logger.info("Memory '" + memoryType + "' :: " + s + ": " + getDoubleValue(value, isPercentage) +
                    ((isPercentage) ? " %." : " MB."));
        }
    }

    private static String join(List info) {
        StringBuilder buf = new StringBuilder();
        Iterator i = info.iterator();
        boolean hasNext = i.hasNext();
        while (hasNext) {
            buf.append((String) i.next());
            hasNext = i.hasNext();
            if (hasNext) {
                buf.append("\t\t");
            }
        }

        return buf.toString().trim();
    }

//    @Test
//    public void testSniffer() throws Exception {
//        String dirName = ".";
//        logger.info("DiskUsage for " + dirName + ": " + snifferUtils.getDeviceDiskUsage(dirName, DfSniffer.DF_AVAILABLE));
//        logger.info("*****************************************************************");
//        logger.info(psSniffer.getPid());
//        logger.info(psSniffer.getPSMemInformation(psSniffer.getPid()));
//        logger.info(psSniffer.getPSMemInformation(2712));
//        getMemByMemoryType();
//        logger.info("*****************************************************************");
//    }

    //    @Test
    private void printJvmByRuntime() {

        int mb = 1024 * 1024;

        //Getting the runtime reference from system
        Runtime runtime = Runtime.getRuntime();

        logger.info("##### Heap utilization statistics [MB] #####");
        //Print used memory
        logger.info("Used Memory:" + (runtime.totalMemory() - runtime.freeMemory()) / mb);
        //Print free memory
        logger.info("Free Memory:" + runtime.freeMemory() / mb);
        //Print total available memory
        logger.info("Total Memory:" + runtime.totalMemory() / mb);
        //Print Maximum available memory
        logger.info("Max Memory:" + runtime.maxMemory() / mb);
        logger.info("Processors:" + runtime.availableProcessors());

        /* Get a list of all filesystem roots on this system */
        File[] roots = File.listRoots();

        /* For each filesystem root, print some info */
        for (File root : roots) {
            logger.info("******************** '" + root.getAbsolutePath() + "' *****************");
            logger.info("Total space (MB): " + root.getTotalSpace() / mb);
            logger.info("Free space (MB): " + root.getFreeSpace() / mb);
            logger.info("Usable space (MB): " + root.getUsableSpace() / mb);
        }
        logger.info("******************************************************************************");
    }

    private void printCpuUsage(Map<String, Number> cpuUsage) throws ExecutionException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yy HH:mm:ss,SSS");
        for (String s : cpuUsage.keySet()) {
            if (s.endsWith("StartTime")) {
                logger.info("JVM Cpu Time for pid '" + snifferUtils.getApplicationPid() + "' :: " + s + ": " +
                        simpleDateFormat.format(new Date((Long) cpuUsage.get(s))) + " -> " + cpuUsage.get(s));
            }
            else if (s.endsWith("LastAccessedTime")) {
                logger.info("JVM Cpu Time for pid '" + snifferUtils.getApplicationPid() + "' :: " + s + ": " +
                        simpleDateFormat.format(new Date((Long) cpuUsage.get(s) + (Long) cpuUsage.get(CpuSniffer.CPU_PID_START_TIME)))
                        + " -> " + cpuUsage.get(s));
            }
            else {
                logger.info("JVM Cpu Time for pid '" + snifferUtils.getApplicationPid() + "' :: " + s + ": " + cpuUsage.get(s));
            }
        }
    }

    private double getDoubleValue(Number value, boolean percentage) {
        BigDecimal bd = null;
        if (value instanceof Double) {
            bd = new BigDecimal((percentage) ? (Double) value : ((Double) value / SnifferUtilsIntegrationTest.FACTOR_1024));
        }
        else if (value instanceof Integer) {
            bd = new BigDecimal((percentage) ? (Integer) value : ((Integer) value / SnifferUtilsIntegrationTest.FACTOR_1024));
        }
        else if (value instanceof Long) {
            bd = new BigDecimal((percentage) ? (Long) value : ((Long) value / SnifferUtilsIntegrationTest.FACTOR_1024));
        }
        if (bd != null) {
            bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
            return bd.doubleValue();
        }
        return -1;
    }
}
