package com.tyrkanych.controller;

import com.tyrkanych.dao.impl.UserDaoImpl;
import com.tyrkanych.session.SessionManager;
import com.tyrkanych.viewmodel.UserViewModel;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
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

@Component
public class ProfileController {

    private final SessionManager sessionManager;
    private final UserDaoImpl userDao;

    @FXML
    private StackPane avatarPane;
    @FXML
    private ImageView profilePhoto;
    @FXML
    private Label profileInitial;
    @FXML
    private Label profileName;
    @FXML
    private Label profileCity;
    @FXML
    private Label profileAge;
    @FXML
    private Label profileGender;
    @FXML
    private Label profileEmail;
    @FXML
    private TextField editName;
    @FXML
    private TextField editCity;
    @FXML
    private TextArea editBio;
    @FXML
    private Label saveStatus;
    @FXML
    private Label statsLikes;
    @FXML
    private Label statsMatches;
    @FXML
    private Label statsMessages;

    @Autowired
    public ProfileController(SessionManager sessionManager, UserDaoImpl userDao) {
        this.sessionManager = sessionManager;
        this.userDao = userDao;
    }

    @FXML
    public void initialize() {
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

        statsLikes.setText("0");
        statsMatches.setText("0");
        statsMessages.setText("0");

        // Завантажуємо фото якщо є
        loadPhoto(vm.getPhotoPath());
    }

    private void loadPhoto(String photoPath) {
        if (photoPath != null && !photoPath.isEmpty()) {
            File file = new File(photoPath);
            if (file.exists()) {
                Image image = new Image(file.toURI().toString());
                profilePhoto.setImage(image);
                profilePhoto.setVisible(true);
                profileInitial.setVisible(false);

                // Робимо фото круглим
                Circle clip = new Circle(60, 60, 60);
                profilePhoto.setClip(clip);
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

        if (selectedFile == null) {
            return;
        }

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

            // Зберігаємо в БД
            userDao.updatePhoto(sessionManager.getCurrentUserId(), newPath);

            // Оновлюємо ViewModel
            sessionManager.getViewModel().setPhotoPath(newPath);

            // Показуємо фото
            Image image = new Image(new File(newPath).toURI().toString());
            profilePhoto.setImage(image);
            profilePhoto.setVisible(true);
            profileInitial.setVisible(false);

            // Робимо фото круглим
            Circle clip = new Circle(60, 60, 60);
            profilePhoto.setClip(clip);

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
}