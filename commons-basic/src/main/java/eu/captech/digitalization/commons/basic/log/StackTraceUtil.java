package eu.captech.digitalization.commons.basic.log;

import eu.captech.digitalization.commons.basic.doc.Preamble;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

@Preamble(
        lastModifiedBy = "Eduardo Melgar",
        creationDate = "24.10.11",
        creationTime = "12:03",
        lastModified = "24.10.11"
)
public enum StackTraceUtil {
    stackTraceUtil;
    private static final String TRUNCATE_MESSAGE = " ...(truncated)";

    public String getStackTrace(Throwable aThrowable) {
        final Writer result = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(result);
        aThrowable.printStackTrace(printWriter);
        return result.toString();
    }

    public String getStackTrace(Throwable aThrowable, int maxSize) {
        String err = getStackTrace(aThrowable);
        if (err.length() > maxSize) {
            err = err.substring(0, maxSize - 16) + TRUNCATE_MESSAGE;
        }
        return err;
    }

    /**
     * Defines a custom format for the stack trace as String.
     *
     * @param aThrowable A Throwable Class
     * @return a String with the trace
     */
    public String getCustomStackTrace(Throwable aThrowable) {
        //add the class name and any message passed to constructor
        final StringBuilder result = new StringBuilder("");
        result.append(aThrowable.toString());
        final String NEW_LINE = System.getProperty("line.separator");
        result.append(NEW_LINE);

        //add each element of the stack trace
        for (StackTraceElement element : aThrowable.getStackTrace()) {
            result.append(element);
            result.append(NEW_LINE);
        }
        return result.toString();
    }
}
