package com.tyrkanych.controller;

import com.tyrkanych.config.LanguageManager;
import com.tyrkanych.config.ThemeManager;
import com.tyrkanych.entity.Preferences;
import com.tyrkanych.service.PreferencesService;
import com.tyrkanych.session.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SettingsController {

    private static final String LIGHT_CSS = "/styles/styles.css";
    private static final String DARK_CSS = "/styles/styles-dark.css";

    private final PreferencesService preferencesService;
    private final SessionManager sessionManager;

    @FXML private ComboBox<String> prefGender;
    @FXML private TextField prefMinAge;
    @FXML private TextField prefMaxAge;
    @FXML private TextField prefCity;
    @FXML private CheckBox notifyMatches;
    @FXML private CheckBox notifyMessages;
    @FXML private Label settingsStatus;
    @FXML private Label themeStatus;
    @FXML private Button btnLightTheme;
    @FXML private Button btnDarkTheme;
    @FXML private Button btnLangUk;
    @FXML private Button btnLangEn;

    @FXML private Label labelSettingsTitle;  // ← додано
    @FXML private Label labelThemeSection;
    @FXML private Label labelPrefsSection;
    @FXML private Label labelGender;
    @FXML private Label labelAgeFrom;
    @FXML private Label labelAgeTo;
    @FXML private Label labelCity;
    @FXML private Label labelNotifications;
    @FXML private Label labelDanger;
    @FXML private Label labelDeleteWarning;
    @FXML private Label labelLangSection;
    @FXML private Button btnSavePrefs;
    @FXML private Button btnSaveNotifications;
    @FXML private Button btnDeleteAccount;
    @FXML private CheckBox checkNotifyMessages;
    @FXML private CheckBox checkNotifyMatches;

    @Autowired
    public SettingsController(PreferencesService preferencesService,
            SessionManager sessionManager) {
        this.preferencesService = preferencesService;
        this.sessionManager = sessionManager;
    }

    @FXML
    public void initialize() {
        applyLanguage();
        LanguageManager.addListener(this::applyLanguage);

        Long userId = sessionManager.getCurrentUserId();
        if (userId != null) {
            preferencesService.findByUserId(userId).ifPresent(p -> {
                prefGender.setValue(p.getPreferredGender());
                prefMinAge.setText(p.getMinAge() != null ? String.valueOf(p.getMinAge()) : "18");
                prefMaxAge.setText(p.getMaxAge() != null ? String.valueOf(p.getMaxAge()) : "60");
                prefCity.setText(p.getCity() != null ? p.getCity() : "");
            });
        }

        updateThemeStatus();
    }

    private void applyLanguage() {
        // Заголовок ← додано
        if (labelSettingsTitle != null)
            labelSettingsTitle.setText(LanguageManager.get("settings.title"));

        // Кнопки теми
        if (btnLightTheme != null)
            btnLightTheme.setText(LanguageManager.get("settings.theme.light"));
        if (btnDarkTheme != null)
            btnDarkTheme.setText(LanguageManager.get("settings.theme.dark"));

        // Кнопки мови
        if (btnLangUk != null)
            btnLangUk.setText("🇺🇦 Українська");
        if (btnLangEn != null)
            btnLangEn.setText("🇬🇧 English");

        // Секції
        if (labelThemeSection != null)
            labelThemeSection.setText(LanguageManager.get("settings.theme"));
        if (labelPrefsSection != null)
            labelPrefsSection.setText(LanguageManager.get("settings.prefs"));
        if (labelGender != null)
            labelGender.setText(LanguageManager.get("settings.gender"));
        if (labelAgeFrom != null)
            labelAgeFrom.setText(LanguageManager.get("settings.age.from"));
        if (labelAgeTo != null)
            labelAgeTo.setText(LanguageManager.get("settings.age.to"));
        if (labelCity != null)
            labelCity.setText(LanguageManager.get("settings.city"));
        if (labelNotifications != null)
            labelNotifications.setText(LanguageManager.get("settings.notifications"));
        if (labelDanger != null)
            labelDanger.setText(LanguageManager.get("settings.danger"));
        if (labelDeleteWarning != null)
            labelDeleteWarning.setText(LanguageManager.get("settings.delete.warning"));
        if (labelLangSection != null)
            labelLangSection.setText(LanguageManager.get("settings.language"));

        // Кнопки дій
        if (btnSavePrefs != null)
            btnSavePrefs.setText(LanguageManager.get("settings.save.prefs"));
        if (btnSaveNotifications != null)
            btnSaveNotifications.setText(LanguageManager.get("settings.save"));
        if (btnDeleteAccount != null)
            btnDeleteAccount.setText(LanguageManager.get("settings.delete"));

        // Чекбокси
        if (checkNotifyMessages != null)
            checkNotifyMessages.setText(LanguageManager.get("settings.notify.messages"));
        if (checkNotifyMatches != null)
            checkNotifyMatches.setText(LanguageManager.get("settings.notify.matches"));

        updateThemeStatus();
    }

    @FXML
    private void setLightTheme() {
        ThemeManager.setCurrentTheme(LIGHT_CSS);
        updateThemeStatus();
    }

    @FXML
    private void setDarkTheme() {
        ThemeManager.setCurrentTheme(DARK_CSS);
        updateThemeStatus();
    }

    @FXML
    private void setLangUk() {
        LanguageManager.setLanguage("uk");
    }

    @FXML
    private void setLangEn() {
        LanguageManager.setLanguage("en");
    }

    private void updateThemeStatus() {
        if (themeStatus == null) return;
        if (ThemeManager.getCurrentTheme().contains("dark")) {
            themeStatus.setText(LanguageManager.get("settings.theme.active.dark"));
        } else {
            themeStatus.setText(LanguageManager.get("settings.theme.active.light"));
        }
    }

    @FXML
    private void savePreferences() {
        Long userId = sessionManager.getCurrentUserId();
        if (userId == null) return;

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

        if (settingsStatus != null) {
            settingsStatus.setText(LanguageManager.get("settings.save.prefs") + " ✅");
            settingsStatus.getStyleClass().add("status-success");
        }
    }

    @FXML
    private void savePrivacy() {}

    @FXML
    private void deleteAccount() {
        if (settingsStatus != null) {
            settingsStatus.setText("❌ Функція у розробці");
            settingsStatus.getStyleClass().add("status-error");
        }
    }

    private int parseIntOrDefault(String text, int def) {
        try {
            return Integer.parseInt(text.trim());
        } catch (NumberFormatException e) {
            return def;
        }
    }
}