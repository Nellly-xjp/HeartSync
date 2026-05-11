package com.tyrkanych.controller;

import com.tyrkanych.dao.impl.UserDaoImpl;
import com.tyrkanych.entity.Match;
import com.tyrkanych.entity.User;
import com.tyrkanych.service.MatchService;
import com.tyrkanych.session.SessionManager;
import java.util.List;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MatchesController {

    private final MatchService matchService;
    private final UserDaoImpl userDao;
    private final SessionManager sessionManager;
    @FXML
    private TextField searchField;
    @FXML
    private ListView<String> matchesList;
    @FXML
    private Label detailInitial;
    @FXML
    private Label detailName;
    @FXML
    private Label detailCity;
    @FXML
    private Label detailScore;
    @FXML
    private ProgressBar compatibilityBar;
    @FXML
    private Label detailBio;
    private List<Match> matches;

    @Autowired
    public MatchesController(MatchService matchService,
            UserDaoImpl userDao,
            SessionManager sessionManager) {
        this.matchService = matchService;
        this.userDao = userDao;
        this.sessionManager = sessionManager;
    }

    @FXML
    public void initialize() {
        loadMatches();

        matchesList.getSelectionModel().selectedIndexProperty()
                .addListener((obs, oldVal, newVal) -> {
                    int idx = newVal.intValue();
                    if (idx >= 0 && idx < matches.size()) {
                        showMatchDetail(matches.get(idx));
                    }
                });

        searchField.textProperty().addListener((obs, old, val) -> filterMatches(val));
    }

    private void loadMatches() {
        Long myId = sessionManager.getCurrentUserId();
        if (myId == null) {
            return;
        }
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
        if (matches == null) {
            return;
        }
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
                            && p.getName().toLowerCase()
                            .contains(query.toLowerCase());
                }).toList();
        refreshList(filtered);
    }

    private void showMatchDetail(Match match) {
        Long partnerId = match.getUser1Id().equals(sessionManager.getCurrentUserId())
                ? match.getUser2Id() : match.getUser1Id();
        User partner = userDao.findById(partnerId).orElse(null);
        if (partner == null) {
            return;
        }

        String name = partner.getName() != null ? partner.getName() : "?";
        detailInitial.setText(String.valueOf(name.charAt(0)).toUpperCase());
        detailName.setText(name);
        detailCity.setText(partner.getCity() != null ? "📍 " + partner.getCity() : "");
        detailBio.setText(partner.getBio() != null ? partner.getBio() : "");

        double score = match.getCompatibilityScore() != null
                ? match.getCompatibilityScore() : 0;
        detailScore.setText((int) score + "%");
        compatibilityBar.setProgress(score / 100.0);
    }

    @FXML
    private void openChat() { /* TODO: перехід до Messages */ }

    @FXML
    private void viewProfile() { /* TODO: перехід до Profile */ }
}