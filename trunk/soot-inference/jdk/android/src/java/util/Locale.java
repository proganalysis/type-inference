package java.util;

import checkers.inference.reim.quals.*; 
import checkers.inference.sflow.quals.*; 

public final class Locale implements java.lang.Cloneable, java.io.Serializable {
    private static class LocaleNameGetter implements sun.util.LocaleServiceProviderPool.LocalizedObjectGetter<java.util.spi.LocaleNameProvider,java.lang.String> {
        private static java.util.Locale.LocaleNameGetter INSTANCE;
        private LocaleNameGetter() { throw new RuntimeException("skeleton method"); }
        public java.lang.String getObject(java.util.spi.LocaleNameProvider arg0, java.util.Locale arg1, java.lang.String arg2, java.lang.Object[] arg3) { throw new RuntimeException("skeleton method"); }
    }
    private static java.util.concurrent.ConcurrentHashMap<java.lang.String,java.util.Locale> cache;
    public static java.util.Locale ENGLISH;
    public static java.util.Locale FRENCH;
    public static java.util.Locale GERMAN;
    public static java.util.Locale ITALIAN;
    public static java.util.Locale JAPANESE;
    public static java.util.Locale KOREAN;
    public static java.util.Locale CHINESE;
    public static java.util.Locale SIMPLIFIED_CHINESE;
    public static java.util.Locale TRADITIONAL_CHINESE;
    public static java.util.Locale FRANCE;
    public static java.util.Locale GERMANY;
    public static java.util.Locale ITALY;
    public static java.util.Locale JAPAN;
    public static java.util.Locale KOREA;
    public static java.util.Locale CHINA;
    public static java.util.Locale PRC;
    public static java.util.Locale TAIWAN;
    public static java.util.Locale UK;
    public static java.util.Locale US;
    public static java.util.Locale CANADA;
    public static java.util.Locale CANADA_FRENCH;
    public static java.util.Locale ROOT;
    static long serialVersionUID;
    private static int DISPLAY_LANGUAGE;
    private static int DISPLAY_COUNTRY;
    private static int DISPLAY_VARIANT;
    private java.lang.String language;
    private java.lang.String country;
    private java.lang.String variant;
    private volatile int hashcode;
    private transient volatile int hashCodeValue;
    private static java.util.Locale defaultLocale;
    private static volatile java.lang.String[] isoLanguages;
    private static volatile java.lang.String[] isoCountries;
    public Locale(java.lang.String arg0, java.lang.String arg1, java.lang.String arg2) { throw new RuntimeException("skeleton method"); }
    public Locale(java.lang.String arg0, java.lang.String arg1) { throw new RuntimeException("skeleton method"); }
    public Locale(java.lang.String arg0) { throw new RuntimeException("skeleton method"); }
    private Locale(java.lang.String arg0, java.lang.String arg1, boolean arg2) { throw new RuntimeException("skeleton method"); }
    private static java.util.Locale createSingleton(java.lang.String arg0, java.lang.String arg1, java.lang.String arg2) { throw new RuntimeException("skeleton method"); }
    static java.util.Locale getInstance(java.lang.String arg0, java.lang.String arg1, java.lang.String arg2) { throw new RuntimeException("skeleton method"); }
    public static java.util.Locale getDefault() { throw new RuntimeException("skeleton method"); }
    public static synchronized void setDefault(java.util.Locale arg0) { throw new RuntimeException("skeleton method"); }
    public static java.util.Locale[] getAvailableLocales() { throw new RuntimeException("skeleton method"); }
    public static java.lang.String[] getISOCountries() { throw new RuntimeException("skeleton method"); }
    public static java.lang.String[] getISOLanguages() { throw new RuntimeException("skeleton method"); }
    private static final java.lang.String[] getISO2Table(java.lang.String arg0) { throw new RuntimeException("skeleton method"); }
    @ReadonlyThis public java.lang.String getLanguage() { throw new RuntimeException("skeleton method"); }
    @ReadonlyThis public java.lang.String getCountry() { throw new RuntimeException("skeleton method"); }
    @ReadonlyThis public java.lang.String getVariant() { throw new RuntimeException("skeleton method"); }
    @ReadonlyThis public final java.lang.String toString() { throw new RuntimeException("skeleton method"); }
    @ReadonlyThis public java.lang.String getISO3Language() throws java.util.MissingResourceException { throw new RuntimeException("skeleton method"); }
    @ReadonlyThis public java.lang.String getISO3Country() throws java.util.MissingResourceException { throw new RuntimeException("skeleton method"); }
    @ReadonlyThis private static final java.lang.String getISO3Code(java.lang.String arg0, java.lang.String arg1) { throw new RuntimeException("skeleton method"); }
    @ReadonlyThis public final java.lang.String getDisplayLanguage() { throw new RuntimeException("skeleton method"); }
    @ReadonlyThis public java.lang.String getDisplayLanguage(java.util.Locale arg0) { throw new RuntimeException("skeleton method"); }
    @ReadonlyThis public final java.lang.String getDisplayCountry() { throw new RuntimeException("skeleton method"); }
    @ReadonlyThis public java.lang.String getDisplayCountry(java.util.Locale arg0) { throw new RuntimeException("skeleton method"); }
    @ReadonlyThis private java.lang.String getDisplayString(java.lang.String arg0, java.util.Locale arg1, int arg2) { throw new RuntimeException("skeleton method"); }
    @ReadonlyThis public final java.lang.String getDisplayVariant() { throw new RuntimeException("skeleton method"); }
    @ReadonlyThis public java.lang.String getDisplayVariant(java.util.Locale arg0) { throw new RuntimeException("skeleton method"); }
    @ReadonlyThis public final java.lang.String getDisplayName() { throw new RuntimeException("skeleton method"); }
    @ReadonlyThis public java.lang.String getDisplayName(java.util.Locale arg0) { throw new RuntimeException("skeleton method"); }
    public java.lang.Object clone() { throw new RuntimeException("skeleton method"); }
    public int hashCode() { throw new RuntimeException("skeleton method"); }
    public boolean equals(java.lang.Object arg0) { throw new RuntimeException("skeleton method"); }
//    private java.lang.String[] getDisplayVariantArray(sun.util.resources.OpenListResourceBundle arg0, java.util.Locale arg1) { throw new RuntimeException("skeleton method"); }
    private static java.lang.String formatList(java.lang.String[] arg0, java.lang.String arg1, java.lang.String arg2) { throw new RuntimeException("skeleton method"); }
    private static java.lang.String[] composeList(java.text.MessageFormat arg0, java.lang.String[] arg1) { throw new RuntimeException("skeleton method"); }
    private java.lang.Object readResolve() throws java.io.ObjectStreamException { throw new RuntimeException("skeleton method"); }
    @ReadonlyThis private java.lang.String toLowerCase(java.lang.String arg0) { throw new RuntimeException("skeleton method"); }
    @ReadonlyThis private java.lang.String toUpperCase(java.lang.String arg0) { throw new RuntimeException("skeleton method"); }
    private java.lang.String convertOldISOCodes(java.lang.String arg0) { throw new RuntimeException("skeleton method"); }
}
