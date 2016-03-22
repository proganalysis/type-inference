package java.text;
import checkers.inference.reim.quals.*;

public abstract class DateFormat extends java.text.Format {
    protected java.util.Calendar calendar;
    protected java.text.NumberFormat numberFormat;
    public static final int ERA_FIELD = 0;
    public static final int YEAR_FIELD = 0;
    public static final int MONTH_FIELD = 0;
    public static final int DATE_FIELD = 0;
    public static final int HOUR_OF_DAY1_FIELD = 0;
    public static final int HOUR_OF_DAY0_FIELD = 0;
    public static final int MINUTE_FIELD = 0;
    public static final int SECOND_FIELD = 0;
    public static final int MILLISECOND_FIELD = 0;
    public static final int DAY_OF_WEEK_FIELD = 0;
    public static final int DAY_OF_YEAR_FIELD = 0;
    public static final int DAY_OF_WEEK_IN_MONTH_FIELD = 0;
    public static final int WEEK_OF_YEAR_FIELD = 0;
    public static final int WEEK_OF_MONTH_FIELD = 0;
    public static final int AM_PM_FIELD = 0;
    public static final int HOUR1_FIELD = 0;
    public static final int HOUR0_FIELD = 0;
    public static final int TIMEZONE_FIELD = 0;
    private static final long serialVersionUID = 0;
    public static final int FULL = 0;
    public static final int LONG = 0;
    public static final int MEDIUM = 0;
    public static final int SHORT = 0;
    public static final int DEFAULT = 0;
    public final java.lang.StringBuffer format(java.lang.Object arg0, java.lang.StringBuffer arg1, java.text.FieldPosition arg2) { throw new RuntimeException("skeleton method"); }
    public abstract java.lang.StringBuffer format(java.util.Date arg0, java.lang.StringBuffer arg1, java.text.FieldPosition arg2);
    public final java.lang.String format(@Readonly java.util.Date arg0) { throw new RuntimeException("skeleton method"); }
    public java.util.Date parse(java.lang.String arg0) throws java.text.ParseException { throw new RuntimeException("skeleton method"); }
    public abstract java.util.Date parse(java.lang.String arg0, java.text.ParsePosition arg1);
    public java.lang.Object parseObject(java.lang.String arg0, java.text.ParsePosition arg1) { throw new RuntimeException("skeleton method"); }
    public static final java.text.DateFormat getTimeInstance() { throw new RuntimeException("skeleton method"); }
    public static final java.text.DateFormat getTimeInstance(int arg0) { throw new RuntimeException("skeleton method"); }
    public static final java.text.DateFormat getTimeInstance(int arg0, java.util.Locale arg1) { throw new RuntimeException("skeleton method"); }
    public static final java.text.DateFormat getDateInstance() { throw new RuntimeException("skeleton method"); }
    public static final java.text.DateFormat getDateInstance(int arg0) { throw new RuntimeException("skeleton method"); }
    public static final java.text.DateFormat getDateInstance(int arg0, java.util.Locale arg1) { throw new RuntimeException("skeleton method"); }
    public static final java.text.DateFormat getDateTimeInstance() { throw new RuntimeException("skeleton method"); }
    public static final java.text.DateFormat getDateTimeInstance(int arg0, int arg1) { throw new RuntimeException("skeleton method"); }
    public static final java.text.DateFormat getDateTimeInstance(int arg0, int arg1, java.util.Locale arg2) { throw new RuntimeException("skeleton method"); }
    public static final java.text.DateFormat getInstance() { throw new RuntimeException("skeleton method"); }
    public static java.util.Locale[] getAvailableLocales() { throw new RuntimeException("skeleton method"); }
    public void setCalendar(java.util.Calendar arg0) { throw new RuntimeException("skeleton method"); }
    public java.util.Calendar getCalendar() { throw new RuntimeException("skeleton method"); }
    public void setNumberFormat(java.text.NumberFormat arg0) { throw new RuntimeException("skeleton method"); }
    public java.text.NumberFormat getNumberFormat() { throw new RuntimeException("skeleton method"); }
    public void setTimeZone(java.util.TimeZone arg0) { throw new RuntimeException("skeleton method"); }
    public java.util.TimeZone getTimeZone() { throw new RuntimeException("skeleton method"); }
    public void setLenient(boolean arg0) { throw new RuntimeException("skeleton method"); }
    public boolean isLenient() { throw new RuntimeException("skeleton method"); }
    public int hashCode() { throw new RuntimeException("skeleton method"); }
    public boolean equals(java.lang.Object arg0) { throw new RuntimeException("skeleton method"); }
    public java.lang.Object clone() { throw new RuntimeException("skeleton method"); }
    private static java.text.DateFormat get(int arg0, int arg1, int arg2, java.util.Locale arg3) { throw new RuntimeException("skeleton method"); }
    protected DateFormat() { throw new RuntimeException("skeleton method"); }
}
