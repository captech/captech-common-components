package eu.captech.digitalization.commons.basic.exceptions;

import eu.captech.digitalization.commons.basic.doc.Preamble;

@Preamble(
        lastModifiedBy = "Eduardo Melgar",
        creationDate = "10/22/12",
        creationTime = "1:58 PM",
        lastModified = "10/22/12"
)
public class OperationRuntimeException extends RuntimeException {

    private Object referencedObject;

    public Object getReferencedObject() {
        return referencedObject;
    }

    public void setReferencedObject(Object referencedObject) {
        this.referencedObject = referencedObject;
    }

    /**
     * Constructs a new runtime exception with {@code null} as its
     * detail message.  The cause is not initialized, and may subsequently be
     * initialized by a call to {@link #initCause}.
     */
    public OperationRuntimeException() {
        super();
    }

    /**
     * Constructs a new runtime exception with the specified detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     *
     * @param message the detail message. The detail message is saved for
     *                later retrieval by the {@link #getMessage()} method.
     */
    public OperationRuntimeException(String message) {
        super(message);
    }

    /**
     * Constructs a new runtime exception with the specified detail message and
     * cause.  <p>Note that the detail message associated with
     * {@code cause} is <i>not</i> automatically incorporated in
     * this runtime exception's detail message.
     *
     * @param message the detail message (which is saved for later retrieval
     *                by the {@link #getMessage()} method).
     * @param cause   the cause (which is saved for later retrieval by the
     *                {@link #getCause()} method).  (A <tt>null</tt> value is
     *                permitted, and indicates that the cause is nonexistent or
     *                unknown.)
     * @since 1.4
     */
    public OperationRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public String toString() {
        if (getReferencedObject() != null) {
            return super.toString() + " : Context {" + referencedObject.toString() + '}';
        }
        return super.toString();
    }
}
