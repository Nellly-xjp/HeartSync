package com.tyrkanych.controller;

import com.tyrkanych.dao.impl.UserDaoImpl;
import com.tyrkanych.entity.Match;
import com.tyrkanych.entity.User;
import com.tyrkanych.service.MatchService;
import com.tyrkanych.service.ReportService;
import com.tyrkanych.session.SessionManager;
import java.io.IOException;
import java.util.List;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.TextField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.io.File;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
@Component
public class MatchesController {

    private final MatchService matchService;
    private final UserDaoImpl userDao;
    private final SessionManager sessionManager;
    private final ReportService reportService;

    @FXML private TextField searchField;
    @FXML private ListView<String> matchesList;
    @FXML private Label detailInitial;
    @FXML private Label detailName;
    @FXML private Label detailCity;
    @FXML private Label detailScore;
    @FXML private ProgressBar compatibilityBar;
    @FXML private Label detailBio;
    @FXML private ImageView detailPhoto;
    private List<Match> matches;
    private Match selectedMatch;
    private Long selectedPartnerId;

    @Autowired
    public MatchesController(MatchService matchService,
            UserDaoImpl userDao,
            SessionManager sessionManager,
            ReportService reportService) {
        this.matchService = matchService;
        this.userDao = userDao;
        this.sessionManager = sessionManager;
        this.reportService = reportService;
    }

    @FXML
    public void initialize() {
        loadMatches();

        matchesList.getSelectionModel().selectedIndexProperty()
                .addListener((obs, oldVal, newVal) -> {
                    int idx = newVal.intValue();
                    if (idx >= 0 && idx < matches.size()) {
                        selectedMatch = matches.get(idx);
                        showMatchDetail(selectedMatch);
                    }
                });

        searchField.textProperty().addListener((obs, old, val) -> filterMatches(val));
    }

    private void loadMatches() {
        Long myId = sessionManager.getCurrentUserId();
        if (myId == null) return;
        matches = matchService.findByUserId(myId);
        refreshList(matches);
    }

    private void refreshList(List<Match> list) {
        matchesList.getItems().clear();
        for (Match m : list) {
            Long partnerId = m.getUser1Id().equals(sessionManager.getCurrentUserId())
                    ? m.getUser2Id() : m.getUser1Id();
            User partner = userDao.findById(partnerId).orElse(null);
            String name = partner != null ? partner.getName() : "Невідомий";
            matchesList.getItems().add("💘 " + name);
        }
    }

    private void filterMatches(String query) {
        if (matches == null) return;
        if (query == null || query.isBlank()) {
            refreshList(matches);
            return;
        }
        List<Match> filtered = matches.stream()
                .filter(m -> {
                    Long pid = m.getUser1Id().equals(sessionManager.getCurrentUserId())
                            ? m.getUser2Id() : m.getUser1Id();
                    User p = userDao.findById(pid).orElse(null);
                    return p != null && p.getName() != null
                            && p.getName().toLowerCase().contains(query.toLowerCase());
                }).toList();
        refreshList(filtered);
    }

    private void showMatchDetail(Match match) {
        Long partnerId = match.getUser1Id().equals(sessionManager.getCurrentUserId())
                ? match.getUser2Id() : match.getUser1Id();
        selectedPartnerId = partnerId;

        User partner = userDao.findById(partnerId).orElse(null);
        if (partner == null) return;

        String name = partner.getName() != null ? partner.getName() : "?";
        detailInitial.setText(String.valueOf(name.charAt(0)).toUpperCase());
        detailName.setText(name);
        detailCity.setText(partner.getCity() != null ? "📍 " + partner.getCity() : "");
        detailBio.setText(partner.getBio() != null ? partner.getBio() : "");

        // Показуємо фото партнера
        if (detailPhoto != null) {
            if (partner.getPhotoPath() != null && !partner.getPhotoPath().isEmpty()) {
                File photoFile = new File(partner.getPhotoPath());
                if (photoFile.exists()) {
                    detailPhoto.setImage(new Image(photoFile.toURI().toString()));
                    detailPhoto.setVisible(true);
                    detailInitial.setVisible(false);
                }
            } else {
                detailPhoto.setVisible(false);
                detailInitial.setVisible(true);
            }
        }

        double score = match.getCompatibilityScore() != null
                ? match.getCompatibilityScore() : 30;
        detailScore.setText((int) score + "%");
        compatibilityBar.setProgress(score / 100.0);
    }

    @FXML
    private void openChat() {
        if (selectedPartnerId == null) return;

        try {
            javafx.scene.layout.StackPane contentArea = (javafx.scene.layout.StackPane)
                    matchesList.getScene().lookup("#contentArea");

            if (contentArea != null) {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/fxml/messages.fxml"));
                loader.setControllerFactory(SpringFxmlContext::getBean);
                Node messagesNode = loader.load();
                contentArea.getChildren().setAll(messagesNode);

                // Передаємо partnerId в MessagesController
                MessagesController mc = loader.getController();
                mc.openChatWith(selectedPartnerId);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void viewProfile() {
        if (selectedPartnerId == null) return;

        User partner = userDao.findById(selectedPartnerId).orElse(null);
        if (partner == null) return;

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Профіль " + partner.getName());
        alert.setHeaderText(partner.getName() + ", " +
                (partner.getAge() != null ? partner.getAge() + " р." : "") +
                " • " + (partner.getCity() != null ? partner.getCity() : ""));
        alert.setContentText(
                "Email: " + partner.getEmail() + "\n" +
                        "Про себе: " + (partner.getBio() != null ? partner.getBio() : "—")
        );
        alert.showAndWait();
    }

    @FXML
    private void reportUser() {
        if (selectedPartnerId == null) {
            showAlert("Оберіть збіг спочатку", Alert.AlertType.WARNING);
            return;
        }

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Скарга");
        dialog.setHeaderText("Поскаржитись на користувача");
        dialog.setContentText("Причина скарги:");

        dialog.showAndWait().ifPresent(reason -> {
            if (!reason.trim().isEmpty()) {
                try {
                    reportService.createReport(
                            sessionManager.getCurrentUserId(),
                            selectedPartnerId,
                            reason.trim()
                    );
                    showAlert("✅ Скарга надіслана!", Alert.AlertType.INFORMATION);
                } catch (Exception e) {
                    showAlert("❌ Помилка: " + e.getMessage(), Alert.AlertType.ERROR);
                }
            }
        });
    }

    private void showAlert(String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}