package com.tyrkanych.controller;

import com.tyrkanych.config.LanguageManager;
import com.tyrkanych.config.ThemeManager;
import com.tyrkanych.dao.impl.UserDaoImpl;
import com.tyrkanych.dto.UserDto;
import com.tyrkanych.dto.UserRegistrationDto;
import com.tyrkanych.service.UserService;
import com.tyrkanych.service.VerificationService;
import com.tyrkanych.session.SessionManager;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RegistrationController {

    private final UserService userService;
    private final VerificationService verificationService;
    private final SessionManager sessionManager;
    private final List<String> selectedInterests = new ArrayList<>();

    @FXML private StackPane step1Pane;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label step1Status;

    @FXML private StackPane step2Pane;
    @FXML private TextField codeField;
    @FXML private Label step2Status;
    @FXML private Label codeEmailLabel;

    @FXML private StackPane step3Pane;
    @FXML private TextField nameField;
    @FXML private ComboBox<String> genderComboBox;
    @FXML private DatePicker birthDatePicker;
    @FXML private TextField cityField;
    @FXML private TextArea bioArea;
    @FXML private FlowPane interestsPane;
    @FXML private Label step3Status;

    // Labels для перекладу — крок 1
    @FXML private Button btnSendCode;
    @FXML private Label labelStep1Title;
    @FXML private Label labelStep1Sub;
    @FXML private Label labelHaveAccount;
    @FXML private Label labelGoLogin;
    @FXML private Label labelEmailField;
    @FXML private Label labelPasswordField;
    @FXML private Label labelConfirmField;

    // Labels для перекладу — крок 2
    @FXML private Button btnVerifyCode;
    @FXML private Label labelStep2Sub;
    @FXML private Label labelStep2Title;
    @FXML private Label labelResendCode;
    @FXML private Label labelCodeField;

    // Labels для перекладу — крок 3
    @FXML private Button btnRegister;
    @FXML private Label labelStep3Title;
    @FXML private Label labelStep3Sub;
    @FXML private Label labelGenderField;
    @FXML private Label labelBirthField;
    @FXML private Label labelNameField;
    @FXML private Label labelCityField;
    @FXML private Label labelBioField;
    @FXML private Label labelInterestsField;

    private String verifiedEmail;
    private String verifiedPassword;
    private final UserDaoImpl userDao;
    @Autowired
    public RegistrationController(UserService userService,
            VerificationService verificationService,
            SessionManager sessionManager,
            UserDaoImpl userDao) { // ← додай параметр
        this.userService = userService;
        this.verificationService = verificationService;
        this.sessionManager = sessionManager;
        this.userDao = userDao; // ← додай
    }

    @FXML
    public void initialize() {
        applyLanguage();
        LanguageManager.addListener(this::applyLanguage);
        genderComboBox.getItems().addAll("male", "female", "other");
        showStep(1);
        loadInterests();
    }

    private void applyLanguage() {
        // Крок 1
        if (btnSendCode != null) btnSendCode.setText(LanguageManager.get("register.btn"));
        if (labelStep1Title != null) labelStep1Title.setText(LanguageManager.get("register.title"));
        if (labelStep1Sub != null) labelStep1Sub.setText(LanguageManager.get("register.step1"));
        if (labelHaveAccount != null) labelHaveAccount.setText(LanguageManager.get("register.have.account"));
        if (labelGoLogin != null) labelGoLogin.setText(LanguageManager.get("register.login"));
        if (labelEmailField != null) labelEmailField.setText(LanguageManager.get("register.email"));
        if (labelPasswordField != null) labelPasswordField.setText(LanguageManager.get("register.password"));
        if (labelConfirmField != null) labelConfirmField.setText(LanguageManager.get("register.confirm"));

        // Крок 2
        if (btnVerifyCode != null) btnVerifyCode.setText(LanguageManager.get("register.verify"));
        if (labelStep2Title != null) labelStep2Title.setText(LanguageManager.get("register.check.mail"));
        if (labelStep2Sub != null) labelStep2Sub.setText(LanguageManager.get("register.step2"));
        if (labelResendCode != null) labelResendCode.setText(LanguageManager.get("register.resend"));
        if (labelCodeField != null) labelCodeField.setText(LanguageManager.get("register.code"));

        // Крок 3
        if (btnRegister != null) btnRegister.setText(LanguageManager.get("register.create"));
        if (labelStep3Title != null) labelStep3Title.setText(LanguageManager.get("register.about.title"));
        if (labelStep3Sub != null) labelStep3Sub.setText(LanguageManager.get("register.step3"));
        if (labelGenderField != null) labelGenderField.setText(LanguageManager.get("register.gender"));
        if (labelBirthField != null) labelBirthField.setText(LanguageManager.get("register.birth"));
        if (labelNameField != null) labelNameField.setText(LanguageManager.get("register.name"));
        if (labelCityField != null) labelCityField.setText(LanguageManager.get("register.city"));
        if (labelBioField != null) labelBioField.setText(LanguageManager.get("register.bio"));
        if (labelInterestsField != null) labelInterestsField.setText(LanguageManager.get("register.interests"));
    }

    @FXML
    private void handleSendCode() {
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        String confirm = confirmPasswordField.getText();

        if (email.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
            setStatus(step1Status, "❌ Заповніть всі поля", "status-error");
            return;
        }
        if (!email.matches("^[\\w.+\\-]+@[\\w\\-]+\\.[a-zA-Z]{2,}$")) {
            setStatus(step1Status, "❌ Невірний формат email", "status-error");
            return;
        }
        if (password.length() < 8) {
            setStatus(step1Status, "❌ Пароль мінімум 8 символів", "status-error");
            return;
        }
        if (!password.matches(".*[A-Za-z].*") || !password.matches(".*[0-9].*")) {
            setStatus(step1Status, "❌ Пароль має містити літери і цифри", "status-error");
            return;
        }
        if (!password.equals(confirm)) {
            setStatus(step1Status, "❌ Паролі не співпадають", "status-error");
            return;
        }
        if (userService.existsByEmail(email)) {
            setStatus(step1Status, "❌ Цей email вже зареєстрований", "status-error");
            return;
        }

        setStatus(step1Status, "⏳ Надсилаємо код...", "status-info");

        Task<Void> sendTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                verificationService.generateAndSendCode(email);
                return null;
            }
        };

        sendTask.setOnSucceeded(e -> {
            verifiedEmail = email;
            verifiedPassword = password;
            codeEmailLabel.setText("Код надіслано на " + email);
            showStep(2);
        });

        sendTask.setOnFailed(e -> {
            Throwable ex = sendTask.getException();
            setStatus(step1Status, "❌ Помилка: " + ex.getMessage(), "status-error");
        });

        new Thread(sendTask).start();
    }

    @FXML
    private void handleVerifyCode() {
        String code = codeField.getText().trim();
        if (code.isEmpty()) {
            setStatus(step2Status, "❌ Введіть код", "status-error");
            return;
        }
        if (code.length() != 6) {
            setStatus(step2Status, "❌ Код має бути 6 цифр", "status-error");
            return;
        }
        if (verificationService.verifyCode(verifiedEmail, code)) {
            verificationService.clearCode(verifiedEmail);
            setStatus(step2Status, "✅ Email підтверджено!", "status-success");
            showStep(3);
        } else {
            setStatus(step2Status, "❌ Невірний або прострочений код", "status-error");
        }
    }

    @FXML
    private void handleResendCode() {
        setStatus(step2Status, "⏳ Надсилаємо...", "status-info");
        Task<Void> resendTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                verificationService.generateAndSendCode(verifiedEmail);
                return null;
            }
        };
        resendTask.setOnSucceeded(e ->
                setStatus(step2Status, "✅ Новий код надіслано!", "status-success"));
        resendTask.setOnFailed(e ->
                setStatus(step2Status, "❌ " + resendTask.getException().getMessage(), "status-error"));
        new Thread(resendTask).start();
    }

    @FXML
    private void handleRegister() {
        String name = nameField.getText().trim();
        String city = cityField.getText().trim();

        if (name.isEmpty() || name.length() < 2) {
            setStatus(step3Status, "❌ Введіть ім'я (мінімум 2 символи)", "status-error");
            return;
        }
        if (genderComboBox.getValue() == null) {
            setStatus(step3Status, "❌ Оберіть стать", "status-error");
            return;
        }
        if (birthDatePicker.getValue() == null) {
            setStatus(step3Status, "❌ Вкажіть дату народження", "status-error");
            return;
        }
        int age = Period.between(birthDatePicker.getValue(), LocalDate.now()).getYears();
        if (age < 18) {
            setStatus(step3Status, "❌ Вам має бути мінімум 18 років", "status-error");
            return;
        }
        if (city.isEmpty()) {
            setStatus(step3Status, "❌ Вкажіть місто", "status-error");
            return;
        }
        if (selectedInterests.isEmpty()) {
            setStatus(step3Status, "❌ Оберіть хоча б один інтерес", "status-error");
            return;
        }

        try {
            UserRegistrationDto dto = new UserRegistrationDto();
            dto.setEmail(verifiedEmail);
            dto.setPassword(verifiedPassword);
            dto.setName(name);
            dto.setGender(genderComboBox.getValue());
            dto.setBirthDate(birthDatePicker.getValue());
            dto.setCity(city);
            dto.setBio(bioArea.getText().trim());
            dto.setInterests(selectedInterests); // ← додано

            UserDto created = userService.register(dto);

            // Зберігаємо інтереси в БД
            String interestsStr = String.join(", ", selectedInterests);
            userDao.updateInterests(created.getId(), interestsStr); // ← додано

            sessionManager.login(created);

            Stage stage = (Stage) nameField.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
            loader.setControllerFactory(SpringFxmlContext::getBean);
            Parent root = loader.load();
            Scene scene = new Scene(root, 1100, 700);
            ThemeManager.registerScene(scene);
            stage.setScene(scene);
            stage.setTitle("HeartSync ♥");
        } catch (Exception e) {
            setStatus(step3Status, "❌ " + e.getMessage(), "status-error");
        }
    }

    private void loadInterests() {
        List<String> interests = List.of(
                "🎵 Музика", "📚 Читання", "🏃 Спорт", "🎮 Ігри",
                "✈️ Подорожі", "🍕 Кулінарія", "🎨 Мистецтво", "🐾 Тварини",
                "💻 Технології", "🎬 Кіно", "🌿 Природа", "📷 Фото",
                "🧘 Йога", "🎭 Театр", "🏋️ Фітнес", "🎸 Концерти"
        );
        for (String interest : interests) {
            Button btn = new Button(interest);
            btn.getStyleClass().add("interest-tag");
            btn.setOnAction(e -> toggleInterest(btn, interest));
            interestsPane.getChildren().add(btn);
        }
    }

    private void toggleInterest(Button btn, String interest) {
        if (selectedInterests.contains(interest)) {
            selectedInterests.remove(interest);
            btn.getStyleClass().remove("interest-tag-active");
        } else {
            if (selectedInterests.size() >= 5) {
                setStatus(step3Status, "⚠️ Максимум 5 інтересів", "status-info");
                return;
            }
            selectedInterests.add(interest);
            btn.getStyleClass().add("interest-tag-active");
        }
        step3Status.setText("");
    }

    private void showStep(int step) {
        step1Pane.setVisible(step == 1); step1Pane.setManaged(step == 1);
        step2Pane.setVisible(step == 2); step2Pane.setManaged(step == 2);
        step3Pane.setVisible(step == 3); step3Pane.setManaged(step == 3);
    }

    @FXML
    private void goToLogin() throws IOException {
        Stage stage = (Stage) emailField.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
        loader.setControllerFactory(SpringFxmlContext::getBean);
        Parent root = loader.load();
        Scene scene = new Scene(root, 900, 650);
        ThemeManager.registerScene(scene);
        stage.setScene(scene);
        stage.setTitle("HeartSync — Вхід");
    }

    private void setStatus(Label label, String message, String styleClass) {
        label.setText(message);
        label.getStyleClass().removeAll("status-success", "status-error", "status-info");
        label.getStyleClass().add(styleClass);
    }
}