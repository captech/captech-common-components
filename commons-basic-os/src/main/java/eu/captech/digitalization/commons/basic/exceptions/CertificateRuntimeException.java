package eu.captech.digitalization.commons.basic.exceptions;

import eu.captech.digitalization.commons.basic.doc.Preamble;

@Preamble(
        lastModifiedBy = "Eduardo Melgar",
        creationDate = "8/8/14",
        creationTime = "2:48 PM",
        lastModified = "8/8/14"
)
public class CertificateRuntimeException extends OperationRuntimeException {
    public CertificateRuntimeException(String message) {
        super(message);
    }

    public CertificateRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}
