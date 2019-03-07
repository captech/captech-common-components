package eu.captech.digitalization.commons.basic.operations.sniff;

import eu.captech.digitalization.commons.basic.doc.Preamble;
import eu.captech.digitalization.commons.basic.exception.ExecutionException;
import org.hyperic.sigar.DirUsage;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import org.hyperic.sigar.cmd.Shell;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;

@Preamble(
        lastModifiedBy = "Eduardo Melgar",
        creationDate = "1/29/13",
        creationTime = "3:46 PM",
        lastModified = "1/29/13"
)
public enum DuSniffer {
    duSniffer;
    private static final Logger logger = LoggerFactory.getLogger(DuSniffer.class);

    public long getDiskUsage(@NotNull Path dir) throws ExecutionException {
        if (dir == null) {
            logger.warn("Passed Path is null");
            return 0;
        }
        if (!Files.exists(dir)) {
            logger.warn("Path " + dir + " doesn't exists");
            return 0;
        }
        if (!Files.isDirectory(dir)) {
            logger.warn("Path " + dir + " is not a directory");
            return 0;
        }
        /*
        Executes "du -s -b" for given directory
         */
        DirUsage du = null;
        Sigar sigar = null;
        Shell shell = new Shell();
        try {
        	sigar = shell.getSigar(); 
            du = sigar.getDirUsage(dir.toAbsolutePath().toString());
        }
        catch (SigarException e) {
            throw new ExecutionException("Unable to get information for path " + dir + ": " + e.getMessage(), e);
        }
        finally {
        	if (sigar != null) {
        		sigar.close();
        	}
        	shell.shutdown();
        }
        return du.getDiskUsage();

    }


}
