package com.tyrkanych.controller;

import com.tyrkanych.dao.impl.UserDaoImpl;
import com.tyrkanych.entity.User;
import com.tyrkanych.service.LikeService;
import com.tyrkanych.service.MatchService;
import com.tyrkanych.session.SessionManager;
import java.io.File;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DiscoverController {

    private final UserDaoImpl userDao;
    private final LikeService likeService;
    private final MatchService matchService;
    private final SessionManager sessionManager;
    private final ObservableList<User> candidates = FXCollections.observableArrayList();
    @FXML
    private Label profileEmoji;
    @FXML
    private ImageView cardPhoto;
    @FXML
    private Label compatibilityBadge;
    @FXML
    private Label cardName;
    @FXML
    private Label cardAge;
    @FXML
    private Label cardCity;
    @FXML
    private Label cardBio;
    @FXML
    private Label interest1;
    @FXML
    private Label interest2;
    @FXML
    private Label interest3;
    @FXML
    private Label statusLabel;
    @FXML
    private ComboBox<String> genderFilter;
    @FXML
    private TextField cityFilter;
    @FXML
    private TextField minAge;
    @FXML
    private TextField maxAge;
    private int currentIndex = 0;

    @Autowired
    public DiscoverController(UserDaoImpl userDao,
            LikeService likeService,
            MatchService matchService,
            SessionManager sessionManager) {
        this.userDao = userDao;
        this.likeService = likeService;
        this.matchService = matchService;
        this.sessionManager = sessionManager;
    }

    @FXML
    public void initialize() {
        genderFilter.getItems().addAll("Всі", "male", "female", "other");
        genderFilter.setValue("Всі");
        loadCandidates();
    }

    private void loadCandidates() {
        Long myId = sessionManager.getCurrentUserId();
        List<User> all = userDao.findAll().stream()
                .filter(u -> !u.getId().equals(myId))
                .toList();
        candidates.setAll(all);
        currentIndex = 0;
        showCurrentCard();
    }

    private void showCurrentCard() {
        if (candidates == null || currentIndex >= candidates.size()) {
            cardName.setText("Більше нікого немає 😅");
            cardAge.setText("");
            cardCity.setText("");
            cardBio.setText("Поверніться пізніше");
            compatibilityBadge.setText("—");
            interest1.setText("");
            interest2.setText("");
            interest3.setText("");
            profileEmoji.setText("?");
            if (cardPhoto != null) {
                cardPhoto.setVisible(false);
            }
            return;
        }

        User u = candidates.get(currentIndex);
        cardName.setText(u.getName() != null ? u.getName() : "Без імені");
        cardAge.setText(u.getAge() != null ? u.getAge() + " р." : "");
        cardCity.setText(u.getCity() != null ? "📍 " + u.getCity() : "");
        cardBio.setText(u.getBio() != null ? u.getBio() : "");
        compatibilityBadge.setText("—");
        interest1.setText("");
        interest2.setText("");
        interest3.setText("");

        // Показуємо фото або ініціал
        if (cardPhoto != null && u.getPhotoPath() != null && !u.getPhotoPath().isEmpty()) {
            File photoFile = new File(u.getPhotoPath());
            if (photoFile.exists()) {
                cardPhoto.setImage(new Image(photoFile.toURI().toString()));
                cardPhoto.setVisible(true);
                profileEmoji.setVisible(false);
            } else {
                cardPhoto.setVisible(false);
                profileEmoji.setVisible(true);
                profileEmoji.setText(u.getName() != null && !u.getName().isEmpty()
                        ? String.valueOf(u.getName().charAt(0)).toUpperCase() : "?");
            }
        } else {
            if (cardPhoto != null) {
                cardPhoto.setVisible(false);
            }
            profileEmoji.setVisible(true);
            profileEmoji.setText(u.getName() != null && !u.getName().isEmpty()
                    ? String.valueOf(u.getName().charAt(0)).toUpperCase() : "?");
        }
    }

    @FXML
    private void handleLike() {
        if (!hasCandidate()) return;

        User target = candidates.get(currentIndex);
        Long myId = sessionManager.getCurrentUserId();

        try {
            likeService.addLike(myId, target.getId());

            // Перевіряємо чи є взаємний лайк
            boolean mutual = likeService.existsLike(target.getId(), myId);

            if (mutual) {
                matchService.createMatch(myId, target.getId(), null);
                statusLabel.setText("🎉 Збіг з " + target.getName() + "! Напишіть їм!");
                statusLabel.setStyle("-fx-text-fill: #C44569; -fx-font-weight: bold; -fx-font-size: 14px;");
            } else {
                statusLabel.setText("♥ Лайк!");
                statusLabel.setStyle("-fx-text-fill: #7C5CBF;");
            }
        } catch (Exception e) {
            statusLabel.setText("Вже лайкнуто");
        }
        nextCandidate();
    }

    @FXML
    private void handlePass() {
        if (!hasCandidate()) {
            return;
        }
        statusLabel.setText("Пропущено");
        nextCandidate();
    }

    @FXML
    private void handleSuperLike() {
        if (!hasCandidate()) {
            return;
        }
        statusLabel.setText("⭐ Супер-лайк!");
        nextCandidate();
    }

    @FXML
    private void applyFilters() {
        Long myId = sessionManager.getCurrentUserId();
        String gender = genderFilter.getValue();
        String city = cityFilter.getText().trim().toLowerCase();
        int min = parseIntOrDefault(minAge.getText(), 18);
        int max = parseIntOrDefault(maxAge.getText(), 99);

        List<User> filtered = userDao.findAll().stream()
                .filter(u -> !u.getId().equals(myId))
                .filter(u -> gender == null || gender.equals("Всі") || gender.equals(u.getGender()))
                .filter(u -> city.isEmpty() || (u.getCity() != null && u.getCity().toLowerCase()
                        .contains(city)))
                .filter(u -> u.getAge() == null || (u.getAge() >= min && u.getAge() <= max))
                .toList();

        candidates.setAll(filtered);
        currentIndex = 0;
        showCurrentCard();
    }

    private void nextCandidate() {
        currentIndex++;
        showCurrentCard();
    }

    private boolean hasCandidate() {
        return candidates != null && currentIndex < candidates.size();
    }

    private int parseIntOrDefault(String text, int def) {
        try {
            return Integer.parseInt(text.trim());
        } catch (NumberFormatException e) {
            return def;
        }
    }
}