package com.tyrkanych.controller;

import com.tyrkanych.dto.UserDto;
import com.tyrkanych.dto.UserRegistrationDto;
import com.tyrkanych.service.UserService;
import com.tyrkanych.session.SessionManager;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RegistrationController {

    private final UserService userService;
    private final SessionManager sessionManager;
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField nameField;
    @FXML
    private ComboBox<String> genderComboBox;
    @FXML
    private DatePicker birthDatePicker;
    @FXML
    private TextField cityField;
    @FXML
    private TextArea bioArea;
    @FXML
    private Label statusLabel;

    @Autowired
    public RegistrationController(UserService userService,
            SessionManager sessionManager) {
        this.userService = userService;
        this.sessionManager = sessionManager;
    }

    @FXML
    public void initialize() {
        genderComboBox.getItems().addAll("male", "female", "other");
    }

    @FXML
    private void handleRegister() {
        try {
            if (emailField.getText().trim().isEmpty()
                    || passwordField.getText().isEmpty()
                    || nameField.getText().trim().isEmpty()
                    || birthDatePicker.getValue() == null
                    || genderComboBox.getValue() == null) {
                setStatus("❌ Заповніть всі обов'язкові поля", "status-error");
                return;
            }

            UserRegistrationDto dto = new UserRegistrationDto();
            dto.setEmail(emailField.getText().trim());
            dto.setPassword(passwordField.getText());
            dto.setName(nameField.getText().trim());
            dto.setGender(genderComboBox.getValue());
            dto.setBirthDate(birthDatePicker.getValue());
            dto.setCity(cityField.getText().trim());
            dto.setBio(bioArea.getText().trim());

            UserDto created = userService.register(dto);
            sessionManager.login(created);
            setStatus("✅ Вітаємо, " + created.getName() + "!", "status-success");

            Stage stage = (Stage) emailField.getScene().getWindow();
            FXMLLoader loader =
                    new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
            loader.setControllerFactory(SpringFxmlContext::getBean);
            Parent root = loader.load();
            stage.setScene(new Scene(root, 1100, 700));
            stage.setTitle("HeartSync");

        } catch (IllegalArgumentException e) {
            setStatus("❌ " + e.getMessage(), "status-error");
        } catch (IOException e) {
            setStatus("❌ Помилка завантаження", "status-error");
            e.printStackTrace();
        }
    }

    @FXML
    private void goToLogin() throws IOException {
        Stage stage = (Stage) emailField.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/login.fxml"));
        stage.setScene(new Scene(root, 900, 650));
        stage.setTitle("HeartSync — Вхід");
    }

    private void setStatus(String message, String styleClass) {
        statusLabel.setText(message);
        statusLabel.getStyleClass()
                .removeAll("status-success", "status-error", "status-info");
        statusLabel.getStyleClass().add(styleClass);
    }
}