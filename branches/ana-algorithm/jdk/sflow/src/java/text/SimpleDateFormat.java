package java.text;
import checkers.inference.reim.quals.*;

import java.util.Date;
import java.util.Locale;

public class SimpleDateFormat extends DateFormat {
    static final long serialVersionUID = 4774881970558875024L;

    public SimpleDateFormat() {
        throw new RuntimeException("skeleton method");
    }

    public SimpleDateFormat(String pattern) {
        throw new RuntimeException("skeleton method");
    }

    public SimpleDateFormat(String pattern, Locale locale) {
        throw new RuntimeException("skeleton method");
    }

    public SimpleDateFormat(String pattern, DateFormatSymbols formatSymbols) {
        throw new RuntimeException("skeleton method");
    }

    public void set2DigitYearStart(@Readonly Date startDate) {
        throw new RuntimeException("skeleton method");
    }

    public Date get2DigitYearStart(@Readonly SimpleDateFormat this)  {
        throw new RuntimeException("skeleton method");
    }

    public StringBuffer format(Date date, StringBuffer toAppendTo, FieldPosition pos) {
        throw new RuntimeException("skeleton method");
    }

    public AttributedCharacterIterator formatToCharacterIterator(Object obj) {
        throw new RuntimeException("skeleton method");
    }

    public Date parse(String text, ParsePosition pos) {
        throw new RuntimeException("skeleton method");
    }

    public String toPattern(@Readonly SimpleDateFormat this)  {
        throw new RuntimeException("skeleton method");
    }

    public String toLocalizedPattern(@Readonly SimpleDateFormat this)  {
        throw new RuntimeException("skeleton method");
    }

    public void applyPattern (String pattern) {
        throw new RuntimeException("skeleton method");
    }

    public void applyLocalizedPattern(String pattern) {
        throw new RuntimeException("skeleton method");
    }

    public DateFormatSymbols getDateFormatSymbols(@Readonly SimpleDateFormat this)  {
        throw new RuntimeException("skeleton method");
    }

    public void setDateFormatSymbols(@Readonly DateFormatSymbols newFormatSymbols) {
        throw new RuntimeException("skeleton method");
    }

    public Object clone(@Readonly SimpleDateFormat this)  {
        throw new RuntimeException("skeleton method");
    }

    public int hashCode(@Readonly SimpleDateFormat this)  {
        throw new RuntimeException("skeleton method");
    }

    public boolean equals(@Readonly SimpleDateFormat this, @Readonly Object obj)  {
        throw new RuntimeException("skeleton method");
    }
}
