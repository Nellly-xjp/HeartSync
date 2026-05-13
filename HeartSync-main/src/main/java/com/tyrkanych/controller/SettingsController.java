package com.tyrkanych.controller;

import com.tyrkanych.dao.impl.UserDaoImpl;
import com.tyrkanych.entity.Preferences;
import com.tyrkanych.service.PreferencesService;
import com.tyrkanych.session.SessionManager;
import java.io.IOException;
import java.net.URL;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
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

    private final PreferencesService preferencesService;
    private final SessionManager sessionManager;
    private final UserDaoImpl userDao;

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

    @Autowired
    public SettingsController(PreferencesService preferencesService,
            SessionManager sessionManager,
            UserDaoImpl userDao) {
        this.preferencesService = preferencesService;
        this.sessionManager = sessionManager;
        this.userDao = userDao;
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
    private void setLightTheme() {
        applyTheme("/styles/styles.css", "☀️ Активна світла тема");
    }

    @FXML
    private void setDarkTheme() {
        applyTheme("/styles/styles-dark.css", "🌙 Активна темна тема");
    }

    private void applyTheme(String cssPath, String statusText) {
        try {
            Stage stage = (Stage) prefGender.getScene().getWindow();
            Scene scene = stage.getScene();

            URL cssUrl = getClass().getResource(cssPath);
            if (cssUrl == null) {
                themeStatus.setText("❌ Файл не знайдено: " + cssPath);
                themeStatus.getStyleClass().add("status-error");
                return;
            }

            scene.getStylesheets().clear();
            scene.getStylesheets().add(cssUrl.toExternalForm());
            themeStatus.setText(statusText);

        } catch (Exception e) {
            themeStatus.setText("❌ Помилка: " + e.getMessage());
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

        setStatus("✅ Уподобання збережено!", "status-success");
    }

    @FXML
    private void savePrivacy() {
        setStatus("✅ Збережено!", "status-success");
    }

    @FXML
    private void deleteAccount() {
        Long userId = sessionManager.getCurrentUserId();
        if (userId == null) return;

        try {
            userDao.deleteById(userId);
            sessionManager.logout();

            Stage stage = (Stage) prefGender.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/login.fxml"));
            loader.setControllerFactory(SpringFxmlContext::getBean);
            Parent root = loader.load();
            stage.setScene(new Scene(root, 900, 650));
            stage.setTitle("HeartSync — Вхід");

        } catch (IOException e) {
            setStatus("❌ Помилка: " + e.getMessage(), "status-error");
        }
    }

    private void setStatus(String message, String styleClass) {
        settingsStatus.setText(message);
        settingsStatus.getStyleClass()
                .removeAll("status-success", "status-error", "status-info");
        settingsStatus.getStyleClass().add(styleClass);
    }

    private int parseIntOrDefault(String text, int def) {
        try {
            return Integer.parseInt(text.trim());
        } catch (NumberFormatException e) {
            return def;
        }
    }
}