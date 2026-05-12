package com.tyrkanych.controller;

import com.tyrkanych.config.ThemeManager;
import com.tyrkanych.entity.Preferences;
import com.tyrkanych.service.PreferencesService;
import com.tyrkanych.session.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SettingsController {

    // Шляхи до CSS файлів
    private static final String LIGHT_CSS = "/styles/styles.css";
    private static final String DARK_CSS = "/styles/styles-dark.css";
    private final PreferencesService preferencesService;
    private final SessionManager sessionManager;
    @FXML
    private ComboBox<String> prefGender;
    @FXML
    private TextField prefMinAge;
    @FXML
    private TextField prefMaxAge;
    @FXML
    private TextField prefCity;
    @FXML
    private CheckBox notifyMatches;
    @FXML
    private CheckBox notifyMessages;
    @FXML
    private Label settingsStatus;
    @FXML
    private Label themeStatus;
    @FXML
    private Button btnLightTheme;
    @FXML
    private Button btnDarkTheme;

    @Autowired
    public SettingsController(PreferencesService preferencesService,
            SessionManager sessionManager) {
        this.preferencesService = preferencesService;
        this.sessionManager = sessionManager;
    }

    @FXML
    public void initialize() {
        prefGender.getItems().addAll("male", "female", "other", "Будь-яка");

        Long userId = sessionManager.getCurrentUserId();
        if (userId != null) {
            preferencesService.findByUserId(userId).ifPresent(p -> {
                prefGender.setValue(p.getPreferredGender());
                prefMinAge.setText(p.getMinAge() != null ? String.valueOf(p.getMinAge()) : "18");
                prefMaxAge.setText(p.getMaxAge() != null ? String.valueOf(p.getMaxAge()) : "60");
                prefCity.setText(p.getCity() != null ? p.getCity() : "");
            });
        }

        // Показуємо поточну тему
        updateThemeStatus();
    }

    @FXML
    private void setLightTheme() {
        applyTheme(LIGHT_CSS, "☀️ Світла тема активна");
    }

    @FXML
    private void setDarkTheme() {
        applyTheme(DARK_CSS, "🌙 Темна тема активна");
    }

    private void applyTheme(String cssPath, String statusText) {
        try {
            // Беремо головне вікно
            Stage stage = (Stage) prefGender.getScene().getWindow();
            Scene scene = stage.getScene();

            String cssUrl = getClass().getResource(cssPath).toExternalForm();

            // Очищаємо і додаємо нову тему
            scene.getStylesheets().clear();
            scene.getStylesheets().add(cssUrl);

            // Зберігаємо вибір
            ThemeManager.setCurrentTheme(cssPath);

            themeStatus.setText(statusText);
            themeStatus.getStyleClass().removeAll("status-error");
            themeStatus.getStyleClass().add("status-success");

        } catch (Exception e) {
            themeStatus.setText("❌ Помилка: " + e.getMessage());
        }
    }

    private void updateThemeStatus() {
        if (ThemeManager.getCurrentTheme().contains("dark")) {
            themeStatus.setText("🌙 Темна тема активна");
        } else {
            themeStatus.setText("☀️ Світла тема активна");
        }
    }

    @FXML
    private void savePreferences() {
        Long userId = sessionManager.getCurrentUserId();
        if (userId == null) {
            return;
        }

        Preferences pref = Preferences.builder()
                .userId(userId)
                .preferredGender(prefGender.getValue())
                .minAge(parseIntOrDefault(prefMinAge.getText(), 18))
                .maxAge(parseIntOrDefault(prefMaxAge.getText(), 60))
                .city(prefCity.getText().trim())
                .build();

        preferencesService.findByUserId(userId).ifPresentOrElse(
                existing -> {
                    pref.setId(existing.getId());
                    preferencesService.update(pref);
                },
                () -> preferencesService.save(pref)
        );

        settingsStatus.setText("✅ Збережено");
        settingsStatus.getStyleClass().add("status-success");
    }

    @FXML
    private void savePrivacy() {
    }

    @FXML
    private void deleteAccount() {
        settingsStatus.setText("❌ Функція у розробці");
        settingsStatus.getStyleClass().add("status-error");
    }

    private int parseIntOrDefault(String text, int def) {
        try {
            return Integer.parseInt(text.trim());
        } catch (NumberFormatException e) {
            return def;
        }
    }
}