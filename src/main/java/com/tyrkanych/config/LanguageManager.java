package com.tyrkanych.config;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

public class LanguageManager {
    private static String currentLang = "uk";
    private static ResourceBundle bundle = loadBundle("uk");
    private static final List<Runnable> listeners = new ArrayList<>();

    public static String getCurrentLang() {
        return currentLang;
    }

    public static void setLanguage(String lang) {
        currentLang = lang;
        bundle = loadBundle(lang);
        notifyListeners();
    }

    public static String get(String key) {
        try {
            return bundle.getString(key);
        } catch (Exception e) {
            return key; // повертаємо ключ якщо переклад не знайдено
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