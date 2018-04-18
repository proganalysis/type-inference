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

    @ReadonlyThis public Locale getLocale()  {
        throw new RuntimeException("skeleton method");
    }

    public void applyPattern(String pattern) {
        throw new RuntimeException("skeleton method");
    }

    @ReadonlyThis public String toPattern()  {
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

    @ReadonlyThis public Format[] getFormatsByArgumentIndex()  {
        throw new RuntimeException("skeleton method");
    }

    @ReadonlyThis public Format[] getFormats()  {
        throw new RuntimeException("skeleton method");
    }

    @ReadonlyThis public final StringBuffer format( @Readonly Object[] arguments, StringBuffer result,
                                     FieldPosition pos) 
    {
        throw new RuntimeException("skeleton method");
    }

    public static String format(String pattern, @Readonly Object ... arguments) {
        throw new RuntimeException("skeleton method");
    }

    @ReadonlyThis public final StringBuffer format( @Readonly Object arguments, StringBuffer result,
                                     FieldPosition pos) 
    {
        throw new RuntimeException("skeleton method");
    }

    @ReadonlyThis public AttributedCharacterIterator formatToCharacterIterator( @Readonly Object arguments)  {
        throw new RuntimeException("skeleton method");
    }

    @ReadonlyThis public Object[] parse( String source, ParsePosition pos)  {
        throw new RuntimeException("skeleton method");
    }

    @ReadonlyThis public Object[] parse( String source)  throws ParseException {
        throw new RuntimeException("skeleton method");
    }

    @ReadonlyThis public Object parseObject( String source, ParsePosition pos)  {
        throw new RuntimeException("skeleton method");
    }

    @ReadonlyThis public Object clone()  {
        throw new RuntimeException("skeleton method");
    }

    @ReadonlyThis public boolean equals( Object obj)  {
        throw new RuntimeException("skeleton method");
    }

    @ReadonlyThis public int hashCode()  {
        throw new RuntimeException("skeleton method");
    }

    public static class Field extends Format.Field {
        private static final long serialVersionUID = 7899943957617360810L;

        protected Field(String name) {
            super(name); // why is this needed to compile?
            throw new RuntimeException("skeleton method");
        }

        @ReadonlyThis protected Object readResolve()  throws InvalidObjectException {
            throw new RuntimeException("skeleton method");
        }

        public final static Field ARGUMENT;
    }
}
