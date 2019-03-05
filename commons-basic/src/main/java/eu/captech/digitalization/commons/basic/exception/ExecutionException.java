package eu.captech.digitalization.commons.basic.exception;

public class ExecutionException extends Exception {
    public ExecutionException() {
    }

    public ExecutionException(String message) {
        super(message);
    }

    public ExecutionException(String message, Throwable cause) {
        super(message, cause);
    }
}
