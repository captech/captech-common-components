package eu.captech.digitalization.commons.basic.api;

import eu.captech.digitalization.commons.basic.doc.Preamble;

import java.text.SimpleDateFormat;

@Preamble(
        lastModifiedBy = "Eduardo Melgar",
        creationDate = "4/14/14",
        creationTime = "1:29 PM",
        lastModified = "4/14/14"
)
public interface IAuditable {
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    String getAuditInformation();
//    Long getAuditDate();
}
