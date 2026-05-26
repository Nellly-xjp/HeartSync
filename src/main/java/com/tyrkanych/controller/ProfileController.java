package com.tyrkanych.controller;

import com.tyrkanych.config.LanguageManager;
import com.tyrkanych.dao.impl.UserDaoImpl;
import com.tyrkanych.entity.Match;
import com.tyrkanych.entity.Message;
import com.tyrkanych.entity.User;
import com.tyrkanych.service.ExportService;
import com.tyrkanych.service.MatchService;
import com.tyrkanych.service.MessageService;
import com.tyrkanych.session.SessionManager;
import com.tyrkanych.viewmodel.UserViewModel;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javafx.scene.control.Button;

@Component
public class ProfileController {

    private final SessionManager sessionManager;
    private final UserDaoImpl userDao;
    private final ExportService exportService;
    private final MatchService matchService;
    private final MessageService messageService;

    @FXML private StackPane avatarPane;
    @FXML private ImageView profilePhoto;
    @FXML private Label profileInitial;
    @FXML private Label profileName;
    @FXML private Label profileCity;
    @FXML private Label profileAge;
    @FXML private Label profileGender;
    @FXML private Label profileEmail;
    @FXML private TextField editName;
    @FXML private TextField editCity;
    @FXML private TextArea editBio;
    @FXML private Label saveStatus;
    @FXML private Label statsLikes;
    @FXML private Label statsMatches;
    @FXML private Label statsMessages;
    @FXML private Label labelEditTitle;
    @FXML private Label labelNameField;
    @FXML private Label labelCityField;
    @FXML private Label labelBioField;
    @FXML private Label labelStatsSection;
    @FXML private Label labelLikes;
    @FXML private Label labelMatches;
    @FXML private Label labelChats;
    @FXML private Button btnSave;
    @FXML private Button btnReset;
    @FXML private Button btnPhoto;
    @FXML private Button btnPdf;
    @FXML private Button btnExcel;
    @Autowired
    public ProfileController(SessionManager sessionManager,
            UserDaoImpl userDao,
            ExportService exportService,
            MatchService matchService,
            MessageService messageService) {
        this.sessionManager = sessionManager;
        this.userDao = userDao;
        this.exportService = exportService;
        this.matchService = matchService;
        this.messageService = messageService;
    }

    @FXML
    public void initialize() {
        applyLanguage();
        LanguageManager.addListener(this::applyLanguage);
        UserViewModel vm = sessionManager.getViewModel();

        profileInitial.textProperty().bind(vm.initialProperty());
        profileName.textProperty().bind(vm.nameProperty());
        profileEmail.textProperty().bind(vm.emailProperty());

        editName.setText(vm.getName());
        editCity.setText(vm.getCity());
        editBio.setText(vm.getBio());

        profileCity.setText("📍 " + (vm.getCity().isEmpty() ? "—" : vm.getCity()));
        profileAge.setText(vm.getAge() > 0 ? vm.getAge() + " р." : "");
        profileGender.setText(vm.getGender());

        // Статистика
        Long myId = sessionManager.getCurrentUserId();
        if (myId != null) {
            int matchCount = matchService.findByUserId(myId).size();
            int messageCount = messageService.getRecentMessages(myId, 1000).size();
            statsLikes.setText("0");
            statsMatches.setText(String.valueOf(matchCount));
            statsMessages.setText(String.valueOf(messageCount));
        } else {
            statsLikes.setText("0");
            statsMatches.setText("0");
            statsMessages.setText("0");
        }

        loadPhoto(vm.getPhotoPath());
    }
    private void applyLanguage() {
        if (labelEditTitle != null) labelEditTitle.setText(LanguageManager.get("profile.title"));
        if (labelNameField != null) labelNameField.setText(LanguageManager.get("profile.name"));
        if (labelCityField != null) labelCityField.setText(LanguageManager.get("profile.city"));
        if (labelBioField != null) labelBioField.setText(LanguageManager.get("profile.bio"));
        if (labelStatsSection != null) labelStatsSection.setText(LanguageManager.get("profile.stats"));
        if (labelLikes != null) labelLikes.setText(LanguageManager.get("profile.likes"));
        if (labelMatches != null) labelMatches.setText(LanguageManager.get("profile.matches"));
        if (labelChats != null) labelChats.setText(LanguageManager.get("profile.chats"));
        if (btnSave != null) btnSave.setText("✓  " + LanguageManager.get("profile.save"));
        if (btnReset != null) btnReset.setText(LanguageManager.get("profile.reset"));
        if (btnPhoto != null) btnPhoto.setText("📷  " + LanguageManager.get("profile.photo"));
        if (btnPdf != null) btnPdf.setText(LanguageManager.get("profile.pdf"));
        if (btnExcel != null) btnExcel.setText(LanguageManager.get("profile.excel"));
    }

    private void loadPhoto(String photoPath) {
        if (photoPath != null && !photoPath.isEmpty()) {
            File file = new File(photoPath);
            if (file.exists()) {
                Image image = new Image(file.toURI().toString());
                profilePhoto.setImage(image);
                profilePhoto.setFitWidth(120);
                profilePhoto.setFitHeight(120);
                profilePhoto.setPreserveRatio(false);
                Circle clip = new Circle(60, 60, 60);
                profilePhoto.setClip(clip);
                profilePhoto.setVisible(true);
                profileInitial.setVisible(false);
            }
        }
    }

    @FXML
    private void handleChoosePhoto() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Оберіть фото профілю");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Зображення", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        Stage stage = (Stage) avatarPane.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);
        if (selectedFile == null) return;

        Task<String> uploadTask = new Task<>() {
            @Override
            protected String call() throws Exception {
                Path photosDir = Paths.get("data/photos");
                Files.createDirectories(photosDir);
                String fileName = sessionManager.getCurrentUserId()
                        + "_" + System.currentTimeMillis()
                        + getExtension(selectedFile.getName());
                Path destination = photosDir.resolve(fileName);
                Files.copy(selectedFile.toPath(), destination,
                        StandardCopyOption.REPLACE_EXISTING);
                return destination.toAbsolutePath().toString();
            }
        };

        uploadTask.setOnSucceeded(e -> {
            String newPath = uploadTask.getValue();
            userDao.updatePhoto(sessionManager.getCurrentUserId(), newPath);
            sessionManager.getViewModel().setPhotoPath(newPath);
            Image image = new Image(new File(newPath).toURI().toString());
            profilePhoto.setImage(image);
            profilePhoto.setFitWidth(120);
            profilePhoto.setFitHeight(120);
            profilePhoto.setPreserveRatio(false);
            Circle clip = new Circle(60, 60, 60);
            profilePhoto.setClip(clip);
            profilePhoto.setVisible(true);
            profileInitial.setVisible(false);
            saveStatus.setText("✅ Фото оновлено!");
            saveStatus.getStyleClass().removeAll("status-error", "status-info");
            saveStatus.getStyleClass().add("status-success");
        });

        uploadTask.setOnFailed(e -> {
            saveStatus.setText("❌ Помилка: " + uploadTask.getException().getMessage());
            saveStatus.getStyleClass().removeAll("status-success", "status-info");
            saveStatus.getStyleClass().add("status-error");
        });

        new Thread(uploadTask).start();
    }

    private String getExtension(String fileName) {
        int dot = fileName.lastIndexOf('.');
        return dot >= 0 ? fileName.substring(dot) : ".jpg";
    }

    @FXML
    private void saveProfile() {
        String name = editName.getText().trim();
        String city = editCity.getText().trim();
        String bio = editBio.getText().trim();

        if (name.isEmpty()) {
            saveStatus.setText("❌ Ім'я не може бути порожнім");
            saveStatus.getStyleClass().removeAll("status-success", "status-info");
            saveStatus.getStyleClass().add("status-error");
            return;
        }

        Task<Void> saveTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                userDao.updateProfile(sessionManager.getCurrentUserId(), name, city, bio);
                return null;
            }
        };

        saveTask.setOnSucceeded(e -> {
            UserViewModel vm = sessionManager.getViewModel();
            vm.setName(name);
            vm.setCity(city);
            vm.setBio(bio);
            profileCity.setText("📍 " + (city.isEmpty() ? "—" : city));
            saveStatus.setText("✅ Збережено!");
            saveStatus.getStyleClass().removeAll("status-error", "status-info");
            saveStatus.getStyleClass().add("status-success");
        });

        saveTask.setOnFailed(e -> {
            saveStatus.setText("❌ Помилка збереження");
            saveStatus.getStyleClass().removeAll("status-success", "status-info");
            saveStatus.getStyleClass().add("status-error");
        });

        new Thread(saveTask).start();
    }

    @FXML
    private void resetProfile() {
        UserViewModel vm = sessionManager.getViewModel();
        editName.setText(vm.getName());
        editCity.setText(vm.getCity());
        editBio.setText(vm.getBio());
        saveStatus.setText("");
    }

    @FXML
    private void exportToPdf() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Зберегти PDF звіт");
        fileChooser.setInitialFileName("heartsync_report.pdf");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("PDF файли", "*.pdf"));

        Stage stage = (Stage) editName.getScene().getWindow();
        File file = fileChooser.showSaveDialog(stage);
        if (file == null) return;

        Task<File> task = new Task<>() {
            @Override
            protected File call() throws Exception {
                Long myId = sessionManager.getCurrentUserId();
                User user = userDao.findById(myId).orElseThrow();
                List<Match> matches = matchService.findByUserId(myId);
                List<Message> messages = messageService.getRecentMessages(myId, 100);
                return exportService.exportToPdf(user, matches, messages,
                        file.getAbsolutePath());
            }
        };

        task.setOnSucceeded(e -> {
            saveStatus.setText("✅ PDF збережено: " + file.getName());
            saveStatus.getStyleClass().removeAll("status-error", "status-info");
            saveStatus.getStyleClass().add("status-success");
        });

        task.setOnFailed(e -> {
            saveStatus.setText("❌ Помилка PDF: " + task.getException().getMessage());
            saveStatus.getStyleClass().removeAll("status-success", "status-info");
            saveStatus.getStyleClass().add("status-error");
        });

        new Thread(task).start();
    }

    @FXML
    private void exportToExcel() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Зберегти Excel звіт");
        fileChooser.setInitialFileName("heartsync_report.xlsx");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Excel файли", "*.xlsx"));

        Stage stage = (Stage) editName.getScene().getWindow();
        File file = fileChooser.showSaveDialog(stage);
        if (file == null) return;

        Task<File> task = new Task<>() {
            @Override
            protected File call() throws Exception {
                Long myId = sessionManager.getCurrentUserId();
                User user = userDao.findById(myId).orElseThrow();
                List<Match> matches = matchService.findByUserId(myId);
                List<Message> messages = messageService.getRecentMessages(myId, 100);
                return exportService.exportToExcel(user, matches, messages,
                        file.getAbsolutePath());
            }
        };

        task.setOnSucceeded(e -> {
            saveStatus.setText("✅ Excel збережено: " + file.getName());
            saveStatus.getStyleClass().removeAll("status-error", "status-info");
            saveStatus.getStyleClass().add("status-success");
        });

        task.setOnFailed(e -> {
            saveStatus.setText("❌ Помилка Excel: " + task.getException().getMessage());
            saveStatus.getStyleClass().removeAll("status-success", "status-info");
            saveStatus.getStyleClass().add("status-error");
        });

        new Thread(task).start();
    }
}