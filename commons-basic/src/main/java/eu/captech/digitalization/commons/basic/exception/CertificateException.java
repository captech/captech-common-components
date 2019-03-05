package eu.captech.digitalization.commons.basic.exception;

import eu.captech.digitalization.commons.basic.doc.Preamble;

@Preamble(
        lastModifiedBy = "Eduardo Melgar",
        creationDate = "8/8/14",
        creationTime = "3:43 PM",
        lastModified = "8/8/14"
)
public class CertificateException extends Exception {
    public CertificateException(String message) {
        super(message);
    }

    public CertificateException(String message, Throwable cause) {
        super(message, cause);
    }
}
