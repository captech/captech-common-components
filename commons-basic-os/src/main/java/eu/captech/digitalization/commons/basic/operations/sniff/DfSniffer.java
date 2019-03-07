package eu.captech.digitalization.commons.basic.operations.sniff;

import eu.captech.digitalization.commons.basic.doc.Preamble;
import eu.captech.digitalization.commons.basic.exceptions.OperationRuntimeException;
import eu.captech.digitalization.commons.basic.exceptions.ParameterException;
import org.hyperic.sigar.FileSystem;
import org.hyperic.sigar.FileSystemUsage;
import org.hyperic.sigar.NfsFileSystem;
import org.hyperic.sigar.SigarException;
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
        creationDate = "11/30/12",
        creationTime = "10:25 AM",
        lastModified = "11/30/12"
)
public enum DfSniffer {
    dfSniffer;
    private static final Logger logger = LoggerFactory.getLogger("dfSniffer");
    public static final String DF_DIR_NAME = "dfDirName";
    public static final String DF_DEV_NAME = "dfDevName";
    public static final String DF_PERCENTAGE = "dfPercentage";
    public static final String DF_TOTAL = "dfTotal";
    public static final String DF_AVAILABLE = "dfAvailable";
    public static final String DF_USED = "dfUsed";
    private static final String ERROR_MESSAGE = "error";
    private static final String DF_INFO_TAG = "dfInfo";
    private static final String FILE_SYSTEMS_TAG = "fileSystems";
    private static final String FILE_SYSTEM_TAG = "fileSystem";
    private static final String ERROR_TAG = "error";
    private static final String INFO_TAG = "info";
    private static final String SYSTEM_ERROR_TAG = "systemError";
    private static final String STATUS_TAG = "status";
    private static final int SYSTEM_ERROR_STATUS = 99;
    private static final int PARTIAL_ERROR_STATUS = 50;
    private static final int OK_STATUS = 0;
    private static final String SIZE_INFORMATION_IS_REPRESENTED_IN_MB = "Size information is represented in KB";

    private void appendFileSystemInfo(Document document, Element info, Map<String, String> fileSystemInformation, String key)
            throws ParameterException {
        String fsiInfo;
        Element osInfo;
        if (fileSystemInformation.containsKey(key)) {
            fsiInfo = fileSystemInformation.get(key);
            osInfo = document.createElement(key);
            osInfo.setTextContent(fsiInfo);
            info.appendChild(osInfo);
        }
        else {
            throw new ParameterException("Tag with name " + key + " not found.");
        }
    }

    public long getDeviceDiskUsage(String dirName, String sizeType) {
        if (!sizeType.equals(DF_AVAILABLE) && !sizeType.equals(DF_PERCENTAGE) && !sizeType.equals(DF_TOTAL) && !sizeType.equals(DF_USED)) {
            throw new UnsupportedOperationException("Operation for sizeType '" + sizeType + "' is not supported.");
        }
        Map<String, Map<String, String>> fileSystems = getDeviceDiskUsage(new Shell(), dirName, false);
        if (fileSystems.containsKey(dirName)) {
            Map<String, String> fileSystemInformation = fileSystems.get(dirName);
            if (fileSystemInformation.containsKey(sizeType)) {
                return Long.valueOf(fileSystemInformation.get(sizeType));
            }
        }
        return -1;
    }


    private Map<String, Map<String, String>> getDeviceDiskUsage(Shell shell, String dirName, boolean opt_i) {
        FileSystem[] fileSystemList;
        try {
            fileSystemList = shell.getSigarProxy().getFileSystemList();
        }
        catch (SigarException e) {
            throw new OperationRuntimeException("Unable to fetch File System List. Probable reason: " + e.getMessage(), e);
        }
        Map<String, Map<String, String>> dfInformation = new HashMap<>();
        Map<String, String> fileSystemInformation;
        for (FileSystem fileSystem : fileSystemList) {
            if (fileSystem.getDirName().equalsIgnoreCase(dirName)) {
                fileSystemInformation = new HashMap<>();
                long used, avail, total, pct;
                FileSystemUsage usage;
                fileSystemInformation.put(DF_DIR_NAME, dirName);
                fileSystemInformation.put(DF_DEV_NAME, fileSystem.getDevName());
                if (fileSystem instanceof NfsFileSystem) {
                    NfsFileSystem nfs = (NfsFileSystem) fileSystem;
                    if (!nfs.ping()) {
                        fileSystemInformation.put(ERROR_MESSAGE, nfs.getUnreachableMessage() + "\t" + fileSystemInformation);
                    }
                }
                try {
                    usage = shell.getSigar().getFileSystemUsage(dirName);
                    if (opt_i) {
                        used = usage.getFiles() - usage.getFreeFiles();
                        avail = usage.getFreeFiles();
                        total = usage.getFiles();
                        if (total == 0) {
                            pct = 0;
                        }
                        else {
                            long u100 = used * 100;
                            pct = (u100 / total) + ((u100 % total != 0) ? 1 : 0);
                        }
                    }
                    else {
                        avail = usage.getAvail();
                        used = usage.getTotal() - avail;
                        total = usage.getTotal();
                        pct = (long) (usage.getUsePercent() * 100);
                    }
                    fileSystemInformation.put(DF_USED, String.valueOf(used));
                    fileSystemInformation.put(DF_AVAILABLE, String.valueOf(avail));
                    fileSystemInformation.put(DF_TOTAL, String.valueOf(total));
                    fileSystemInformation.put(DF_PERCENTAGE, String.valueOf(pct));
                    if (pct > 0) {
                        if (!opt_i || usage.getFiles() > 0) {
                            dfInformation.put(dirName, fileSystemInformation);
                        }
                    }
                }
                catch (SigarException e) {
                    String message = "Unable to fetch File System Usage information for " + dirName + "\t" + fileSystemInformation +
                            "\tProbable reason: " + e.getMessage().trim();
                    logger.warn(message);
                    fileSystemInformation.put(ERROR_MESSAGE, message);
                    dfInformation.put(dirName, fileSystemInformation);
                }
                break;
            }
        }
        return dfInformation;
    }

    public Map<String, Map<String, String>> getFileSystems() {
        return searchFileSystem(new Shell(), false);
    }

    public Map<String, Map<String, String>> searchFileSystem(Shell shell, boolean opt_i) {
        FileSystem[] fileSystemList;
        try {
            fileSystemList = shell.getSigarProxy().getFileSystemList();
        }
        catch (SigarException e) {
            throw new OperationRuntimeException("Unable to fetch File System List. Probable reason: " + e.getMessage(), e);
        }
        Map<String, Map<String, String>> dfInformation = new HashMap<>();
        Map<String, String> fileSystemInformation;
        int counter = 1;
        for (FileSystem fileSystem : fileSystemList) {
            fileSystemInformation = new HashMap<>();
            long used, avail, total, pct;
            FileSystemUsage usage;
            String dirName = fileSystem.getDirName();
            fileSystemInformation.put(DF_DIR_NAME, dirName);
            fileSystemInformation.put(DF_DEV_NAME, fileSystem.getDevName());
            if (fileSystem instanceof NfsFileSystem) {
                NfsFileSystem nfs = (NfsFileSystem) fileSystem;
                if (!nfs.ping()) {
                    fileSystemInformation.put(ERROR_MESSAGE, nfs.getUnreachableMessage() + "\t" + fileSystemInformation);
                }
            }
            try {
                usage = shell.getSigar().getFileSystemUsage(dirName);
                if (opt_i) {
                    used = usage.getFiles() - usage.getFreeFiles();
                    avail = usage.getFreeFiles();
                    total = usage.getFiles();
                    if (total == 0) {
                        pct = 0;
                    }
                    else {
                        long u100 = used * 100;
                        pct = (u100 / total) + ((u100 % total != 0) ? 1 : 0);
                    }
                }
                else {
                    avail = usage.getAvail();
                    used = usage.getTotal() - avail;
                    total = usage.getTotal();
                    pct = (long) (usage.getUsePercent() * 100);
                }
                fileSystemInformation.put(DF_USED, String.valueOf(used));
                fileSystemInformation.put(DF_AVAILABLE, String.valueOf(avail));
                fileSystemInformation.put(DF_TOTAL, String.valueOf(total));
                fileSystemInformation.put(DF_PERCENTAGE, String.valueOf(pct));
                if (pct > 0) {
                    if (!opt_i || usage.getFiles() > 0) {
                        dfInformation.put(dirName, fileSystemInformation);
                    }
                }
            }
            catch (SigarException e) {
                String message = "Unable to fetch File System Usage information for " + dirName + "\t" + fileSystemInformation +
                        "\tProbable reason: " + e.getMessage().trim();
                logger.warn(message);
                fileSystemInformation.put(ERROR_MESSAGE, message);
                dfInformation.put(dirName, fileSystemInformation);
            }
        }
        return dfInformation;
    }

    public Document appendDf(Document document, Element parent, Shell shell) {
        Element fileSystem;
        Element info;
        Element error;

        Comment comment = document.createComment("DF based Information");
        parent.appendChild(comment);
        Element dfInfo = document.createElement(DF_INFO_TAG);
        parent.appendChild(dfInfo);
        Element status = document.createElement(STATUS_TAG);
        status.setTextContent(String.valueOf(OK_STATUS));
        dfInfo.appendChild(status);
        Element fileSystems = document.createElement(FILE_SYSTEMS_TAG);
        dfInfo.appendChild(fileSystems);
        try {
            Map<String, String> fileSystemInformation;
            Map<String, Map<String, String>> osInformation = searchFileSystem(shell, false);
            for (String key : osInformation.keySet()) {
                fileSystemInformation = osInformation.get(key);
                if (logger.isInfoEnabled()) {
                    logger.info("fileSystemInformation: " + fileSystemInformation);
                }
                fileSystem = document.createElement(FILE_SYSTEM_TAG);
                if (fileSystemInformation.containsKey(ERROR_MESSAGE)) {
                    error = document.createElement(ERROR_TAG);
                    error.setTextContent(fileSystemInformation.get(ERROR_MESSAGE).trim());
                    if (fileSystemInformation.containsKey(DF_DIR_NAME) || fileSystemInformation.containsKey(DF_DEV_NAME)) {
                        info = document.createElement(INFO_TAG);
                        boolean empty = true;
                        if (fileSystemInformation.containsKey(DF_DIR_NAME)) {
                            try {
                                appendFileSystemInfo(document, info, fileSystemInformation, DF_DIR_NAME);
                                empty = false;
                            }
                            catch (ParameterException ignore) {
                            }
                        }
                        if (fileSystemInformation.containsKey(DF_DEV_NAME)) {
                            try {
                                appendFileSystemInfo(document, info, fileSystemInformation, DF_DEV_NAME);
                                empty = false;
                            }
                            catch (ParameterException ignore) {
                            }
                        }
                        if (!empty) {
                            fileSystem.appendChild(info);
                        }
                    }
                    fileSystem.appendChild(error);
                    fileSystems.appendChild(fileSystem);
                }
                else {
                    info = document.createElement(INFO_TAG);
                    try {
                        Comment comment2 = document.createComment(SIZE_INFORMATION_IS_REPRESENTED_IN_MB);
                        info.appendChild(comment2);
                        appendFileSystemInfo(document, info, fileSystemInformation, DF_DIR_NAME);
                        appendFileSystemInfo(document, info, fileSystemInformation, DF_DEV_NAME);
                        appendFileSystemInfo(document, info, fileSystemInformation, DF_USED);
                        appendFileSystemInfo(document, info, fileSystemInformation, DF_AVAILABLE);
                        appendFileSystemInfo(document, info, fileSystemInformation, DF_TOTAL);
                        appendFileSystemInfo(document, info, fileSystemInformation, DF_PERCENTAGE);
                        fileSystem.appendChild(info);
                    }
                    catch (ParameterException e) {
                        error = document.createElement(ERROR_TAG);
                        error.setTextContent("Error fetching mandatory info: " + e.getMessage());
                        fileSystem.appendChild(error);
                        status.setTextContent(String.valueOf(PARTIAL_ERROR_STATUS));
                    }
                    fileSystems.appendChild(fileSystem);
                }
            }
        }
        catch (OperationRuntimeException e) {
            Element systemError = document.createElement(SYSTEM_ERROR_TAG);
            systemError.setTextContent(e.getMessage());
            fileSystems.appendChild(systemError);
            status.setTextContent(String.valueOf(SYSTEM_ERROR_STATUS));
        }
        return document;
    }


}
