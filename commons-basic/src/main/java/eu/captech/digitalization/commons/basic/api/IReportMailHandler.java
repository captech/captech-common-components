package eu.captech.digitalization.commons.basic.api;

import eu.captech.digitalization.commons.basic.doc.Preamble;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.List;

@Preamble (
        lastModifiedBy = "Eduardo Melgar",
        creationDate = "16/02/15",
        creationTime = "11:30",
        lastModified = "16/02/15"
)
public interface IReportMailHandler{
    @SuppressWarnings("unchecked")
    void sendEmail(@NotNull String from, @NotNull String to, @NotNull String subject, @NotNull String body, List<Path> pathsToAttach);

    boolean isSendEmails();

    void setSendEmails(boolean sendEmails);
}
