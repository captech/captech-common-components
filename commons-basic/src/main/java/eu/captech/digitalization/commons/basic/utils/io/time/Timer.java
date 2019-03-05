package eu.captech.digitalization.commons.basic.utils.io.time;

import eu.captech.digitalization.commons.basic.doc.Preamble;

import java.util.Date;

@Preamble(
        lastModifiedBy = "Eduardo Melgar",
        creationDate = "01.07.11",
        creationTime = "16:58",
        lastModified = "01.07.11"
)
/**
 * Class for program event timing.
 * Usage:
 *
 *   <pre>
 *   Timer timer = new Timer();
 *
 *   // do stuff
 *
 *   System.out.println (timer);  // prints time elapsed since
 *                                // object was created.
 *   </pre>
 *
 */
public class Timer {
    private Date start_;


    /**
     * Start timer.
     */
    public Timer() {
        reset();
    }


    /**
     * Returns exact number of milliseconds since timer was started.
     *
     * @return Number of milliseconds since timer was started.
     */
    public long getTime() {
        Date now = new Date();
        return now.getTime() - start_.getTime();
    }


    /**
     * Restarts the timer.
     */
    public void reset() {
        start_ = new Date();  // now
    }


    /**
     * Returns a formatted string showing the elapsed time
     * since the instance was created.
     *
     * @return Formatted time string.
     */
    public String toString() {
        long nMillis = getTime();

        long nHours = nMillis / 1000 / 60 / 60;
        nMillis -= nHours * 1000 * 60 * 60;

        long nMinutes = nMillis / 1000 / 60;
        nMillis -= nMinutes * 1000 * 60;

        long nSeconds = nMillis / 1000;
        nMillis -= nSeconds * 1000;

        StringBuilder time = new StringBuilder();
        if (nHours > 0) {
            time.append(nHours).append(":");
        }
        if (nHours > 0 && nMinutes < 10) {
            time.append("0");
        }
        time.append(nMinutes).append(":");
        if (nSeconds < 10) {
            time.append("0");
        }
        time.append(nSeconds);
        time.append(".");
        if (nMillis < 100) {
            time.append("0");
        }
        if (nMillis < 10) {
            time.append("0");
        }
        time.append(nMillis);

        return time.toString();
    }


    /**
     * Testing this class.
     *
     * @param args Not used.
     */
    public static void main(String[] args) {
        Timer timer = new Timer();

        for (int i = 0; i < 100000000; i++) {
            double b = 998.43678;
            Math.sqrt(b);
        }

        System.out.println(timer);
    }
}
