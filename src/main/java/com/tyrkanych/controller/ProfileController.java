package com.tyrkanych.controller;

import com.tyrkanych.session.SessionManager;
import com.tyrkanych.viewmodel.UserViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProfileController {

    private final SessionManager sessionManager;
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
    public ProfileController(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
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
        // TODO: зберегти через UserService
        saveStatus.setText("✅ Збережено");
        saveStatus.getStyleClass().add("status-success");
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