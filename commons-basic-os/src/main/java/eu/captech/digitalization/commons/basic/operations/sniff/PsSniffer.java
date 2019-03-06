package eu.captech.digitalization.commons.basic.operations.sniff;

import eu.captech.digitalization.commons.basic.doc.Preamble;
import eu.captech.digitalization.commons.basic.exception.ExecutionException;
import org.hyperic.sigar.*;
import org.hyperic.sigar.cmd.Shell;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Preamble(
        lastModifiedBy = "Eduardo Melgar",
        creationDate = "1/29/13",
        creationTime = "3:38 PM",
        lastModified = "1/29/13"
)
public enum PsSniffer {
    psSniffer;
    public static final String PS_UNKNOWN_ENTRY = "unknown";
    public static final Integer UNKNOWN_ENTRY = -1;
    public static final String PS_PID_ENTRY = "pid";
    public static final String PS_USER_ENTRY = "user";
    public static final String PS_START_TIME_ENTRY = "startTime";
    public static final String PS_MEM_SIZE_ENTRY = "memSize";
    public static final String PS_MEM_RSS_SIZE_ENTRY = "memRssSize";
    public static final String PS_MEM_SHARE_SIZE_ENTRY = "memShareSize";
    public static final String PS_STATE_ENTRY = "state";
    public static final String PS_CPU_TIME_ENTRY = "cpuTime";
    public static final String PS_NAME_ENTRY = "name";
    public static final String DATE_FORMAT_START_TIME = "dd.MM.yy-HH:mm";


    public Map<String, Number> getPSCpuInformation(long pid) throws ExecutionException {
        Sigar sigar = (new Shell()).getSigar();
        ProcTime time = null;
        try {
            time = sigar.getProcTime(pid);
        }
        catch (SigarException e) {
            throw new ExecutionException("Unable to get CPU Time information: " + e.getMessage(), e);
        }
        Map<String, Number> info = new HashMap<>();
        info.put(PS_START_TIME_ENTRY, time.getStartTime());
        info.put(PS_CPU_TIME_ENTRY, time.getTotal());
        return info;
    }

    @SuppressWarnings(value = "deprecation")
    public Map<String, Number> getPSMemInformation(long pid) throws ExecutionException {
        Sigar sigar = (new Shell()).getSigar();
        if (pid < 1) {
            throw new ExecutionException("Unable to get state information for a negative PID ");
        }
        Map<String, Number> info = new HashMap<>();

        try {
            ProcMem mem = sigar.getProcMem(pid);
            info.put(PS_MEM_SIZE_ENTRY, bytesToKB(mem.getSize()));
            info.put(PS_MEM_RSS_SIZE_ENTRY, bytesToKB((mem.getRss())));
            info.put(PS_MEM_SHARE_SIZE_ENTRY, bytesToKB((mem.getShare())));
        }
        catch (SigarException e) {
            info.put(PS_MEM_SIZE_ENTRY, UNKNOWN_ENTRY);
            info.put(PS_MEM_RSS_SIZE_ENTRY, UNKNOWN_ENTRY);
            info.put(PS_MEM_SHARE_SIZE_ENTRY, UNKNOWN_ENTRY);
        }
        return info;
    }

    public boolean isRunning(long pid) {
    	
         if (pid < 1) {
             return false;
         }
         Sigar sigar = (new Shell()).getSigar();
         try {
             sigar.getProcState(pid);
         } catch (SigarException e) {
             return false;
         } finally {
        	 sigar.close();
         }
         return true;
    }
    
    @SuppressWarnings(value = "deprecation")
    public Map<String, String> getPSInfo(long pid) throws ExecutionException {
        Sigar sigar = (new Shell()).getSigar();
        if (pid < 1) {
            throw new ExecutionException("Unable to get state information for a negative PID ");
        }
        ProcState state = null;
        try {
            state = sigar.getProcState(pid);
        }
        catch (SigarException e) {
            throw new ExecutionException("Unable to get state information: " + e.getMessage(), e);
        }
        ProcTime time = null;
        java.lang.String unknown = PS_UNKNOWN_ENTRY;

        java.util.Map<java.lang.String, java.lang.String> info = new HashMap<>();
        info.put(PS_PID_ENTRY, java.lang.String.valueOf(pid));

        try {
            ProcCredName cred = sigar.getProcCredName(pid);
            info.put(PS_USER_ENTRY, cred.getUser());
        }
        catch (SigarException e) {
            info.put(PS_USER_ENTRY, unknown);
        }

        try {
            time = sigar.getProcTime(pid);
            info.put(PS_START_TIME_ENTRY, getStartTime(time.getStartTime()));
        }
        catch (SigarException e) {
            info.put(PS_START_TIME_ENTRY, unknown);
        }

        try {
            ProcMem mem = sigar.getProcMem(pid);
            info.put(PS_MEM_SIZE_ENTRY, Sigar.formatSize(mem.getSize()));
            info.put(PS_MEM_RSS_SIZE_ENTRY, Sigar.formatSize(mem.getRss()));
            info.put(PS_MEM_SHARE_SIZE_ENTRY, Sigar.formatSize(mem.getShare()));
        }
        catch (SigarException e) {
            info.put(PS_MEM_SIZE_ENTRY, unknown);
            info.put(PS_MEM_RSS_SIZE_ENTRY, unknown);
            info.put(PS_MEM_SHARE_SIZE_ENTRY, unknown);
        }

        info.put(PS_STATE_ENTRY, java.lang.String.valueOf(state.getState()));

        if (time != null) {
            info.put(PS_CPU_TIME_ENTRY, getCpuTime(time));
        }
        else {
            info.put(PS_CPU_TIME_ENTRY, unknown);
        }

        java.lang.String name = null;
        try {
            name = ProcUtil.getDescription(sigar, pid);
        }
        catch (SigarException e) {
            throw new ExecutionException("Unable to get process information: " + e.getMessage(), e);
        }
        info.put(PS_NAME_ENTRY, name);

        return info;
    }

    public long getSigarPid() throws SigarException {
        Shell shell = new Shell();

        long[] pids = shell.findPids(new String[0]);

//        for(int i = 0; i < pids.length; i++) {
//            System.out.println(pids[i]);
//        }
        return pids[0];
    }

    private static String getCpuTime(long total) {
        long t = total / 1000;
        return t / 60 + ":" + t % 60;
    }

    private static String getCpuTime(ProcTime time) {
        return getCpuTime(time.getTotal());
    }

    private static String getStartTime(long time) {
        if (time == 0) {
            return "00:00";
        }
        return new SimpleDateFormat(DATE_FORMAT_START_TIME).format(new Date(time));
    }

    private static long bytesToKB(long bytes) {
//        System.out.println(Sigar.formatSize(bytes));
        return bytes / (1024);
    }
}
