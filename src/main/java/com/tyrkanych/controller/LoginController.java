package com.tyrkanych.controller;

import com.tyrkanych.dto.UserDto;
import com.tyrkanych.service.UserService;
import com.tyrkanych.session.SessionManager;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LoginController {

    private final UserService userService;
    private final SessionManager sessionManager;
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label statusLabel;

    @Autowired
    public LoginController(UserService userService,
            SessionManager sessionManager) {
        this.userService = userService;
        this.sessionManager = sessionManager;
    }

    @FXML
    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            setStatus("❌ Заповніть всі поля", "status-error");
            return;
        }

        try {
            UserDto user = userService.findByEmail(email).orElse(null);
            if (user == null) {
                setStatus("❌ Користувача не знайдено", "status-error");
                return;
            }
            // TODO: додати перевірку хешу пароля BCrypt
            sessionManager.login(user);
            openMainWindow();
        } catch (Exception e) {
            setStatus("❌ Помилка входу: " + e.getMessage(), "status-error");
            e.printStackTrace();
        }
    }

    @FXML
    private void goToRegistration() throws IOException {
        Stage stage = (Stage) emailField.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/registration.fxml"));
        stage.setScene(new Scene(root, 900, 700));
        stage.setTitle("HeartSync — Реєстрація");
    }

    private void openMainWindow() throws IOException {
        Stage stage = (Stage) emailField.getScene().getWindow();
        FXMLLoader loader =
                new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
        // Передаємо Spring factory щоб контролери отримали DI
        loader.setControllerFactory(
                clazz -> SpringFxmlContext.getBean(clazz));
        Parent root = loader.load();
        stage.setScene(new Scene(root, 1100, 700));
        stage.setTitle("HeartSync");
    }

    private void setStatus(String message, String styleClass) {
        statusLabel.setText(message);
        statusLabel.getStyleClass()
                .removeAll("status-success", "status-error", "status-info");
        statusLabel.getStyleClass().add(styleClass);
    }
}