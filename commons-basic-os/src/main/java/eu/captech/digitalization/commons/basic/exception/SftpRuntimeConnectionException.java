package eu.captech.digitalization.commons.basic.exception;

import eu.captech.digitalization.commons.basic.doc.Preamble;

@Preamble(
        lastModifiedBy = "Eduardo Melgar",
        creationDate = "4/10/13",
        creationTime = "10:10 AM",
        lastModified = "4/10/13"
)
public class SftpRuntimeConnectionException extends RuntimeException {

    public SftpRuntimeConnectionException() {
        super();
    }

    public SftpRuntimeConnectionException(String message) {
        super(message);
    }

    public SftpRuntimeConnectionException(String message, Throwable cause) {
        super(message, cause);
    }
}
