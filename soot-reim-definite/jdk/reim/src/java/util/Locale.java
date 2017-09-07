package java.util;
import checkers.inference.reim.quals.*;

import java.io.*;

public final class Locale implements Cloneable, Serializable {

    static public final Locale ENGLISH;
    static public final Locale FRENCH;
    static public final Locale GERMAN;
    static public final Locale ITALIAN;
    static public final Locale JAPANESE;
    static public final Locale KOREAN;
    static public final Locale CHINESE;
    static public final Locale SIMPLIFIED_CHINESE;
    static public final Locale TRADITIONAL_CHINESE;
    static public final Locale FRANCE;
    static public final Locale GERMANY;
    static public final Locale ITALY;
    static public final Locale JAPAN;
    static public final Locale KOREA;
    static public final Locale CHINAE;
    static public final Locale PRC;
    static public final Locale TAIWAN; 
    static public final Locale UK;
    static public final Locale US;
    static public final Locale CANADA;
    static public final Locale CANADA_FRENCH;
    static public final Locale ROOT;

    static final long serialVersionUID = 9149081749638150636L;

    public Locale(String language, String country, String variant) {
        throw new RuntimeException("skeleton method");
    }

    public Locale(String language, String country) {
        throw new RuntimeException("skeleton method");
    }

    public Locale(String language) {
        throw new RuntimeException("skeleton method");
    }

    static Locale getInstance(String language, String country, String variant) {
        throw new RuntimeException("skeleton method");
    }

    public static Locale getDefault() {
        throw new RuntimeException("skeleton method");
    }

    public static synchronized void setDefault(Locale newLocale) {
        throw new RuntimeException("skeleton method");
    }

    public static Locale[] getAvailableLocales() {
        throw new RuntimeException("skeleton method");
    }

    public static String[] getISOCountries() {
        throw new RuntimeException("skeleton method");
    }

    public static String[] getISOLanguages() {
        throw new RuntimeException("skeleton method");
    }

    @ReadonlyThis public String getLanguage()  {
        throw new RuntimeException("skeleton method");
    }

    @ReadonlyThis public String getCountry()  {
        throw new RuntimeException("skeleton method");
    }

    @ReadonlyThis public String getVariant()  {
        throw new RuntimeException("skeleton method");
    }

    @ReadonlyThis public final String toString()  {
        throw new RuntimeException("skeleton method");
    }

    @ReadonlyThis public String getISO3Language()  throws MissingResourceException {
        throw new RuntimeException("skeleton method");
    }

    @ReadonlyThis public String getISO3Country()  throws MissingResourceException {
        throw new RuntimeException("skeleton method");
    }

    public final String getDisplayLanguage() {
        throw new RuntimeException("skeleton method");
    }

    @ReadonlyThis public String getDisplayLanguage( Locale inLocale)  {
        throw new RuntimeException("skeleton method");
    }

    public final String getDisplayCountry() {
        throw new RuntimeException("skeleton method");
    }

    @ReadonlyThis public String getDisplayCountry( Locale inLocale)  {
        throw new RuntimeException("skeleton method");
    }

    public final String getDisplayVariant() {
        throw new RuntimeException("skeleton method");
    }

    @ReadonlyThis public String getDisplayVariant( Locale inLocale)  {
        throw new RuntimeException("skeleton method");
    }

    public final String getDisplayName() {
        return getDisplayName(getDefault());
    }

    @ReadonlyThis public String getDisplayName( Locale inLocale)  {
        throw new RuntimeException("skeleton method");
    }

    public Object clone() {
        throw new RuntimeException("skeleton method");
    }

    public int hashCode() {
        throw new RuntimeException("skeleton method");
    }

    @ReadonlyThis public boolean equals( @Readonly Object obj)  {
        throw new RuntimeException("skeleton method");
    }
}
