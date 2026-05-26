package com.tyrkanych.controller;

import com.tyrkanych.config.LanguageManager;
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
import javafx.scene.control.Button;
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
    @FXML private Label labelTitle;
    @FXML private Label labelSubtitle;
    @FXML private Button btnApply;

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
        applyLanguage();
        LanguageManager.addListener(this::applyLanguage);

        genderFilter.getItems().addAll(
                "uk".equals(LanguageManager.getCurrentLang())
                        ? new String[]{"Всі", "male", "female", "other"}
                        : new String[]{"All", "male", "female", "other"}
        );
        genderFilter.setValue(genderFilter.getItems().get(0));

        cardName.setText(LanguageManager.get("discover.loading"));
        cardBio.setText("");
        profileEmoji.setText("⏳");
        profileEmoji.setVisible(true);
        if (cardPhoto != null) cardPhoto.setVisible(false);

        new Thread(() -> {
            try { loadCandidatesData(); }
            catch (Exception e) {
                javafx.application.Platform.runLater(() -> {
                    cardName.setText(LanguageManager.get("discover.error"));
                    cardBio.setText("");
                    profileEmoji.setText("⚠️");
                    profileEmoji.setVisible(true);
                    if (cardPhoto != null) cardPhoto.setVisible(false);
                });
            }
        }).start();
    }

    private void applyLanguage() {
        if (minAge != null)
            minAge.setPromptText(LanguageManager.get("discover.filter.age.from"));
        if (maxAge != null)
            maxAge.setPromptText(LanguageManager.get("discover.filter.age.to"));
        if (genderFilter != null)
            genderFilter.setPromptText(LanguageManager.get("discover.filter.gender"));
        if (labelTitle != null)
            labelTitle.setText(LanguageManager.get("discover.title"));
        if (labelSubtitle != null)
            labelSubtitle.setText(LanguageManager.get("discover.subtitle"));
        if (btnApply != null)
            btnApply.setText(LanguageManager.get("discover.filter.apply"));

        // Оновлюємо текст якщо зараз показується "немає нікого"
        if (candidates != null && currentIndex >= candidates.size() && cardName != null) {
            cardName.setText(LanguageManager.get("discover.empty"));
            if (cardBio != null) cardBio.setText(LanguageManager.get("discover.back"));
        }
    }

    private void loadCandidatesData() {
        Long myId = sessionManager.getCurrentUserId();
        User me = userDao.findById(myId).orElse(null);
        String myCity = me != null && me.getCity() != null
                ? me.getCity().toLowerCase().trim() : "";

        List<User> all = userDao.findAll().stream()
                .filter(u -> !u.getId().equals(myId))
                .filter(u -> myCity.isEmpty() ||
                        (u.getCity() != null &&
                                u.getCity().toLowerCase().trim().equals(myCity)))
                .filter(u -> !likeService.existsLike(myId, u.getId()))
                .toList();

        javafx.application.Platform.runLater(() -> {
            candidates.setAll(all);
            currentIndex = 0;
            showCurrentCard();
        });
    }

    private void loadCandidates() {
        new Thread(() -> {
            try {
                loadCandidatesData();
            } catch (Exception e) {
                javafx.application.Platform.runLater(() ->
                        cardName.setText(LanguageManager.get("discover.error")));
            }
        }).start();
    }

    private void showCurrentCard() {
        if (candidates == null || currentIndex >= candidates.size()) {
            cardName.setText(LanguageManager.get("discover.empty"));
            cardAge.setText("");
            cardCity.setText("");
            cardBio.setText(LanguageManager.get("discover.back"));
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
        cardName.setText(u.getName() != null ? u.getName() : "?");
        cardAge.setText(u.getAge() != null ? u.getAge() + " р." : "");
        cardCity.setText(u.getCity() != null ? "📍 " + u.getCity() : "");
        cardBio.setText(u.getBio() != null ? u.getBio() : "");
        compatibilityBadge.setText("—");
        interest1.setText("");
        interest2.setText("");
        interest3.setText("");

        if (cardPhoto != null && u.getPhotoPath() != null && !u.getPhotoPath().isEmpty()) {
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
        if (u.getInterests() != null && !u.getInterests().isEmpty()) {
            String[] parts = u.getInterests().split(", ");
            interest1.setText(parts.length > 0 ? parts[0] : "");
            interest2.setText(parts.length > 1 ? parts[1] : "");
            interest3.setText(parts.length > 2 ? parts[2] : "");
        } else {
            interest1.setText("");
            interest2.setText("");
            interest3.setText("");
        }
    }

    @FXML
    private void applyFilters() {
        new Thread(() -> {
            try {
                Long myId = sessionManager.getCurrentUserId();
                User me = userDao.findById(myId).orElse(null);
                String myCity = me != null && me.getCity() != null
                        ? me.getCity().toLowerCase().trim() : "";

                String gender = genderFilter.getValue();
                int min = parseIntOrDefault(minAge.getText(), 18);
                int max = parseIntOrDefault(maxAge.getText(), 99);

                List<User> filtered = userDao.findAll().stream()
                        .filter(u -> !u.getId().equals(myId))
                        .filter(u -> myCity.isEmpty() ||
                                (u.getCity() != null &&
                                        u.getCity().toLowerCase().trim().equals(myCity)))
                        .filter(u -> !likeService.existsLike(myId, u.getId()))
                        .filter(u -> gender == null
                                || gender.equals("Всі")
                                || gender.equals("All")
                                || gender.equals(u.getGender()))
                        .filter(u -> u.getAge() == null ||
                                (u.getAge() >= min && u.getAge() <= max))
                        .toList();

                javafx.application.Platform.runLater(() -> {
                    candidates.setAll(filtered);
                    currentIndex = 0;
                    showCurrentCard();
                });
            } catch (Exception e) {
                javafx.application.Platform.runLater(() ->
                        statusLabel.setText(LanguageManager.get("discover.error")));
            }
        }).start();
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
                statusLabel.setText("🎉 " + LanguageManager.get("discover.match")
                        + " " + target.getName() + "!");
                statusLabel.setStyle("-fx-text-fill: #C44569; -fx-font-weight: bold;");
            } else {
                statusLabel.setText("♥ " + LanguageManager.get("discover.like"));
                statusLabel.setStyle("-fx-text-fill: #7C5CBF;");
            }
        } catch (Exception e) {
            statusLabel.setText("❌");
        }
        nextCandidate();
    }

    @FXML
    private void handlePass() {
        if (!hasCandidate()) return;
        statusLabel.setText(LanguageManager.get("discover.pass"));
        nextCandidate();
    }

    @FXML
    private void handleSuperLike() {
        if (!hasCandidate()) return;
        statusLabel.setText("⭐ " + LanguageManager.get("discover.superlike"));
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