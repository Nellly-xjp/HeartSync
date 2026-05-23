package com.tyrkanych.controller;

import com.tyrkanych.config.ThemeManager;
import com.tyrkanych.session.SessionManager;
import com.tyrkanych.viewmodel.UserViewModel;
import java.io.File;
import java.io.IOException;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MainController {

    private final SessionManager sessionManager;

    @FXML private StackPane contentArea;
    @FXML private Button btnDiscover;
    @FXML private Button btnMatches;
    @FXML private Button btnMessages;
    @FXML private Button btnProfile;
    @FXML private Button btnSettings;
    @FXML private Label sidebarName;
    @FXML private Label sidebarCity;
    @FXML private Label avatarInitial;
    @FXML private ImageView sidebarPhoto;

    private Button activeButton;

    @Autowired
    public MainController(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }


    @FXML
    public void initialize() {
        UserViewModel vm = sessionManager.getViewModel();
        sidebarName.textProperty().bind(vm.nameProperty());
        sidebarCity.textProperty().bind(vm.cityProperty());
        avatarInitial.textProperty().bind(vm.initialProperty());

        loadSidebarPhoto(vm.getPhotoPath());
        vm.photoPathProperty().addListener((obs, oldVal, newVal) -> {
            loadSidebarPhoto(newVal);
        });

        activeButton = btnDiscover;
        showDiscover();

        Platform.runLater(() -> {
            Scene scene = contentArea.getScene();
            System.out.println("Scene: " + scene); // ← додай
            System.out.println("Scenes count: " + scene); // ← додай
            if (scene != null) {
                ThemeManager.setMainScene(scene);
            }
        });
    }

    private void loadSidebarPhoto(String photoPath) {
        if (photoPath != null && !photoPath.isEmpty()) {
            File file = new File(photoPath);
            if (file.exists()) {
                Image image = new Image(file.toURI().toString());
                sidebarPhoto.setImage(image);
                sidebarPhoto.setVisible(true);
                Circle clip = new Circle(22, 22, 22);
                sidebarPhoto.setClip(clip);
                avatarInitial.setVisible(false);
                return;
            }
        }
        if (sidebarPhoto != null) {
            sidebarPhoto.setVisible(false);
        }
        avatarInitial.setVisible(true);
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

            // Застосовуємо тему ПІСЛЯ того як content доданий
            Platform.runLater(() -> {
                Scene scene = contentArea.getScene();
                if (scene != null) {
                    ThemeManager.setMainScene(scene); // ← замість дублюючого коду
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogout() throws IOException {
        sessionManager.logout();
        Stage stage = (Stage) contentArea.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/fxml/login.fxml"));
        loader.setControllerFactory(SpringFxmlContext::getBean);
        Parent root = loader.load();
        Scene loginScene = new Scene(root, 900, 650);
        ThemeManager.setMainScene(loginScene); // ← додай цей рядок
        stage.setScene(loginScene);
        stage.setTitle("HeartSync — Вхід");
    }
    @FXML
    private void handleExit() {
        Platform.exit();
        System.exit(0);
    }
}