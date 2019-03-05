package eu.captech.digitalization.commons.basic.utils.io.time;

import eu.captech.digitalization.commons.basic.doc.Preamble;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

@Preamble(
        lastModifiedBy = "Eduardo Melgar",
        creationDate = "01.07.11",
        creationTime = "16:52",
        lastModified = "01.07.11"
)

/**
 * A class representing a moment in time. Extends Day which represents
 * the day of the moment, and defines the time within the day to
 * millisecond accuracy.
 *
 */
public class Time extends Day {
    /**
     * Instantiate a Time object.
     * The time is lenient meaning that illegal day parameters can be
     * specified and results in a recomputed day with legal month/day
     * values.
     *
     * @param year       Year of this time
     * @param month      Month of this time
     * @param dayOfMonth Day of month of this time.
     * @param hours      Hours of this time [0-23]
     * @param minutes    Minutes of this time [0-23]
     * @param seconds    Seconds of this time [0-23]
     */
    public Time(int year, int month, int dayOfMonth,
                int hours, int minutes, int seconds) {
        super(year, month, dayOfMonth);
        setHours(hours);
        setMinutes(minutes);
        setSeconds(seconds);
    }


    public Time(Day day, int hours, int minutes, int seconds) {
        this(day.getYear(), day.getMonth(), day.getDayOfMonth(),
                hours, minutes, seconds);
    }


    public Time(int hours, int minutes, int seconds) {
        this(new Day(), hours, minutes, seconds);
    }


    public Time() {
        calendar_ = new GregorianCalendar(); // Now
    }

    public Time(long time) {
        this();
        Date date = new Date(time);
        // Create a calendar based on given date
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        setYear(calendar.get(Calendar.YEAR));
        setMonth(calendar.get(Calendar.MONTH));
        setDayOfMonth(calendar.get(Calendar.DAY_OF_MONTH));
        setHours(calendar.get(Calendar.HOUR_OF_DAY));
        setMinutes(calendar.get(Calendar.MINUTE));
        setSeconds(calendar.get(Calendar.SECOND));
    }

    public Time(Date date) {
        this();
        // Create a calendar based on given date
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        setYear(calendar.get(Calendar.YEAR));
        setMonth(calendar.get(Calendar.MONTH));
        setDayOfMonth(calendar.get(Calendar.DAY_OF_MONTH));
        setHours(calendar.get(Calendar.HOUR_OF_DAY));
        setMinutes(calendar.get(Calendar.MINUTE));
        setSeconds(calendar.get(Calendar.SECOND));
    }


    public void setDay(Day day) {
        setYear(day.getYear());
        setMonth(day.getMonth());
        setDayOfMonth(day.getDayOfMonth());
    }

    public void setDayOfMonth(int dayOfMonth) {
        calendar_.set(Calendar.DAY_OF_MONTH, dayOfMonth);
    }

    public void setMonth(int month) {
        calendar_.set(Calendar.MONTH, month);
    }

    public void setYear(int year) {
        calendar_.set(Calendar.YEAR, year);
    }

    public void setHours(int hours) {
        calendar_.set(Calendar.HOUR_OF_DAY, hours);
    }


    public int getHours() {
        return calendar_.get(Calendar.HOUR_OF_DAY);
    }


    public void setMinutes(int minutes) {
        calendar_.set(Calendar.MINUTE, minutes);
    }


    public int getMinutes() {
        return calendar_.get(Calendar.MINUTE);
    }


    public void setSeconds(int seconds) {
        calendar_.set(Calendar.SECOND, seconds);
    }


    public int getSeconds() {
        return calendar_.get(Calendar.SECOND);
    }


    public void setMilliSeconds(int milliSeconds) {
        calendar_.set(Calendar.MILLISECOND, milliSeconds);
    }


    public int getMilliSeconds() {
        return calendar_.get(Calendar.MILLISECOND);
    }


    public boolean isAfter(Time time) {
        return calendar_.after(time.calendar_);
    }


    public boolean isBefore(Time time) {
        return calendar_.before(time.calendar_);
    }


    public boolean equals(Time time) {
        return calendar_.equals(time.calendar_);
    }

    public void addHours(int nHours) {
        calendar_.add(Calendar.HOUR_OF_DAY, nHours);
    }


    public void addMinutes(int nMinutes) {
        calendar_.add(Calendar.MINUTE, nMinutes);
    }

    public void addSeconds(int nSeconds) {
        calendar_.add(Calendar.SECOND, nSeconds);
    }


    public void addMilliSeconds(int nMilliSeconds) {
        calendar_.add(Calendar.MILLISECOND, nMilliSeconds);
    }


    public long milliSecondsBetween(Time time) {
        return calendar_.getTime().getTime() -
                time.calendar_.getTime().getTime();
    }


    public double secondsBetween(Time time) {
        long millisBetween = calendar_.getTime().getTime() -
                time.calendar_.getTime().getTime();
        return millisBetween / 1000;
    }


    public double minutesBetween(Time time) {
        long millisBetween = calendar_.getTime().getTime() -
                time.calendar_.getTime().getTime();
        return millisBetween / (1000 * 60);
    }


    public double hoursBetween(Time time) {
        long millisBetween = calendar_.getTime().getTime() -
                time.calendar_.getTime().getTime();
        return millisBetween / (1000 * 60 * 60);
    }


    public String toString() {
        StringBuilder string = new StringBuilder();
        string.append(super.toString());
        string.append(' ');
        if (getHours() < 10) {
            string.append('0');
        }
        string.append(getHours());
        string.append(':');
        if (getMinutes() < 10) {
            string.append('0');
        }
        string.append(getMinutes());
        string.append(':');
        if (getSeconds() < 10) {
            string.append('0');
        }
        string.append(getSeconds());
        string.append(',');
        string.append(getMilliSeconds());

        return string.toString();
    }

    public static Time now() {
        return new Time();
    }


    /*
   Days number of week:
   Monday = 1
   ...
   Saturday = 6
   Sunday = 7
    */
    public static int getNumberOfDaysByIgnoringCustomDays(int days, Integer... ignoreDaysNumberOfWeek) {
        if (ignoreDaysNumberOfWeek.length == 0) {
            return 0;
        }
        Time today = Time.now();
        int daysPlus = 0;
        int dayOfWeek;
        for (int i = 0; i <= days; i++) {
            dayOfWeek = today.addDays(i).getDayNumberOfWeek();
            for (Integer ignoreDay : ignoreDaysNumberOfWeek) {
                if (dayOfWeek == ignoreDay) {
                    daysPlus += 1;
                    days++;
                }
            }
        }
        return daysPlus;
    }

    /*
   Days number of week:
   Monday = 1
   ...
   Saturday = 6
   Sunday = 7
    */
    public static int getNumberOfDaysByIgnoringCustomDays(int days, List<Integer> ignoreDaysNumberOfWeek) {
        if (ignoreDaysNumberOfWeek.isEmpty()) {
            return 0;
        }
        Time now = Time.now();
        int daysPlus = 0;
        int dayOfWeek;
        for (int i = 0; i <= days; i++) {
            dayOfWeek = now.addDays(i).getDayNumberOfWeek();
            for (Integer ignoreDay : ignoreDaysNumberOfWeek) {
                if (dayOfWeek == ignoreDay) {
                    daysPlus += 1;
                    days++;
                }
            }
        }
        return daysPlus;
    }
}
