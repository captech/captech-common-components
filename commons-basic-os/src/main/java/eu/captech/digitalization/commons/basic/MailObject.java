package eu.captech.digitalization.commons.basic;

import eu.captech.digitalization.commons.basic.doc.Preamble;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static eu.captech.digitalization.commons.basic.log.StackTraceUtil.stackTraceUtil;


@Preamble (
        lastModifiedBy = "Eduardo Melgar",
        creationDate = "25/09/14",
        creationTime = "20:33",
        lastModified = "25/09/14"
)
public class MailObject{
    private static final Logger logger = LoggerFactory.getLogger(MailObject.class);
    public static final Charset UTF_8_CHARSET = Charset.forName("UTF-8");
    public static final String JAVA_IO_TMPDIR = "java.io.tmpdir";
    public static final String THROWABLE_TXT = "_throwable.txt";
    private final String from;
    private final String to;
    private final String subject;
    private final String body;
    private List<Path> attachments;
    private Throwable throwable;

    public MailObject(@NotNull String from, @NotNull String to, @NotNull String subject, @NotNull String body,
                      @Nullable List<Path> attachments){
        this.from = from;
        this.to = to;
        this.subject = subject;
        this.body = body;
        this.attachments = attachments;
    }

    public MailObject(@NotNull String from, @NotNull String to, @NotNull String subject, @NotNull String body,
                      @Nullable List<Path> attachments, @Nullable Throwable throwable){
        this.from = from;
        this.to = to;
        this.subject = subject;
        this.body = body;
        this.attachments = attachments;
        this.throwable = throwable;
    }

    public String getFrom(){
        return from;
    }

    public String getTo(){
        return to;
    }

    public String getSubject(){
        return subject;
    }

    public String getBody(){
        return body;
    }

    public List<Path> getAttachments(){
        return attachments;
    }

    public void setAttachments(List<Path> attachments){
        this.attachments = attachments;
    }

    public void setThrowable(Throwable throwable){
        Path tmp = Paths.get(System.getProperty(JAVA_IO_TMPDIR));
        Path tt = Paths.get(tmp.toString(), System.currentTimeMillis() + THROWABLE_TXT);
        try {
            Files.write(tt, (stackTraceUtil.getCustomStackTrace(throwable)).getBytes(UTF_8_CHARSET));
            if (attachments == null) {
                attachments = new ArrayList<>(1);
                attachments.add(tt);
            }
        }
        catch(IOException e) {
            logger.warn("Unable to attach Throwable error: " + e.getMessage(), e);
            logger.warn("Throwable error was: " + throwable.getMessage(), throwable);
        }
    }
}
