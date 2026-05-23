package com.tyrkanych.controller;

import com.tyrkanych.config.LanguageManager;
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

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label statusLabel;
    @FXML private Label labelTitle;
    @FXML private Label labelSubtitle;
    @FXML private Label labelEmail;
    @FXML private Label labelPassword;
    @FXML private javafx.scene.control.Button btnLogin;
    @FXML private Label labelNoAccount;
    @FXML private Label labelRegister;

    @Autowired
    public LoginController(UserService userService, SessionManager sessionManager) {
        this.userService = userService;
        this.sessionManager = sessionManager;
    }

    @FXML
    public void initialize() {
        applyLanguage();
        LanguageManager.addListener(this::applyLanguage);
    }

    private void applyLanguage() {
        if (labelTitle != null)
            labelTitle.setText(LanguageManager.get("login.title"));
        if (labelSubtitle != null)
            labelSubtitle.setText(LanguageManager.get("login.subtitle"));
        if (labelEmail != null)
            labelEmail.setText(LanguageManager.get("login.email"));
        if (labelPassword != null)
            labelPassword.setText(LanguageManager.get("login.password"));
        if (btnLogin != null)
            btnLogin.setText(LanguageManager.get("login.btn"));
        if (labelNoAccount != null)
            labelNoAccount.setText(LanguageManager.get("login.no.account"));
        if (labelRegister != null)
            labelRegister.setText(LanguageManager.get("login.register"));
    }

    @FXML
    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            setStatus("❌ " + LanguageManager.get("login.email") + " / "
                    + LanguageManager.get("login.password"), "status-error");
            return;
        }
        if (!email.matches("^[\\w.+\\-]+@[\\w\\-]+\\.[a-zA-Z]{2,}$")) {
            setStatus("❌ Невірний формат email", "status-error");
            return;
        }
        try {
            UserDto user = userService.findByEmailAndPassword(email, password).orElse(null);
            if (user == null) {
                setStatus("❌ Невірний email або пароль", "status-error");
                return;
            }
            sessionManager.login(user);
            openMainWindow();
        } catch (Exception e) {
            setStatus("❌ Помилка входу: " + e.getMessage(), "status-error");
        }
    }

    @FXML
    private void goToRegistration() throws IOException {
        Stage stage = (Stage) emailField.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/fxml/registration.fxml"));
        loader.setControllerFactory(SpringFxmlContext::getBean);
        Parent root = loader.load();
        stage.setScene(new Scene(root, 900, 700));
        stage.setTitle("HeartSync — " + LanguageManager.get("register.title"));
    }

    private void openMainWindow() throws IOException {
        Stage stage = (Stage) emailField.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/fxml/main.fxml"));
        loader.setControllerFactory(SpringFxmlContext::getBean);
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