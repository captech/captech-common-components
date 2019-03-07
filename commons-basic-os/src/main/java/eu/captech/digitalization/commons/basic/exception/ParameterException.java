package eu.captech.digitalization.commons.basic.exception;

import eu.captech.digitalization.commons.basic.doc.Preamble;

@Preamble(
        lastModifiedBy = "Eduardo Melgar",
        creationDate = "24.10.11",
        creationTime = "00:40",
        lastModified = "24.10.11"
)
public class ParameterException extends Exception {
    public ParameterException() {
    }

    public ParameterException(String message) {
        super(message);
    }

    public ParameterException(String message, Throwable cause) {
        super(message, cause);
    }
}
