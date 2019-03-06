package eu.captech.digitalization.commons.basic.operations.sniff;

import eu.captech.digitalization.commons.basic.doc.Preamble;
import eu.captech.digitalization.commons.basic.exception.ExecutionException;

import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.Map;

@Preamble(
        lastModifiedBy = "Eduardo Melgar",
        creationDate = "1/30/13",
        creationTime = "3:25 PM",
        lastModified = "1/30/13"
)
public enum JvmSniffer {
    jvmSniffer;

    public static final String JVM_PID = "jvmPid";
    private static final String DECLARED_METHOD_GET_PROCESS_ID = "getProcessId";
    private static final String DECLARED_FIELD_JVM = "jvm";
    public static final char ARROBA_CHAR = '@';

    public int getNumberOfCpus() {
        return ManagementFactory.getOperatingSystemMXBean().getAvailableProcessors();
    }

    public Map<String, Number> getJvmHeapMemoryUsage() {
        Map<String, Number> memoryUsage = new HashMap<>();
        Runtime runtime = Runtime.getRuntime();
        /*
jvm as Long Objects in KB
*/
        long used = runtime.totalMemory() - runtime.freeMemory();
        memoryUsage.put(MemSniffer.MEM_USED_TAG, bytesToKB(used));
        memoryUsage.put(MemSniffer.MEM_TOTAL_TAG, bytesToKB(runtime.totalMemory()));
        memoryUsage.put(MemSniffer.MEM_FREE_TAG, bytesToKB(runtime.freeMemory()));
        // Heap max amount of memory
        memoryUsage.put(MemSniffer.MEM_MAX_TAG, bytesToKB(runtime.maxMemory()));
        /*
Percentage as Double Objects
*/
        memoryUsage.put(MemSniffer.MEM_FREE_PERCENTAGE_TAG, getPercentage(runtime.totalMemory(), runtime.freeMemory()));
        memoryUsage.put(MemSniffer.MEM_USED_PERCENTAGE_TAG, getPercentage(runtime.totalMemory(), used));
        return memoryUsage;
    }

    public int getPid() throws ExecutionException {

        String processName = ManagementFactory.getRuntimeMXBean().getName();
        try {
            return Integer.parseInt(processName.substring(0, processName.indexOf(ARROBA_CHAR)));
        }
        catch (NumberFormatException e) {
            throw new ExecutionException("Unable to acquire pid fo process name " + processName + ": " + e.getMessage(), e);
        }
    }

//    public int getPidByReflection() throws ExecutionException {
//        RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();
//        Field jvm = null;
//        try {
//            jvm = runtime.getClass().getDeclaredField(DECLARED_FIELD_JVM);
//            jvm.setAccessible(true);
//            VMManagement mgmt = null;
//            try {
//                mgmt = (VMManagement) jvm.get(runtime);
//                Method pid_method = null;
//                try {
//                    pid_method = mgmt.getClass().getDeclaredMethod(DECLARED_METHOD_GET_PROCESS_ID);
//                    pid_method.setAccessible(true);
//                    try {
//                        return (Integer) pid_method.invoke(mgmt);
//                    }
//                    catch (InvocationTargetException e) {
//                        throw new ExecutionException("Unable to invoke Method: " + e.getMessage(), e);
//                    }
//                }
//                catch (NoSuchMethodException e) {
//                    throw new ExecutionException("Unable to acquire Method " + DECLARED_METHOD_GET_PROCESS_ID + ": " + e.getMessage(), e);
//                }
//            }
//            catch (IllegalAccessException e) {
//                throw new ExecutionException("Unable to acquire VM Management instance: " + e.getMessage(), e);
//            }
//        }
//        catch (NoSuchFieldException e) {
//            throw new ExecutionException("Unable to acquire field " + DECLARED_FIELD_JVM + ": " + e.getMessage(), e);
//        }
//    }

    private double getPercentage(long total, long part) {
        return (part * 100) / total;
    }

    private static double bytesToKB(long bytes) {
        return bytes / 1024;
    }
}
