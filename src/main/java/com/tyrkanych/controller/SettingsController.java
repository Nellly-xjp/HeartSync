package com.tyrkanych.controller;

import com.tyrkanych.entity.Preferences;
import com.tyrkanych.service.PreferencesService;
import com.tyrkanych.session.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SettingsController {

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
                prefMinAge.setText(p.getMinAge() != null
                        ? String.valueOf(p.getMinAge()) : "18");
                prefMaxAge.setText(p.getMaxAge() != null
                        ? String.valueOf(p.getMaxAge()) : "60");
                prefCity.setText(p.getCity() != null ? p.getCity() : "");
            });
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
    private void savePrivacy() { /* TODO */ }

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