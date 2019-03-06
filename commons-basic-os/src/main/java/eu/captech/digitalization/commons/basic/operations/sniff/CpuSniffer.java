package eu.captech.digitalization.commons.basic.operations.sniff;

import eu.captech.digitalization.commons.basic.doc.Preamble;
import eu.captech.digitalization.commons.basic.exception.ExecutionException;
import org.hyperic.sigar.*;
import org.hyperic.sigar.cmd.Shell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static eu.captech.digitalization.commons.basic.operations.sniff.JvmSniffer.jvmSniffer;

@Preamble(
        lastModifiedBy = "Eduardo Melgar",
        creationDate = "1/22/13",
        creationTime = "2:42 PM",
        lastModified = "1/22/13"
)
public enum CpuSniffer {
    cpuSniffer;
    private static final Logger logger = LoggerFactory.getLogger(CpuSniffer.class);
    private static final String STATUS_TAG = "status";
    private static final int OK_STATUS = 0;
    private static final int PARTIAL_ERROR_STATUS = 50;
    private static final int SYSTEM_ERROR_STATUS = 99;
    private static final NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);
    public static final String CPU_USER_TAG = "cpuUser";
    public static final String CPU_SYS_TAG = "cpuSys";
    public static final String CPU_IDLE_TAG = "cpuIdle";
    public static final String CPU_COMBINED_TAG = "cpuCombined";
    public static final String CPU_PID_SYS = "cpuPidSys";
    public static final String CPU_PID_USER = "cpuPidUser";
    public static final String CPU_PID_PERCENTAGE = "cpuPidPercentage";
    public static final String CPU_PID_LAST_ACCESSED_TIME = "cpuPidLastAccessedTime";
    public static final String CPU_PID_START_TIME = "cpuPidStartTime";
    public static final String CPU_PID_TOTAL = "cpuPidTotal";
    private static final String TIME_TAG = "time";
    public static final String USER_TAG = "user";
    public static final String SYS_TAG = "sys";
    public static final String IDLE_TAG = "idle";
    public static final String COMBINED_TAG = "combined";
    public static final String CPU_INFO_TAG = "cpuInfo";
    private static final String CORE_TAG = "core";
    private static final String ID_TAG = "id";
    private static final String CORE_INFO_TAG = "coreInfo";
    private static final String SYSTEM_ERROR_TAG = "systemError";
    private static final String CORE_INFOS_TAG = "coreInfos";
    private static final String TOTAL_CPUS_TAG = "totalCpus";
    private static final String MHZ_TAG = "mhz";
    private static final String MODEL_TAG = "model";
    private static final String VENDOR_TAG = "vendor";
    private static final String CPU_BASED_INFORMATION_COMMENT = "Cpu based Information";
    private static final String USAGE_COMMENT = "Core time usage information is represented in % of the total amount of process time";

    static {
        numberFormat.setMaximumFractionDigits(2);
    }

    public Document appendCpuInfo(Document document, Element parent, Shell shell) {
        String cpuInformationFetched = "";
        String cpuPercInformationFetched = "";
        String coreInformationFetched = "";
        Sigar sigar = shell.getSigar();
        org.hyperic.sigar.CpuInfo[] infos = new org.hyperic.sigar.CpuInfo[0];
        try {
            infos = sigar.getCpuInfoList();
        }
        catch (SigarException e) {
            String message = "Unable to fetch CPU Info: " + e.getMessage();
            logger.warn(message, e);
            cpuInformationFetched = message;
        }
        CpuPerc[] cpus = new CpuPerc[0];
        try {
            cpus = sigar.getCpuPercList();
        }
        catch (SigarException e) {
            String message = "Unable to fetch CPU Core Info: " + e.getMessage();
            logger.warn(message, e);
            coreInformationFetched = message;
        }
        CpuPerc cpu = null;
        try {
            cpu = sigar.getCpuPerc();
        }
        catch (SigarException e) {
            String message = "Unable to fetch CPU Core Total Info: " + e.getMessage();
            logger.warn(message, e);
            cpuPercInformationFetched = message;
        }


        Comment comment = document.createComment(CPU_BASED_INFORMATION_COMMENT);
        parent.appendChild(comment);
        Element cpuInfo = document.createElement(CPU_INFO_TAG);
        parent.appendChild(cpuInfo);
        Element status = document.createElement(STATUS_TAG);
        Element coreInfos = document.createElement(CORE_INFOS_TAG);

        if (infos.length <= 0 || !cpuInformationFetched.isEmpty()) {
            status.setTextContent(String.valueOf(SYSTEM_ERROR_STATUS));
            cpuInfo.appendChild(status);
            Element systemError = document.createElement(SYSTEM_ERROR_TAG);
            systemError.setTextContent(cpuInformationFetched);
            coreInfos.appendChild(systemError);
            cpuInfo.appendChild(coreInfos);
            return document;
        }


        if (cpus.length <= 0 || !coreInformationFetched.isEmpty() || cpu == null || !cpuPercInformationFetched.isEmpty()) {
            status.setTextContent(String.valueOf(PARTIAL_ERROR_STATUS));
            cpuInfo.appendChild(status);
            Element systemError = document.createElement(SYSTEM_ERROR_TAG);
            String errorMessage = ((cpuInformationFetched.isEmpty()) ? "" : cpuInformationFetched + ". ") +
                    ((cpuPercInformationFetched.isEmpty()) ? "" : cpuPercInformationFetched);
            systemError.setTextContent(errorMessage);
            coreInfos.appendChild(systemError);
            cpuInfo.appendChild(coreInfos);
            return document;
        }
        status.setTextContent(String.valueOf(OK_STATUS));
        cpuInfo.appendChild(status);
        CpuInfo info;
        CpuPerc cpui;
        for (int i = 0; i < cpus.length; i++) {
            info = infos[i];
            cpui = cpus[i];
            Element coreInfo = document.createElement(CORE_INFO_TAG);
            Element vendor = document.createElement(VENDOR_TAG);
            vendor.setTextContent(info.getVendor());
            Element model = document.createElement(MODEL_TAG);
            model.setTextContent(info.getModel());
            Element mhz = document.createElement(MHZ_TAG);
            mhz.setTextContent(String.valueOf(info.getMhz()));
            Element totalCpus = document.createElement(TOTAL_CPUS_TAG);
            totalCpus.setTextContent(String.valueOf(info.getTotalCores()));
            coreInfo.appendChild(vendor);
            coreInfo.appendChild(model);
            coreInfo.appendChild(mhz);
            coreInfo.appendChild(totalCpus);
            Element core = document.createElement(CORE_TAG);
            Element id = document.createElement(ID_TAG);
            id.setTextContent(String.valueOf(i));
            core.appendChild(id);
            appendTimeTag(document, cpui, core);
            coreInfo.appendChild(core);
            coreInfos.appendChild(coreInfo);
        }
        appendTimeTag(document, cpu, coreInfos);
        cpuInfo.appendChild(coreInfos);
        parent.appendChild(cpuInfo);
        return document;
    }

    public Map<String, Number> getCpuTime() throws ExecutionException {
        CpuPerc cpu = null;
        try {
            cpu = (new Shell()).getSigar().getCpuPerc();
            Map<String, Number> cpuUsage = new HashMap<>();
            /*
           Cpu time as Double Objects (percentage)
            */
            cpuUsage.put(CPU_USER_TAG, getPercentRepresentation(cpu.getUser()));
            cpuUsage.put(CPU_SYS_TAG, getPercentRepresentation(cpu.getSys()));
            cpuUsage.put(CPU_IDLE_TAG, getPercentRepresentation(cpu.getIdle()));
            cpuUsage.put(CPU_COMBINED_TAG, getPercentRepresentation(cpu.getCombined()));
            return cpuUsage;
        }
        catch (SigarException e) {
            throw new ExecutionException("Unable to fetch CPU Core Total Time: " + e.getMessage(), e);
        }
    }

    public Map<String, Number> getJvmCpuTime() throws ExecutionException {
        int pid = jvmSniffer.getPid();
        return getCpuTimeByPid(pid);
    }

    public Map<String, Number> getCpuTimeByPid(int pid) throws ExecutionException {
        Map<String, Number> cpuUsage = new HashMap<>();
        ProcCpu procCpu = null;
        try {
            procCpu = (new Shell()).getSigar().getProcCpu(pid);
        }
        catch (SigarException e) {
            throw new ExecutionException("Unable to fetch CPU PID Time for pid '" + pid + "': " + e.getMessage(), e);
        }
        cpuUsage.put(CPU_PID_PERCENTAGE, procCpu.getPercent());
        cpuUsage.put(CPU_PID_START_TIME, procCpu.getStartTime());
        cpuUsage.put(CPU_PID_LAST_ACCESSED_TIME, System.currentTimeMillis() - procCpu.getStartTime());
        cpuUsage.put(CPU_PID_USER, procCpu.getUser());
        cpuUsage.put(CPU_PID_SYS, procCpu.getSys());
        cpuUsage.put(CPU_PID_TOTAL, procCpu.getTotal());
        return cpuUsage;
    }

    public int getNumberOfCPU() {
        return jvmSniffer.getNumberOfCpus();
    }

    public int getSigarNumberOfCPU() {
        Sigar sigar = (new Shell()).getSigar();
        org.hyperic.sigar.CpuInfo[] cpuInfoArray = new org.hyperic.sigar.CpuInfo[0];
        try {
            cpuInfoArray = sigar.getCpuInfoList();
            if (cpuInfoArray.length != 0) {
                org.hyperic.sigar.CpuInfo info = cpuInfoArray[0];
                return info.getTotalCores();
            }
        }
        catch (SigarException e) {
            logger.warn("Unable to fetch CPU Info: " + e.getMessage(), e);
        }
        return 0;
    }

    private void appendTimeTag(Document document, CpuPerc cpui, Element parent) {
        Element time = document.createElement(TIME_TAG);
        Comment comment2 = document.createComment(USAGE_COMMENT);
        time.appendChild(comment2);

        Element user = document.createElement(USER_TAG);
        user.setTextContent(getFormattedTime(cpui.getUser()));
        time.appendChild(user);

        Element sys = document.createElement(SYS_TAG);
        sys.setTextContent(getFormattedTime(cpui.getSys()));
        time.appendChild(sys);

        Element idle = document.createElement(IDLE_TAG);
        idle.setTextContent(getFormattedTime(cpui.getIdle()));
        time.appendChild(idle);

        Element combined = document.createElement(COMBINED_TAG);
        combined.setTextContent(getFormattedTime(cpui.getCombined()));
        time.appendChild(combined);

        parent.appendChild(time);
    }

    protected String getFormattedTime(double val) {
        return numberFormat.format(val * 100);
    }

    private double getPercentRepresentation(double val) {
        return val * 100;
    }
}
