package eu.askadev.magic.service;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Service for managing application localization and language switching.
 */
public class LocalizationService {

    private static LocalizationService instance;
    private ResourceBundle resourceBundle;
    private Locale currentLocale;
    private static final String BUNDLE_NAME = "messages";

    private LocalizationService() {
        setLocale(Locale.ENGLISH);
    }

    public static LocalizationService getInstance() {
        if (instance == null) {
            instance = new LocalizationService();
        }
        return instance;
    }

    /**
     * Set the current locale and load the appropriate resource bundle.
     */
    public void setLocale(Locale locale) {
        try {
            this.currentLocale = locale;
            this.resourceBundle = ResourceBundle.getBundle(BUNDLE_NAME, locale);
            System.out.println("INFO: Language changed to " + locale.getDisplayLanguage());
        } catch (Exception e) {
            System.err.println("ERROR: Failed to load locale " + locale.getLanguage());
            e.printStackTrace();
            // Fallback to English
            this.currentLocale = Locale.ENGLISH;
            this.resourceBundle = ResourceBundle.getBundle(BUNDLE_NAME, Locale.ENGLISH);
        }
    }

    /**
     * Get translated string for a key.
     */
    public String get(String key) {
        try {
            return resourceBundle.getString(key);
        } catch (Exception e) {
            System.err.println("WARNING: Missing translation key: " + key);
            return key;
        }
    }

    /**
     * Get current locale.
     */
    public Locale getCurrentLocale() {
        return currentLocale;
    }

    /**
     * Check if current language is French.
     */
    public boolean isFrench() {
        return currentLocale.getLanguage().equals("fr");
    }

    /**
     * Check if current language is English.
     */
    public boolean isEnglish() {
        return currentLocale.getLanguage().equals("en");
    }

    /**
     * Switch to English.
     */
    public void switchToEnglish() {
        setLocale(Locale.ENGLISH);
    }

    /**
     * Switch to French.
     */
    public void switchToFrench() {
        setLocale(Locale.FRENCH);
    }
}

