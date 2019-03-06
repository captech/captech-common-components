package eu.captech.digitalization.commons.basic.api;

import eu.captech.digitalization.commons.basic.doc.Preamble;
import eu.captech.digitalization.commons.basic.exception.ExecutionException;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import static eu.captech.digitalization.commons.basic.operations.sniff.CpuSniffer.cpuSniffer;
import static eu.captech.digitalization.commons.basic.operations.sniff.DfSniffer.dfSniffer;
import static eu.captech.digitalization.commons.basic.operations.sniff.DuSniffer.duSniffer;
import static eu.captech.digitalization.commons.basic.operations.sniff.JvmSniffer.jvmSniffer;
import static eu.captech.digitalization.commons.basic.operations.sniff.MemSniffer.*;
import static eu.captech.digitalization.commons.basic.operations.sniff.PsSniffer.psSniffer;

@Preamble(
        lastModifiedBy = "Eduardo Melgar",
        creationDate = "1/23/13",
        creationTime = "1:13 PM",
        lastModified = "1/23/13"
)
public abstract class AbstractSnifferUtils {
    public static final AtomicBoolean LIBRARY_PROPERTY_SET = new AtomicBoolean(false);
    protected static final Logger logger = LoggerFactory.getLogger(AbstractSnifferUtils.class);

    protected AbstractSnifferUtils() {
        setLibraryPathProperty();
    }

    public abstract void setLibraryPathProperty();

    /**
     * Gets the size of a directory folder
     *
     * @param dir Path object pointing to the directory to be measured
     * @return The size of the directory in KB or '0' if dir is null, doesn't exists or is not a directory
     * @throws ExecutionException If unable to gather the required information
     */
    public Long getDiskUsage(@NotNull Path dir) throws ExecutionException {
        return (byteToKB(duSniffer.getDiskUsage(dir)));
    }

    /**
     * Gets the size of a Device Partition
     *
     * @param dirName  The name of the Device
     * @param sizeType The size Type to be evaluated. Can be DfSniffer.DF_AVAILABLE, DfSniffer.DF_PERCENTAGE,
     *                 DfSniffer.DF_TOTAL or DfSniffer.DF_USED
     * @return The size of the partition in KB
     * @throws UnsupportedOperationException If the size type is not supported
     */
    public Long getDeviceDiskUsage(String dirName, String sizeType) {
        return dfSniffer.getDeviceDiskUsage(dirName, sizeType);
    }

    /**
     * Get a Map with the description of all available partitions of the host machine.
     *
     * @return a Map with the partition information.
     */
    public Map<String, Map<String, String>> getFileSystems() {
        return dfSniffer.getFileSystems();
    }

    /**
     * Gets the amount of memory used by the host
     *
     * @param memoryType The memory type to be evaluated. Can be MemSniffer.MEM_MEMORY_TYPE, MemSniffer.JVM_MEMORY_TYPE
     *                   or MemSniffer.SWAP_MEMORY_TYPE
     * @return A Map with the size information based on memory type. Keys can be: MemSniffer.MEM_USED_TAG, MemSniffer.,
     *         MemSniffer.MEM_FREE_TAG, MemSniffer.MEM_TOTAL_TAG, MemSniffer.MEM_FREE_PERCENTAGE_TAG,
     *         MemSniffer.MEM_USED_PERCENTAGE_TAG. For the MemSniffer.JVM_MEMORY_TYPE only exists an additional key:
     *         MemSniffer.MEM_MAX_TAG. This key defines the max amount of heap space that can be allocated for the application.
     * @throws UnsupportedOperationException If the memory type is not supported
     * @throws ExecutionException            If the information can't be acquired from the host
     */
    public Map<String, Number> getByMemoryType(String memoryType) throws ExecutionException {
        return memSniffer.getMemory(memoryType);
    }

    /**
     * Gets the CPU time usage for the host machine
     *
     * @return The CPU Time Usage for the host
     * @throws ExecutionException If the information can't be acquired from the host
     */
    public Map<String, Number> getCpuTime() throws ExecutionException {
        return cpuSniffer.getCpuTime();
    }

    public Map<String, Number> getCpuTimeByPid(int pid) throws ExecutionException {
        return cpuSniffer.getCpuTimeByPid(pid);
    }

    /**
     * @return
     * @throws ExecutionException
     */
    public Map<String, Number> getJvmCpuTime() throws ExecutionException {
        return cpuSniffer.getJvmCpuTime();
    }

    /**
     * Gets the number of CPU
     *
     * @return The number of CPUs
     */
    public Integer getNumberOfCPU() {
        return cpuSniffer.getNumberOfCPU();
    }

    /**
     * Gets the Application CPU usage based on application's pid number
     *
     * @param pid The pid of the application to be monitored
     * @return a Map with the CPU Usage information for this pid
     * @throws ExecutionException If the CPU usage information is not available
     */
    public Map<String, Number> getPSCpuInformation(long pid) throws ExecutionException {
        return psSniffer.getPSCpuInformation(pid);
    }

    /**
     * Gets the PID of the current running application (JVM)
     *
     * @return The PID for this application
     * @throws ExecutionException If the PID is not available
     */
    public int getApplicationPid() throws ExecutionException {
        return jvmSniffer.getPid();
//        return jvmSniffer.getPid();
    }

    public Map<String, Map<String, Number>> getAllMemories() throws ExecutionException {
        Map<String, Map<String, Number>> map = new HashMap<>();
        map.put(MEM_MEMORY_TYPE, memSniffer.getMemory(MEM_MEMORY_TYPE));
        map.put(JVM_MEMORY_TYPE, memSniffer.getMemory(JVM_MEMORY_TYPE));
        map.put(SWAP_MEMORY_TYPE, memSniffer.getMemory(SWAP_MEMORY_TYPE));
        return map;
    }

//    public Map<String, String> getPSInfo(long pid) throws ExecutionException {
//        return psSniffer.getPSInfo(pid);
//    }

    private static long byteToKB(long kb) {
        return (kb / 1024);
    }
}
