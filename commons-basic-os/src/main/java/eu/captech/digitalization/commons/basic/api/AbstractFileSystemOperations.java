package eu.captech.digitalization.commons.basic.api;


import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractFileSystemOperations implements IFileSystemOperations {

    private static final Logger logger = LoggerFactory.getLogger(AbstractFileSystemOperations.class);
    protected String initialParameter = OPERATIONS_PROTOCOL_UNKNOWN;
    protected String protocol = OPERATIONS_PROTOCOL_UNKNOWN;

    protected AbstractFileSystemOperations(String initialParameter) {
        this.initialParameter = initialParameter;
    }

    protected void printOperation() {
        if (logger.isInfoEnabled()) {
            logger.info("Operation: " + this.getClass().getSimpleName() + "\tInitialParameter: " + initialParameter);
        }
    }

    @Override
    public String getInitialParameter() {
        return initialParameter;
    }

    @Override
    public String getProtocol() {
        return protocol;
    }

    @Override
    public int compareTo(@NotNull IOperations o) {
        return getInitialParameter().compareTo(o.getInitialParameter());
    }

}
