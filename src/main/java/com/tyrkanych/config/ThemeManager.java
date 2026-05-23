package com.tyrkanych.config;

import javafx.scene.Scene;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class ThemeManager {
    private static final String PREFS_FILE = "heartsync.properties";
    private static String currentTheme = loadSavedTheme();
    private static final List<Scene> managedScenes = new ArrayList<>();

    public static void registerScene(Scene scene) {
        if (!managedScenes.contains(scene)) {
            managedScenes.add(scene);
        }
        applyThemeTo(scene);
    }

    public static String getCurrentTheme() {
        return currentTheme;
    }

    public static void setCurrentTheme(String theme) {
        currentTheme = theme;
        saveTheme(theme);
        for (Scene scene : managedScenes) {
            applyThemeTo(scene);
        }
    }

    public static void applyTheme() {
        for (Scene scene : managedScenes) {
            applyThemeTo(scene);
        }
    }

    public static void setMainScene(Scene scene) {
        registerScene(scene);
    }

    private static void applyThemeTo(Scene scene) {
        if (scene == null) return;
        java.net.URL url = ThemeManager.class.getResource(currentTheme);
        if (url == null) return;
        String cssUrl = url.toExternalForm();
        if (javafx.application.Platform.isFxApplicationThread()) {
            scene.getStylesheets().clear();
            scene.getStylesheets().add(cssUrl);
        } else {
            javafx.application.Platform.runLater(() -> {
                scene.getStylesheets().clear();
                scene.getStylesheets().add(cssUrl);
            });
        }
    }

    private static String loadSavedTheme() {
        try {
            Properties props = new Properties();
            props.load(new FileInputStream(PREFS_FILE));
            return props.getProperty("theme", "/styles/styles.css");
        } catch (Exception e) {
            return "/styles/styles.css";
        }
    }

    private static void saveTheme(String theme) {
        try {
            Properties props = new Properties();
            try {
                props.load(new FileInputStream(PREFS_FILE));
            } catch (Exception ignored) {}
            props.setProperty("theme", theme);
            props.store(new FileOutputStream(PREFS_FILE), "HeartSync preferences");
        } catch (Exception e) {
            System.err.println("Cannot save theme: " + e.getMessage());
        }
    }
}