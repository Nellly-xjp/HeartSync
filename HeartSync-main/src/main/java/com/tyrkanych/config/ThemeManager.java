package com.tyrkanych.config;

public class ThemeManager {

    private static String currentTheme = "/styles/styles.css";

    public static String getCurrentTheme() {
        return currentTheme;
    }

    public static void setCurrentTheme(String theme) {
        currentTheme = theme;
    }
}