package java.util;

import java.io.*;
import java.sql.*;
import java.util.*;

import checkers.inference.reim.quals.*;

public class Calendar implements Serializable, Cloneable, Comparable<Calendar> {
    protected Calendar() { throw new RuntimeException("skeleton method"); }
    protected Calendar(@Readonly TimeZone arg0, @Readonly Locale arg1) { throw new RuntimeException("skeleton method"); }
    public static Calendar getInstance() { throw new RuntimeException("skeleton method"); }
    public static Calendar getInstance(@Readonly TimeZone arg0) { throw new RuntimeException("skeleton method"); }
    public static Calendar getInstance(@Readonly Locale arg0) { throw new RuntimeException("skeleton method"); }
    public static Calendar getInstance(@Readonly TimeZone arg0, @Readonly Locale arg1) { throw new RuntimeException("skeleton method"); }
    public static Locale[] getAvailableLocales() { throw new RuntimeException("skeleton method"); }
    protected void computeTime() { throw new RuntimeException("skeleton method"); }
    protected void computeFields() { throw new RuntimeException("skeleton method"); }
    public Date getTime() { throw new RuntimeException("skeleton method"); }
    public void setTime(@Readonly Date arg0) { throw new RuntimeException("skeleton method"); }
    public long getTimeInMillis() { throw new RuntimeException("skeleton method"); }
    public void setTimeInMillis(long arg0) { throw new RuntimeException("skeleton method"); }
    public int get(int arg0) { throw new RuntimeException("skeleton method"); }
    protected int internalGet(int arg0) { throw new RuntimeException("skeleton method"); }
    public void set(int arg0, int arg1) { throw new RuntimeException("skeleton method"); }
    public void set(int arg0, int arg1, int arg2) { throw new RuntimeException("skeleton method"); }
    public void set(int arg0, int arg1, int arg2, int arg3, int arg4) { throw new RuntimeException("skeleton method"); }
    public void set(int arg0, int arg1, int arg2, int arg3, int arg4, int arg5) { throw new RuntimeException("skeleton method"); }
    public void clear() { throw new RuntimeException("skeleton method"); }
    public void clear(int arg0) { throw new RuntimeException("skeleton method"); }
    public boolean isSet(int arg0) { throw new RuntimeException("skeleton method"); }
    public String getDisplayName(int arg0, int arg1, Locale arg2) { throw new RuntimeException("skeleton method"); }
    public Map<String,Integer> getDisplayNames(int arg0, int arg1, Locale arg2) { throw new RuntimeException("skeleton method"); }
    protected void complete() { throw new RuntimeException("skeleton method"); }
    public boolean equals(@Readonly Object arg0) { throw new RuntimeException("skeleton method"); }
    public int hashCode() { throw new RuntimeException("skeleton method"); }
    public boolean before(Object arg0) { throw new RuntimeException("skeleton method"); }
    public boolean after(Object arg0) { throw new RuntimeException("skeleton method"); }
    public int compareTo(Calendar arg0) { throw new RuntimeException("skeleton method"); }
    public void add(int arg0, int arg1) { throw new RuntimeException("skeleton method"); }
    public void roll(int arg0, boolean arg1) { throw new RuntimeException("skeleton method"); }
    public void roll(int arg0, int arg1) { throw new RuntimeException("skeleton method"); }
    public void setTimeZone(@Readonly TimeZone arg0) { throw new RuntimeException("skeleton method"); }
    public TimeZone getTimeZone() { throw new RuntimeException("skeleton method"); }
    public void setLenient(boolean arg0) { throw new RuntimeException("skeleton method"); }
    public boolean isLenient() { throw new RuntimeException("skeleton method"); }
    public void setFirstDayOfWeek(int arg0) { throw new RuntimeException("skeleton method"); }
    public int getFirstDayOfWeek() { throw new RuntimeException("skeleton method"); }
    public void setMinimalDaysInFirstWeek(int arg0) { throw new RuntimeException("skeleton method"); }
    public int getMinimalDaysInFirstWeek() { throw new RuntimeException("skeleton method"); }
    public int getMinimum(int arg0) { throw new RuntimeException("skeleton method"); }
    public int getMaximum(int arg0) { throw new RuntimeException("skeleton method"); }
    public int getGreatestMinimum(int arg0) { throw new RuntimeException("skeleton method"); }
    public int getLeastMaximum(int arg0) { throw new RuntimeException("skeleton method"); }
    public int getActualMinimum(int arg0) { throw new RuntimeException("skeleton method"); }
    public int getActualMaximum(int arg0) { throw new RuntimeException("skeleton method"); }
    public Object clone() { throw new RuntimeException("skeleton method"); }
    public String toString() { throw new RuntimeException("skeleton method"); }
}
