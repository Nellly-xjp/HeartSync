package com.tyrkanych.controller;

import com.tyrkanych.session.SessionManager;
import com.tyrkanych.viewmodel.UserViewModel;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MainController {

    private final SessionManager sessionManager;
    @FXML
    private StackPane contentArea;
    @FXML
    private Button btnDiscover;
    @FXML
    private Button btnMatches;
    @FXML
    private Button btnMessages;
    @FXML
    private Button btnProfile;
    @FXML
    private Button btnSettings;
    @FXML
    private Label sidebarName;
    @FXML
    private Label sidebarCity;
    @FXML
    private Label avatarInitial;
    private Button activeButton;

    @Autowired
    public MainController(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @FXML
    public void initialize() {
        // MVVM binding — сайдбар автоматично оновлюється через ViewModel
        UserViewModel vm = sessionManager.getViewModel();
        sidebarName.textProperty().bind(vm.nameProperty());
        sidebarCity.textProperty().bind(vm.cityProperty());
        avatarInitial.textProperty().bind(vm.initialProperty());

        activeButton = btnDiscover;
        showDiscover();
    }

    @FXML
    private void showDiscover() {
        navigate("/fxml/discover.fxml", btnDiscover);
    }

    @FXML
    private void showMatches() {
        navigate("/fxml/matches.fxml", btnMatches);
    }

    @FXML
    private void showMessages() {
        navigate("/fxml/messages.fxml", btnMessages);
    }

    @FXML
    private void showProfile() {
        navigate("/fxml/profile.fxml", btnProfile);
    }

    @FXML
    private void showSettings() {
        navigate("/fxml/settings.fxml", btnSettings);
    }

    private void navigate(String fxmlPath, Button clicked) {
        try {
            if (activeButton != null) {
                activeButton.getStyleClass().remove("nav-item-active");
            }
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            loader.setControllerFactory(SpringFxmlContext::getBean);
            Node content = loader.load();
            contentArea.getChildren().setAll(content);
            clicked.getStyleClass().add("nav-item-active");
            activeButton = clicked;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogout() throws IOException {
        sessionManager.logout();
        Stage stage = (Stage) contentArea.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/login.fxml"));
        stage.setScene(new Scene(root, 900, 650));
        stage.setTitle("HeartSync — Вхід");
    }
}