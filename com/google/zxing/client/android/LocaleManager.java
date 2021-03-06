package com.google.zxing.client.android;

import android.content.Context;
import android.preference.PreferenceManager;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public final class LocaleManager {
    private static final String DEFAULT_COUNTRY = "US";
    private static final String DEFAULT_LANGUAGE = "en";
    private static final String DEFAULT_TLD = "com";
    private static final Map<String, String> GOOGLE_BOOK_SEARCH_COUNTRY_TLD = GOOGLE_COUNTRY_TLD;
    private static final Map<String, String> GOOGLE_COUNTRY_TLD;
    private static final Map<String, String> GOOGLE_PRODUCT_SEARCH_COUNTRY_TLD;
    private static final Collection<String> TRANSLATED_HELP_ASSET_LANGUAGES = Arrays.asList("de", DEFAULT_LANGUAGE, "es", "fr", "it", "ja", "ko", "nl", "pt", "ru", "uk", "zh-rCN", "zh-rTW", "zh-rHK");

    static {
        HashMap hashMap = new HashMap();
        GOOGLE_COUNTRY_TLD = hashMap;
        hashMap.put("AR", "com.ar");
        GOOGLE_COUNTRY_TLD.put("AU", "com.au");
        GOOGLE_COUNTRY_TLD.put("BR", "com.br");
        GOOGLE_COUNTRY_TLD.put("BG", "bg");
        GOOGLE_COUNTRY_TLD.put(Locale.CANADA.getCountry(), "ca");
        GOOGLE_COUNTRY_TLD.put(Locale.CHINA.getCountry(), "cn");
        GOOGLE_COUNTRY_TLD.put("CZ", "cz");
        GOOGLE_COUNTRY_TLD.put("DK", "dk");
        GOOGLE_COUNTRY_TLD.put("FI", "fi");
        GOOGLE_COUNTRY_TLD.put(Locale.FRANCE.getCountry(), "fr");
        GOOGLE_COUNTRY_TLD.put(Locale.GERMANY.getCountry(), "de");
        GOOGLE_COUNTRY_TLD.put("GR", "gr");
        GOOGLE_COUNTRY_TLD.put("HU", "hu");
        GOOGLE_COUNTRY_TLD.put("ID", "co.id");
        GOOGLE_COUNTRY_TLD.put("IL", "co.il");
        GOOGLE_COUNTRY_TLD.put(Locale.ITALY.getCountry(), "it");
        GOOGLE_COUNTRY_TLD.put(Locale.JAPAN.getCountry(), "co.jp");
        GOOGLE_COUNTRY_TLD.put(Locale.KOREA.getCountry(), "co.kr");
        GOOGLE_COUNTRY_TLD.put("NL", "nl");
        GOOGLE_COUNTRY_TLD.put("PL", "pl");
        GOOGLE_COUNTRY_TLD.put("PT", "pt");
        GOOGLE_COUNTRY_TLD.put("RO", "ro");
        GOOGLE_COUNTRY_TLD.put("RU", "ru");
        GOOGLE_COUNTRY_TLD.put("SK", "sk");
        GOOGLE_COUNTRY_TLD.put("SI", "si");
        GOOGLE_COUNTRY_TLD.put("ES", "es");
        GOOGLE_COUNTRY_TLD.put("SE", "se");
        GOOGLE_COUNTRY_TLD.put("CH", "ch");
        GOOGLE_COUNTRY_TLD.put(Locale.TAIWAN.getCountry(), "tw");
        GOOGLE_COUNTRY_TLD.put("TR", "com.tr");
        GOOGLE_COUNTRY_TLD.put("UA", "com.ua");
        GOOGLE_COUNTRY_TLD.put(Locale.UK.getCountry(), "co.uk");
        GOOGLE_COUNTRY_TLD.put(Locale.US.getCountry(), DEFAULT_TLD);
        HashMap hashMap2 = new HashMap();
        GOOGLE_PRODUCT_SEARCH_COUNTRY_TLD = hashMap2;
        hashMap2.put("AU", "com.au");
        GOOGLE_PRODUCT_SEARCH_COUNTRY_TLD.put(Locale.FRANCE.getCountry(), "fr");
        GOOGLE_PRODUCT_SEARCH_COUNTRY_TLD.put(Locale.GERMANY.getCountry(), "de");
        GOOGLE_PRODUCT_SEARCH_COUNTRY_TLD.put(Locale.ITALY.getCountry(), "it");
        GOOGLE_PRODUCT_SEARCH_COUNTRY_TLD.put(Locale.JAPAN.getCountry(), "co.jp");
        GOOGLE_PRODUCT_SEARCH_COUNTRY_TLD.put("NL", "nl");
        GOOGLE_PRODUCT_SEARCH_COUNTRY_TLD.put("ES", "es");
        GOOGLE_PRODUCT_SEARCH_COUNTRY_TLD.put("CH", "ch");
        GOOGLE_PRODUCT_SEARCH_COUNTRY_TLD.put(Locale.UK.getCountry(), "co.uk");
        GOOGLE_PRODUCT_SEARCH_COUNTRY_TLD.put(Locale.US.getCountry(), DEFAULT_TLD);
    }

    private LocaleManager() {
    }

    public static String getCountryTLD(Context context) {
        return doGetTLD(GOOGLE_COUNTRY_TLD, context);
    }

    public static String getProductSearchCountryTLD(Context context) {
        return doGetTLD(GOOGLE_PRODUCT_SEARCH_COUNTRY_TLD, context);
    }

    public static String getBookSearchCountryTLD(Context context) {
        return doGetTLD(GOOGLE_BOOK_SEARCH_COUNTRY_TLD, context);
    }

    public static boolean isBookSearchUrl(String str) {
        return str.startsWith("http://google.com/books") || str.startsWith("http://books.google.");
    }

    private static String getSystemCountry() {
        Locale locale = Locale.getDefault();
        if (locale == null) {
            return DEFAULT_COUNTRY;
        }
        return locale.getCountry();
    }

    private static String getSystemLanguage() {
        Locale locale = Locale.getDefault();
        if (locale == null) {
            return DEFAULT_LANGUAGE;
        }
        String language = locale.getLanguage();
        if (!Locale.SIMPLIFIED_CHINESE.getLanguage().equals(language)) {
            return language;
        }
        return language + "-r" + getSystemCountry();
    }

    static String getTranslatedAssetLanguage() {
        String systemLanguage = getSystemLanguage();
        return TRANSLATED_HELP_ASSET_LANGUAGES.contains(systemLanguage) ? systemLanguage : DEFAULT_LANGUAGE;
    }

    private static String doGetTLD(Map<String, String> map, Context context) {
        String str = map.get(getCountry(context));
        return str == null ? DEFAULT_TLD : str;
    }

    private static String getCountry(Context context) {
        String string = PreferenceManager.getDefaultSharedPreferences(context).getString(PreferencesActivity.KEY_SEARCH_COUNTRY, "-");
        if (string == null || string.isEmpty() || "-".equals(string)) {
            return getSystemCountry();
        }
        return string;
    }
}
