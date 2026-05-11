package com.tyrkanych.controller;

import com.tyrkanych.dao.impl.UserDaoImpl;
import com.tyrkanych.session.SessionManager;
import com.tyrkanych.viewmodel.UserViewModel;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class ProfileController {

    private final SessionManager sessionManager;
    private final UserDaoImpl userDao;
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

        // MVVM binding — автоматичне оновлення
        profileInitial.textProperty().bind(vm.initialProperty());
        profileName.textProperty().bind(vm.nameProperty());
        profileEmail.textProperty().bind(vm.emailProperty());

        // Заповнення полів редагування
        editName.setText(vm.getName());
        editCity.setText(vm.getCity());
        editBio.setText(vm.getBio());

        profileCity.setText("📍 " + (vm.getCity().isEmpty() ? "—" : vm.getCity()));
        profileAge.setText(vm.getAge() > 0 ? "· " + vm.getAge() + " р." : "");
        profileGender.setText(vm.getGender());

        statsLikes.setText("0");
        statsMatches.setText("0");
        statsMessages.setText("0");
    }

    @FXML
    private void saveProfile() {
        String name = editName.getText().trim();
        String city = editCity.getText().trim();
        String bio = editBio.getText().trim();

        if (name.isEmpty()) {
            saveStatus.setText("❌ Ім'я не може бути порожнім");
            saveStatus.getStyleClass().removeAll("status-success", "status-error");
            saveStatus.getStyleClass().add("status-error");
            return;
        }

        Task<Void> saveTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                Long userId = sessionManager.getCurrentUserId();
                userDao.updateProfile(userId, name, city, bio);
                return null;
            }
        };

        saveTask.setOnSucceeded(e -> {
            // Оновлюємо ViewModel
            UserViewModel vm = sessionManager.getViewModel();
            vm.setName(name);
            vm.setCity(city);
            vm.setBio(bio);

            saveStatus.setText("✅ Збережено!");
            saveStatus.getStyleClass().removeAll("status-success", "status-error");
            saveStatus.getStyleClass().add("status-success");
        });

        saveTask.setOnFailed(e -> {
            saveStatus.setText("❌ Помилка збереження");
            saveStatus.getStyleClass().removeAll("status-success", "status-error");
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