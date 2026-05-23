package com.tyrkanych.config;

import javafx.scene.Scene;
import java.util.ArrayList;
import java.util.List;

public class ThemeManager {
    private static String currentTheme = "/styles/styles.css";
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
        // Застосовуємо до всіх зареєстрованих сцен
        for (Scene scene : managedScenes) {
            applyThemeTo(scene);
        }
    }

    public static void applyTheme() {
        for (Scene scene : managedScenes) {
            applyThemeTo(scene);
        }
    }

    // Для зворотної сумісності з MainController
    public static void setMainScene(Scene scene) {
        registerScene(scene);
    }

    private static void applyThemeTo(Scene scene) {
        if (scene == null) return;
        java.net.URL url = ThemeManager.class.getResource(currentTheme);
        System.out.println("Applying theme: " + currentTheme);
        System.out.println("URL: " + url);
        System.out.println("managedScenes size: " + managedScenes.size());
        if (url == null) {
            System.out.println("❌ CSS файл не знайдено!");
            return;
        }
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
}