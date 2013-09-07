package java.text;
import checkers.inference.reim.quals.*;

import java.io.InvalidObjectException;
import java.util.Locale;

public class MessageFormat extends Format {
    private static final long serialVersionUID = 6479157306784022952L;

    public MessageFormat(String pattern) {
        throw new RuntimeException("skeleton method");
    }

    public MessageFormat(String pattern, Locale locale) {
        throw new RuntimeException("skeleton method");
    }

    public void setLocale(Locale locale) {
        throw new RuntimeException("skeleton method");
    }

    public Locale getLocale(@Readonly MessageFormat this)  {
        throw new RuntimeException("skeleton method");
    }

    public void applyPattern(String pattern) {
        throw new RuntimeException("skeleton method");
    }

    public String toPattern(@Readonly MessageFormat this)  {
        throw new RuntimeException("skeleton method");
    }

    public void setFormatsByArgumentIndex(Format[] newFormats) {
        throw new RuntimeException("skeleton method");
    }

    public void setFormats(Format[] newFormats) {
        throw new RuntimeException("skeleton method");
    }

    public void setFormatByArgumentIndex(int argumentIndex, Format newFormat) {
        throw new RuntimeException("skeleton method");
    }

    public void setFormat(int formatElementIndex, Format newFormat) {
        throw new RuntimeException("skeleton method");
    }

    public Format[] getFormatsByArgumentIndex(@Readonly MessageFormat this)  {
        throw new RuntimeException("skeleton method");
    }

    public Format[] getFormats(@Readonly MessageFormat this)  {
        throw new RuntimeException("skeleton method");
    }

    public final StringBuffer format(@Readonly MessageFormat this, @Readonly Object[] arguments, StringBuffer result,
                                     FieldPosition pos) 
    {
        throw new RuntimeException("skeleton method");
    }

    public static String format(String pattern, @Readonly Object ... arguments) {
        throw new RuntimeException("skeleton method");
    }

    public final StringBuffer format(@Readonly MessageFormat this, @Readonly Object arguments, StringBuffer result,
                                     FieldPosition pos) 
    {
        throw new RuntimeException("skeleton method");
    }

    public AttributedCharacterIterator formatToCharacterIterator(@Readonly MessageFormat this, @Readonly Object arguments)  {
        throw new RuntimeException("skeleton method");
    }

    public Object[] parse(@Readonly MessageFormat this, String source, ParsePosition pos)  {
        throw new RuntimeException("skeleton method");
    }

    public Object[] parse(@Readonly MessageFormat this, String source)  throws ParseException {
        throw new RuntimeException("skeleton method");
    }

    public Object parseObject(@Readonly MessageFormat this, String source, ParsePosition pos)  {
        throw new RuntimeException("skeleton method");
    }

    public Object clone(@Readonly MessageFormat this)  {
        throw new RuntimeException("skeleton method");
    }

    public boolean equals(@Readonly MessageFormat this, Object obj)  {
        throw new RuntimeException("skeleton method");
    }

    public int hashCode(@Readonly MessageFormat this)  {
        throw new RuntimeException("skeleton method");
    }

    public static class Field extends Format.Field {
        private static final long serialVersionUID = 7899943957617360810L;

        protected Field(String name) {
            super(name); // why is this needed to compile?
            throw new RuntimeException("skeleton method");
        }

        protected Object readResolve(@Readonly Field this)  throws InvalidObjectException {
            throw new RuntimeException("skeleton method");
        }

        public final static Field ARGUMENT;
    }
}
