package eu.captech.digitalization.commons.basic.operations.sniff;

import eu.captech.digitalization.commons.basic.doc.Preamble;
import eu.captech.digitalization.commons.basic.exception.ExecutionException;
import org.hyperic.sigar.Mem;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import org.hyperic.sigar.Swap;
import org.hyperic.sigar.cmd.Shell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.HashMap;
import java.util.Map;

@Preamble(
        lastModifiedBy = "Eduardo Melgar",
        creationDate = "1/22/13",
        creationTime = "4:55 PM",
        lastModified = "1/22/13"
)
public enum MemSniffer {
    memSniffer;
    private static final Logger logger = LoggerFactory.getLogger(MemSniffer.class);
    private static final String STATUS_TAG = "status";
    private static final int OK_STATUS = 0;
    private static final int SYSTEM_ERROR_STATUS = 99;
    private static final String MEMORY__INFO_TAG = "memoryInfo";
    private static final String MEMORY_BASED_INFORMATION_COMMENT = "Memory based Information";
    private static final String SYSTEM_ERROR_TAG = "systemError";
    private static final String MEMORY_TAG = "memory";
    private static final String TYPE_TAG = "type";
    public static final String MEM_USED_TAG = "memUsed";
    public static final String MEM_TOTAL_TAG = "memTotal";
    public static final String MEM_FREE_TAG = "memFree";
    public static final String MEM_MAX_TAG = "memMax";
    public static final String MEM_FREE_PERCENTAGE_TAG = "memFreePercentage";
    public static final String MEM_USED_PERCENTAGE_TAG = "memUsedPercentage";
    public static final String MEMORY_TYPE_STRING = "MemoryType";
    public static final String SWAP_MEMORY_TYPE = "swap" + MEMORY_TYPE_STRING;
    public static final String MEM_MEMORY_TYPE = "mem" + MEMORY_TYPE_STRING;
    public static final String JVM_MEMORY_TYPE = "jvm" + MEMORY_TYPE_STRING;
    public static final String ALL_MEMORY_TYPE = "all" + MEMORY_TYPE_STRING;
    private static final String MEMORIES_TAG = "memories";

    public Map<String, Number> getMemory(String memoryType) throws ExecutionException {
        if (!memoryType.equals(MEM_MEMORY_TYPE) && !memoryType.equals(SWAP_MEMORY_TYPE) && !memoryType.equals(JVM_MEMORY_TYPE)) {
            throw new UnsupportedOperationException("Operation for memory type '" + memoryType + "' is not supported.");
        }
        return memSniffer.getMemoryByType(memoryType);
    }

    private Map<String, Number> getMemoryByType(String memoryType) throws ExecutionException {
        switch (memoryType) {
            case MEM_MEMORY_TYPE:
                Mem mem = null;
                try {
                    mem = (new Shell()).getSigar().getMem();
                    Map<String, Number> memoryUsage = new HashMap<>();
                    /*
                   mem as Long Objects in KB
                    */
                    memoryUsage.put(MEM_USED_TAG, bytesToKB(mem.getUsed()));
                    memoryUsage.put(MEM_TOTAL_TAG, bytesToKB(mem.getTotal()));
                    memoryUsage.put(MEM_FREE_TAG, bytesToKB(mem.getFree()));
                    /*
                   Percentage as Double Objects
                    */
                    memoryUsage.put(MEM_FREE_PERCENTAGE_TAG, mem.getFreePercent());
                    memoryUsage.put(MEM_USED_PERCENTAGE_TAG, mem.getUsedPercent());
                    return memoryUsage;
                }
                catch (SigarException e) {
                    throw new ExecutionException("Unable to fetch MEM Info: " + e.getMessage(), e);
                }
            case SWAP_MEMORY_TYPE:
                Swap swap = null;
                try {
                    swap = (new Shell()).getSigar().getSwap();
                    Map<String, Number> memoryUsage = new HashMap<>();
                    /*
                   swap as Long Objects in KB
                    */
                    memoryUsage.put(MEM_USED_TAG, bytesToKB(swap.getUsed()));
                    memoryUsage.put(MEM_TOTAL_TAG, bytesToKB(swap.getTotal()));
                    memoryUsage.put(MEM_FREE_TAG, bytesToKB(swap.getFree()));
                    /*
                   Percentage as Double Objects
                    */
                    memoryUsage.put(MEM_FREE_PERCENTAGE_TAG, getPercentage(swap.getTotal(), swap.getFree()));
                    memoryUsage.put(MEM_USED_PERCENTAGE_TAG, getPercentage(swap.getTotal(), swap.getUsed()));
                    return memoryUsage;
                }
                catch (SigarException e) {
                    throw new ExecutionException("Unable to fetch SWAP Info: " + e.getMessage(), e);
                }
            case JVM_MEMORY_TYPE:
                return JvmSniffer.jvmSniffer.getJvmHeapMemoryUsage();
            default:
                return null;
        }
    }

    public Document appendMemInfo(Document document, Element parent, Shell shell) {
        String memInformationFetched = "";
        String swapInformationFetched = "";
        Sigar sigar = shell.getSigar();
        Mem mem = null;
        try {
            mem = sigar.getMem();
        }
        catch (SigarException e) {
            String message = "Unable to fetch MEM Info: " + e.getMessage();
            logger.warn(message, e);
            memInformationFetched = message;
        }
        Swap swap = null;
        try {
            swap = sigar.getSwap();
        }
        catch (SigarException e) {
            String message = "Unable to fetch SWAP Info: " + e.getMessage();
            logger.warn(message, e);
            swapInformationFetched = message;
        }
        Comment comment = document.createComment(MEMORY_BASED_INFORMATION_COMMENT);
        parent.appendChild(comment);
        Element memoryInfo = document.createElement(MEMORY__INFO_TAG);
        parent.appendChild(memoryInfo);
        Element status = document.createElement(STATUS_TAG);
        Element memories = document.createElement(MEMORIES_TAG);
        if (!memInformationFetched.isEmpty() || !swapInformationFetched.isEmpty()) {
            String errorMessage = ((memInformationFetched.isEmpty()) ? "" : memInformationFetched + ". ") +
                    ((swapInformationFetched.isEmpty()) ? "" : swapInformationFetched);
            status.setTextContent(String.valueOf(SYSTEM_ERROR_STATUS));
            memoryInfo.appendChild(status);
            Element systemError = document.createElement(SYSTEM_ERROR_TAG);
            systemError.setTextContent(errorMessage);
            memories.appendChild(systemError);
            memoryInfo.appendChild(memories);
            return document;
        }

        status.setTextContent(String.valueOf(OK_STATUS));
        memoryInfo.appendChild(status);

        addMemMemory(document, mem, memories);

        addSwapMemory(document, swap, memories);

        memoryInfo.appendChild(memories);

        return document;
    }

    private void addMemMemory(Document document, Mem mem, Element memories) {
        Element memory = document.createElement(MEMORY_TAG);
        memory.appendChild(document.createComment("In MB"));

        Element ram = document.createElement("ram");
        ram.setTextContent(String.valueOf(mem.getRam()));
        memory.appendChild(ram);

        Element memoryType = document.createElement(TYPE_TAG);
        Element memoryUsed = document.createElement(MEM_USED_TAG);
        Element memoryTotal = document.createElement(MEM_TOTAL_TAG);
        Element memoryFree = document.createElement(MEM_FREE_TAG);
        memoryType.setTextContent(MEM_MEMORY_TYPE);
        memoryUsed.setTextContent(String.valueOf(bytesToMB(mem.getUsed())));
        memoryTotal.setTextContent(String.valueOf(bytesToMB(mem.getTotal())));
        memoryFree.setTextContent(String.valueOf(bytesToMB(mem.getFree())));
        memory.appendChild(memoryType);
        memory.appendChild(memoryUsed);
        memory.appendChild(memoryTotal);
        memory.appendChild(memoryFree);
        memories.appendChild(memory);
    }

    private void addSwapMemory(Document document, Swap swap, Element memories) {
        Element swapElement = document.createElement(MEMORY_TAG);
        swapElement.appendChild(document.createComment("In MB"));
        Element swapType = document.createElement(TYPE_TAG);
        Element swapUsed = document.createElement(MEM_USED_TAG);
        Element swapTotal = document.createElement(MEM_TOTAL_TAG);
        Element swapFree = document.createElement(MEM_FREE_TAG);
        swapType.setTextContent(SWAP_MEMORY_TYPE);
        swapUsed.setTextContent(String.valueOf(bytesToMB(swap.getUsed())));
        swapTotal.setTextContent(String.valueOf(bytesToMB(swap.getTotal())));
        swapFree.setTextContent(String.valueOf(bytesToMB(swap.getFree())));
        swapElement.appendChild(swapType);
        swapElement.appendChild(swapUsed);
        swapElement.appendChild(swapTotal);
        swapElement.appendChild(swapFree);
        memories.appendChild(swapElement);
    }


    private double getPercentage(long total, long part) {
        return (part * 100) / total;
    }

    private static double bytesToKB(long bytes) {
        return bytes / 1024;
    }

    private static double bytesToMB(long bytes) {
        return bytes / 1048576;
    }

}
