package com.tyrkanych;

import com.tyrkanych.config.AppConfig;
import com.tyrkanych.config.ThemeManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class HeartSyncApp extends Application {

    private ApplicationContext springContext;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void init() {
        springContext = new AnnotationConfigApplicationContext(AppConfig.class);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Додай іконку
        primaryStage.getIcons().add(
                new javafx.scene.image.Image(
                        getClass().getResourceAsStream("/images/icon.png")));

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
        loader.setControllerFactory(springContext::getBean);
        Parent root = loader.load();

        Scene scene = new Scene(root, 900, 650);
        String css = getClass().getResource(ThemeManager.getCurrentTheme()).toExternalForm();
        scene.getStylesheets().add(css);

        primaryStage.setTitle("HeartSync");
        primaryStage.setScene(scene);
        primaryStage.setResizable(true);
        primaryStage.show();
    }
}