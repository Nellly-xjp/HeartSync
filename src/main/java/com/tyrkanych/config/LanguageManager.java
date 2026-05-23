package com.tyrkanych.config;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

public class LanguageManager {
    private static final String PREFS_FILE = "heartsync.properties";
    private static String currentLang = loadSavedLang();
    private static ResourceBundle bundle = loadBundle(currentLang);
    private static final List<Runnable> listeners = new ArrayList<>();

    public static String getCurrentLang() {
        return currentLang;
    }

    public static void setLanguage(String lang) {
        currentLang = lang;
        bundle = loadBundle(lang);
        saveLang(lang);
        notifyListeners();
    }

    public static String get(String key) {
        try {
            return bundle.getString(key);
        } catch (Exception e) {
            return key;
        }
    }

    public static void addListener(Runnable listener) {
        listeners.add(listener);
    }

    public static void removeListener(Runnable listener) {
        listeners.remove(listener);
    }

    private static void notifyListeners() {
        for (Runnable listener : listeners) {
            javafx.application.Platform.runLater(listener);
        }
    }

    private static String loadSavedLang() {
        try {
            Properties props = new Properties();
            props.load(new FileInputStream(PREFS_FILE));
            return props.getProperty("language", "uk");
        } catch (Exception e) {
            return "uk";
        }
    }

    private static void saveLang(String lang) {
        try {
            Properties props = new Properties();
            // Зберігаємо існуючі налаштування
            try {
                props.load(new FileInputStream(PREFS_FILE));
            } catch (Exception ignored) {}
            props.setProperty("language", lang);
            props.store(new FileOutputStream(PREFS_FILE), "HeartSync preferences");
        } catch (Exception e) {
            System.err.println("Cannot save language: " + e.getMessage());
        }
    }

    private static ResourceBundle loadBundle(String lang) {
        try {
            var stream = LanguageManager.class
                    .getResourceAsStream("/i18n/messages_" + lang + ".properties");
            return new PropertyResourceBundle(
                    new InputStreamReader(stream, StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new RuntimeException("Cannot load language: " + lang, e);
        }
    }
}