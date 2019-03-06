package eu.captech.digitalization.commons.basic.file;

import eu.captech.digitalization.commons.basic.api.IReportMailHandler;
import eu.captech.digitalization.commons.basic.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MoveFile implements Consumer<Path> {
    private static final Logger logger = LoggerFactory.getLogger(MoveFile.class);

    private final Path workingFolder;
    private final IReportMailHandler reportMailHandler;

    private String from = "data-capture-filewatcher@nets.eu";
    private String to = "vaiba@nets.eu";
    private String subject = "Error moving file";

    public MoveFile(Path workingFolder, IReportMailHandler reportMailHandler) {
        this.workingFolder = workingFolder;
        this.reportMailHandler = reportMailHandler;
    }

    public void setFrom(String from){
        this.from = from;
    }

    public void setTo(String to){
        this.to = to;
    }

    public void setSubject(String subject){
        this.subject = subject;
    }

    @Override
    public void accept(Path child) {
        logger.debug("Moving file from " + child.toAbsolutePath() + " to " + workingFolder.toAbsolutePath());
        try {
            Files.move(child, Paths.get(workingFolder.toString(), child.getFileName().toString()));
            logger.info("File moved from " + child.toAbsolutePath() + " to " + workingFolder.toAbsolutePath());
        }
        catch(IOException e) {
            String message = "Unable to move file from " + child.toAbsolutePath() + " to " + workingFolder.toAbsolutePath();
            logger.warn(message);
            if (reportMailHandler != null) {
                reportMailHandler.sendEmail(from, to, subject, message, null);
            }
        }
    }
}
