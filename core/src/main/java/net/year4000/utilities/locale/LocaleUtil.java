package net.year4000.utilities.locale;

@SuppressWarnings("unused")
public interface LocaleUtil {
    /** Translate to the specific locale with formatting */
    public String get(String key, Object... args);
}
