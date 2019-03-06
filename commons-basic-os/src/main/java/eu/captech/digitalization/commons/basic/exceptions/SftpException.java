package eu.captech.digitalization.commons.basic.exceptions;

import eu.captech.digitalization.commons.basic.doc.Preamble;

@Preamble(
        lastModifiedBy = "Eduardo Melgar",
        creationDate = "10/22/12",
        creationTime = "12:03 PM",
        lastModified = "10/22/12"
)
public class SftpException extends AbstractOperationsException {

    public SftpException() {
        super();
    }

    public SftpException(String message) {
        super(message);
    }

    public SftpException(String message, Throwable cause) {
        super(message, cause);
    }
}
