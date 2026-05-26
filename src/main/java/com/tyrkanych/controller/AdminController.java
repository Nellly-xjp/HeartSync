package com.tyrkanych.controller;

import com.tyrkanych.config.LanguageManager;
import com.tyrkanych.config.ThemeManager;
import com.tyrkanych.entity.Report;
import com.tyrkanych.entity.User;
import com.tyrkanych.service.AdminService;
import com.tyrkanych.session.SessionManager;
import java.io.IOException;
import java.util.List;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AdminController {

    private final AdminService adminService;
    private final SessionManager sessionManager;

    @FXML private VBox reportsPane;
    @FXML private VBox usersPane;
    @FXML private VBox bannedPane;
    @FXML private VBox statsPane;
    @FXML private VBox settingsPane;
    @FXML private Label contentTitle;
    @FXML private Label adminStatus;
    @FXML private Label usersStatus;
    @FXML private Label bannedStatus;
    @FXML private Label adminThemeStatus;
    @FXML private Button btnExcelReport;
    @FXML private ListView<String> reportsList;
    @FXML private ListView<String> usersList;
    @FXML private ListView<String> bannedList;

    @FXML private Label reportReporter;
    @FXML private Label reportTarget;
    @FXML private Label reportReason;

    @FXML private Label statUsers;
    @FXML private Label statReports;
    @FXML private Label statBanned;

    @FXML private Button btnReports;
    @FXML private Button btnUsers;
    @FXML private Button btnBanned;
    @FXML private Button btnStats;
    @FXML private Button btnSettings;
    @FXML private Label labelPanel;
    @FXML private Button btnLogout;
    @FXML private Button btnExit;
    @FXML private Label labelReportDetail;
    @FXML private Button btnBan;
    @FXML private Button btnDismiss;
    @FXML private Button btnBanUser;
    @FXML private Button btnUnban;
    @FXML private Label labelStatUsers;
    @FXML private Label labelStatReports;
    @FXML private Label labelStatBanned;
    @FXML private Label labelThemeTitle;
    @FXML private Label labelLangTitle;
    @FXML private Button btnLightTheme;
    @FXML private Button btnDarkTheme;

    private List<Report> reports;
    private List<User> users;
    private List<User> banned;
    private Button activeBtn;

    @Autowired
    public AdminController(AdminService adminService, SessionManager sessionManager) {
        this.adminService = adminService;
        this.sessionManager = sessionManager;
    }

    @FXML
    public void initialize() {
        Platform.runLater(() -> {
            Scene scene = reportsList.getScene();
            if (scene != null) ThemeManager.setMainScene(scene);
        });
        LanguageManager.addListener(this::applyLanguage);
        applyLanguage();
        showReports();
    }

    @FXML
    private void showReports() {
        setActive(btnReports);
        contentTitle.setText(LanguageManager.get("nav.reports"));
        show(reportsPane);

        new Thread(() -> {
            reports = adminService.getAllReports();
            Platform.runLater(() -> {
                reportsList.getItems().clear();
                for (Report r : reports) {
                    String targetInfo = adminService.getUserInfo(r.getReportedUserId());
                    reportsList.getItems().add("⚠ " + targetInfo + " — " + r.getReason());
                }
            });
        }).start();

        reportsList.getSelectionModel().selectedIndexProperty()
                .addListener((obs, old, idx) -> {
                    int i = idx.intValue();
                    if (i >= 0 && reports != null && i < reports.size()) {
                        Report r = reports.get(i);
                        String reporterInfo = adminService.getUserInfo(r.getFromUserId());
                        String targetInfo = adminService.getUserInfo(r.getReportedUserId());
                        reportReporter.setText("Від: " + reporterInfo);
                        reportTarget.setText("На: " + targetInfo);
                        reportReason.setText("Причина: " + r.getReason());
                    }
                });
    }
    @FXML
    private void showUsers() {
        setActive(btnUsers);
        contentTitle.setText(LanguageManager.get("nav.users"));
        show(usersPane);

        new Thread(() -> {
            users = adminService.getAllUsers();
            Platform.runLater(() -> {
                usersList.getItems().clear();
                for (User u : users) {
                    String status = Boolean.TRUE.equals(u.getIsBanned()) ? " 🚫" : "";
                    usersList.getItems().add(u.getName() + " | " + u.getEmail() + status);
                }
            });
        }).start();
    }

    @FXML
    private void showBanned() {
        setActive(btnBanned);
        contentTitle.setText(LanguageManager.get("nav.banned"));
        show(bannedPane);

        new Thread(() -> {
            banned = adminService.getBannedUsers();
            Platform.runLater(() -> {
                bannedList.getItems().clear();
                for (User u : banned) {
                    bannedList.getItems().add("🚫 " + u.getName() + " | " + u.getEmail());
                }
            });
        }).start();
    }

    @FXML
    private void showStats() {
        setActive(btnStats);
        contentTitle.setText(LanguageManager.get("nav.stats"));
        show(statsPane);

        new Thread(() -> {
            long totalUsers = adminService.getTotalUsers();
            long totalReports = adminService.getTotalReports();
            long totalBanned = adminService.getBannedCount();
            Platform.runLater(() -> {
                statUsers.setText(String.valueOf(totalUsers));
                statReports.setText(String.valueOf(totalReports));
                statBanned.setText(String.valueOf(totalBanned));
            });
        }).start();
    }

    @FXML
    private void showSettings() {
        setActive(btnSettings);
        contentTitle.setText(LanguageManager.get("admin.settings"));
        show(settingsPane);
        updateThemeStatus();
    }

    @FXML
    private void banSelected() {
        int idx = reportsList.getSelectionModel().getSelectedIndex();
        if (idx < 0 || reports == null || idx >= reports.size()) {
            adminStatus.setText(LanguageManager.get("admin.ban") + " — оберіть скаргу");
            return;
        }
        Long targetId = reports.get(idx).getReportedUserId();
        new Thread(() -> {
            adminService.banUser(targetId);
            Platform.runLater(() -> {
                adminStatus.setText("✅ " + LanguageManager.get("admin.ban"));
                showReports();
            });
        }).start();
    }

    @FXML
    private void dismissReport() {
        adminStatus.setText("✓ " + LanguageManager.get("admin.dismiss"));
    }

    @FXML
    private void banSelectedUser() {
        int idx = usersList.getSelectionModel().getSelectedIndex();
        if (idx < 0 || users == null || idx >= users.size()) {
            usersStatus.setText("Оберіть користувача");
            return;
        }
        Long targetId = users.get(idx).getId();
        new Thread(() -> {
            adminService.banUser(targetId);
            Platform.runLater(() -> {
                usersStatus.setText("✅ " + LanguageManager.get("admin.ban"));
                showUsers();
            });
        }).start();
    }

    @FXML
    private void unbanSelected() {
        int idx = bannedList.getSelectionModel().getSelectedIndex();
        if (idx < 0 || banned == null || idx >= banned.size()) {
            bannedStatus.setText("Оберіть користувача");
            return;
        }
        Long targetId = banned.get(idx).getId();
        new Thread(() -> {
            adminService.unbanUser(targetId);
            Platform.runLater(() -> {
                bannedStatus.setText("✅ " + LanguageManager.get("admin.unban"));
                showBanned();
            });
        }).start();
    }

    @FXML
    private void setLightTheme() {
        ThemeManager.setCurrentTheme("/styles/styles.css");
        updateThemeStatus();
    }

    @FXML
    private void setDarkTheme() {
        ThemeManager.setCurrentTheme("/styles/styles-dark.css");
        updateThemeStatus();
    }

    @FXML
    private void setLangUk() {
        LanguageManager.setLanguage("uk");
    }

    @FXML
    private void setLangEn() {
        LanguageManager.setLanguage("en");
    }

    @FXML
    private void handleLogout() throws IOException {
        sessionManager.logout();
        Stage stage = (Stage) reportsList.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/fxml/login.fxml"));
        loader.setControllerFactory(SpringFxmlContext::getBean);
        Parent root = loader.load();
        Scene loginScene = new Scene(root, 900, 650);
        ThemeManager.registerScene(loginScene);
        stage.setScene(loginScene);
        stage.setTitle("HeartSync — Вхід");
    }

    @FXML
    private void handleExit() {
        Platform.exit();
        System.exit(0);
    }

    private void applyLanguage() {
        if (btnReports != null) btnReports.setText("⚠  " + LanguageManager.get("nav.reports"));
        if (btnUsers != null) btnUsers.setText("👥  " + LanguageManager.get("nav.users"));
        if (btnBanned != null) btnBanned.setText("🚫  " + LanguageManager.get("nav.banned"));
        if (btnStats != null) btnStats.setText("📊  " + LanguageManager.get("nav.stats"));
        if (btnSettings != null) btnSettings.setText("⚙  " + LanguageManager.get("admin.settings"));
        if (btnLogout != null) btnLogout.setText("👤  " + LanguageManager.get("nav.logout"));
        if (btnExit != null) btnExit.setText("✕  " + LanguageManager.get("nav.exit"));
        if (labelPanel != null) labelPanel.setText(LanguageManager.get("admin.panel"));
        if (labelReportDetail != null) labelReportDetail.setText(LanguageManager.get("admin.reports.detail"));
        if (btnBan != null) btnBan.setText("🚫  " + LanguageManager.get("admin.ban"));
        if (btnDismiss != null) btnDismiss.setText("✓  " + LanguageManager.get("admin.dismiss"));
        if (btnBanUser != null) btnBanUser.setText("🚫  " + LanguageManager.get("admin.ban"));
        if (btnUnban != null) btnUnban.setText("✓  " + LanguageManager.get("admin.unban"));
        if (labelStatUsers != null) labelStatUsers.setText(LanguageManager.get("nav.users"));
        if (labelStatReports != null) labelStatReports.setText(LanguageManager.get("nav.reports"));
        if (labelStatBanned != null) labelStatBanned.setText(LanguageManager.get("nav.banned"));
        if (labelThemeTitle != null) labelThemeTitle.setText(LanguageManager.get("admin.theme"));
        if (labelLangTitle != null) labelLangTitle.setText(LanguageManager.get("admin.language"));
        if (btnLightTheme != null) btnLightTheme.setText(LanguageManager.get("settings.theme.light"));
        if (btnDarkTheme != null) btnDarkTheme.setText(LanguageManager.get("settings.theme.dark"));
        if (btnExcelReport != null)
            btnExcelReport.setText(LanguageManager.get("profile.excel"));

        updateThemeStatus();
    }

    private void updateThemeStatus() {
        if (adminThemeStatus == null) return;
        if (ThemeManager.getCurrentTheme().contains("dark")) {
            adminThemeStatus.setText(LanguageManager.get("admin.theme.dark"));
        } else {
            adminThemeStatus.setText(LanguageManager.get("admin.theme.light"));
        }
    }

    private void show(VBox pane) {
        reportsPane.setVisible(false);  reportsPane.setManaged(false);
        usersPane.setVisible(false);    usersPane.setManaged(false);
        bannedPane.setVisible(false);   bannedPane.setManaged(false);
        statsPane.setVisible(false);    statsPane.setManaged(false);
        settingsPane.setVisible(false); settingsPane.setManaged(false);
        pane.setVisible(true);
        pane.setManaged(true);
    }
    @FXML
    private void exportToExcel() {
        javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
        fileChooser.setTitle("Зберегти Excel звіт");
        fileChooser.setInitialFileName("heartsync_admin_report.xlsx");
        fileChooser.getExtensionFilters().add(
                new javafx.stage.FileChooser.ExtensionFilter("Excel файли", "*.xlsx"));

        Stage stage = (Stage) reportsList.getScene().getWindow();
        java.io.File file = fileChooser.showSaveDialog(stage);
        if (file == null) return;

        new Thread(() -> {
            try {
                List<User> allUsers = adminService.getAllUsers();
                List<Report> allReports = adminService.getAllReports();
                adminService.exportToExcel(allUsers, allReports, file.getAbsolutePath());
                Platform.runLater(() ->
                        contentTitle.setText("✅ Excel збережено: " + file.getName()));
            } catch (Exception e) {
                Platform.runLater(() ->
                        contentTitle.setText("❌ Помилка: " + e.getMessage()));
            }
        }).start();
    }
    private void setActive(Button btn) {
        if (activeBtn != null) activeBtn.getStyleClass().remove("nav-item-active");
        btn.getStyleClass().add("nav-item-active");
        activeBtn = btn;
    }
}