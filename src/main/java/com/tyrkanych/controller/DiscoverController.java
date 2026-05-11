package com.tyrkanych.controller;

import com.tyrkanych.dao.impl.UserDaoImpl;
import com.tyrkanych.entity.User;
import com.tyrkanych.service.LikeService;
import com.tyrkanych.session.SessionManager;
import java.util.List;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DiscoverController {

    private final UserDaoImpl userDao;
    private final LikeService likeService;
    private final SessionManager sessionManager;
    @FXML
    private Label profileEmoji;
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
    private List<User> candidates;
    private int currentIndex = 0;

    @Autowired
    public DiscoverController(UserDaoImpl userDao,
            LikeService likeService,
            SessionManager sessionManager) {
        this.userDao = userDao;
        this.likeService = likeService;
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
        candidates = userDao.findAll().stream()
                .filter(u -> !u.getId().equals(myId))
                .toList();
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
            return;
        }
        User u = candidates.get(currentIndex);
        cardName.setText(u.getName() != null ? u.getName() : "Без імені");
        cardAge.setText(u.getAge() != null ? u.getAge() + " р." : "");
        cardCity.setText(u.getCity() != null ? "📍 " + u.getCity() : "");
        cardBio.setText(u.getBio() != null ? u.getBio() : "");
        compatibilityBadge.setText("—"); // TODO: розрахунок сумісності
        interest1.setText("");
        interest2.setText("");
        interest3.setText("");
    }

    @FXML
    private void handleLike() {
        if (!hasCandidate()) {
            return;
        }
        User target = candidates.get(currentIndex);
        try {
            likeService.addLike(sessionManager.getCurrentUserId(), target.getId());
            statusLabel.setText("♥ Лайк!");
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

        candidates = userDao.findAll().stream()
                .filter(u -> !u.getId().equals(myId))
                .filter(u -> gender == null || gender.equals("Всі")
                        || gender.equals(u.getGender()))
                .filter(u -> city.isEmpty()
                        || (u.getCity() != null
                        && u.getCity().toLowerCase().contains(city)))
                .filter(u -> u.getAge() == null
                        || (u.getAge() >= min && u.getAge() <= max))
                .toList();
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