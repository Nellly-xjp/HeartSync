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
    private final ObservableList<User> candidates =
            FXCollections.observableArrayList();

    @FXML private Label profileEmoji;
    @FXML private ImageView cardPhoto;
    @FXML private Label compatibilityBadge;
    @FXML private Label cardName;
    @FXML private Label cardAge;
    @FXML private Label cardCity;
    @FXML private Label cardBio;
    @FXML private Label interest1;
    @FXML private Label interest2;
    @FXML private Label interest3;
    @FXML private Label statusLabel;
    @FXML private ComboBox<String> genderFilter;
    @FXML private TextField minAge;
    @FXML private TextField maxAge;

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
        User me = userDao.findById(myId).orElse(null);
        String myCity = me != null && me.getCity() != null
                ? me.getCity().toLowerCase().trim() : "";

        List<User> all = userDao.findAll().stream()
                .filter(u -> !u.getId().equals(myId))
                // Фільтр по місту — тільки з мого міста
                .filter(u -> myCity.isEmpty() ||
                        (u.getCity() != null &&
                                u.getCity().toLowerCase().trim().equals(myCity)))
                // Виключаємо вже лайкнутих
                .filter(u -> !likeService.existsLike(myId, u.getId()))
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
            profileEmoji.setVisible(true);
            if (cardPhoto != null) cardPhoto.setVisible(false);
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

        if (cardPhoto != null && u.getPhotoPath() != null
                && !u.getPhotoPath().isEmpty()) {
            File photoFile = new File(u.getPhotoPath());
            if (photoFile.exists()) {
                cardPhoto.setImage(new Image(photoFile.toURI().toString()));
                cardPhoto.setVisible(true);
                profileEmoji.setVisible(false);
            } else {
                cardPhoto.setVisible(false);
                profileEmoji.setVisible(true);
                profileEmoji.setText(getInitial(u.getName()));
            }
        } else {
            if (cardPhoto != null) cardPhoto.setVisible(false);
            profileEmoji.setVisible(true);
            profileEmoji.setText(getInitial(u.getName()));
        }
    }

    @FXML
    private void applyFilters() {
        Long myId = sessionManager.getCurrentUserId();
        User me = userDao.findById(myId).orElse(null);
        String myCity = me != null && me.getCity() != null
                ? me.getCity().toLowerCase().trim() : "";

        String gender = genderFilter.getValue();
        int min = parseIntOrDefault(minAge.getText(), 18);
        int max = parseIntOrDefault(maxAge.getText(), 99);

        List<User> filtered = userDao.findAll().stream()
                .filter(u -> !u.getId().equals(myId))
                // Місто завжди фільтруємо по місту поточного користувача
                .filter(u -> myCity.isEmpty() ||
                        (u.getCity() != null &&
                                u.getCity().toLowerCase().trim().equals(myCity)))
                // Виключаємо вже лайкнутих
                .filter(u -> !likeService.existsLike(myId, u.getId()))
                // Фільтр за статтю
                .filter(u -> gender == null || gender.equals("Всі") ||
                        gender.equals(u.getGender()))
                // Фільтр за віком
                .filter(u -> u.getAge() == null ||
                        (u.getAge() >= min && u.getAge() <= max))
                .toList();

        candidates.setAll(filtered);
        currentIndex = 0;
        showCurrentCard();
    }

    @FXML
    private void handleLike() {
        if (!hasCandidate()) return;
        User target = candidates.get(currentIndex);
        Long myId = sessionManager.getCurrentUserId();
        try {
            likeService.addLike(myId, target.getId());
            boolean mutual = likeService.existsLike(target.getId(), myId);
            if (mutual) {
                matchService.createMatch(myId, target.getId(), null);
                statusLabel.setText("🎉 Збіг з " + target.getName() + "!");
                statusLabel.setStyle(
                        "-fx-text-fill: #C44569; -fx-font-weight: bold;");
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
        if (!hasCandidate()) return;
        statusLabel.setText("Пропущено");
        nextCandidate();
    }

    @FXML
    private void handleSuperLike() {
        if (!hasCandidate()) return;
        statusLabel.setText("⭐ Супер-лайк!");
        nextCandidate();
    }

    private void nextCandidate() {
        currentIndex++;
        showCurrentCard();
    }

    private boolean hasCandidate() {
        return candidates != null && currentIndex < candidates.size();
    }

    private String getInitial(String name) {
        return (name != null && !name.isEmpty())
                ? String.valueOf(name.charAt(0)).toUpperCase() : "?";
    }

    private int parseIntOrDefault(String text, int def) {
        try {
            return Integer.parseInt(text.trim());
        } catch (NumberFormatException e) {
            return def;
        }
    }
}