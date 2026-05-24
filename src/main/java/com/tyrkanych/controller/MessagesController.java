package com.tyrkanych.controller;

import com.tyrkanych.config.LanguageManager;
import com.tyrkanych.dao.impl.UserDaoImpl;
import com.tyrkanych.entity.Message;
import com.tyrkanych.entity.User;
import com.tyrkanych.service.MessageService;
import com.tyrkanych.session.SessionManager;
import java.util.ArrayList;
import java.util.List;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MessagesController {

    private final MessageService messageService;
    private final UserDaoImpl userDao;
    private final SessionManager sessionManager;

    @FXML private TextField chatSearch;
    @FXML private ListView<String> chatList;
    @FXML private Label chatInitial;
    @FXML private Label chatPartnerName;
    @FXML private Label chatPartnerStatus;
    @FXML private ScrollPane chatScrollPane;
    @FXML private VBox messagesContainer;
    @FXML private TextField messageInput;
    @FXML private Label labelTitle;
    private Long selectedPartnerId;
    private final List<Long> partnerIds = new ArrayList<>();
    @FXML private Label labelPlaceholder;

    @Autowired
    public MessagesController(MessageService messageService,
            UserDaoImpl userDao,
            SessionManager sessionManager) {
        this.messageService = messageService;
        this.userDao = userDao;
        this.sessionManager = sessionManager;
    }

    @FXML
    public void initialize() {
        applyLanguage();
        LanguageManager.addListener(this::applyLanguage);
        Long myId = sessionManager.getCurrentUserId();
        if (myId == null) return;

        loadChatList(myId);

        chatList.getSelectionModel().selectedIndexProperty()
                .addListener((obs, old, val) -> {
                    int idx = val.intValue();
                    if (idx >= 0 && idx < partnerIds.size()) {
                        selectedPartnerId = partnerIds.get(idx);
                        User partner = userDao.findById(selectedPartnerId).orElse(null);
                        if (partner != null) {
                            chatPartnerName.setText(partner.getName());
                            chatInitial.setText(partner.getName() != null
                                    && !partner.getName().isEmpty()
                                    ? String.valueOf(partner.getName().charAt(0)).toUpperCase()
                                    : "?");
                            chatPartnerStatus.setText("онлайн");
                        }
                        loadConversation(selectedPartnerId);
                    }
                });

        if (chatSearch != null) {
            chatSearch.textProperty().addListener((obs, old, val) -> {
                filterChats(myId, val);
            });
        }
    }
    private void applyLanguage() {
        if (labelTitle != null)
            labelTitle.setText(LanguageManager.get("messages.title"));
        if (chatSearch != null)
            chatSearch.setPromptText(LanguageManager.get("messages.search"));
        if (messageInput != null)
            messageInput.setPromptText(LanguageManager.get("messages.input"));
        if (labelPlaceholder != null)
            labelPlaceholder.setText(LanguageManager.get("messages.placeholder"));
    }

    public void loadChatList(Long myId) {
        chatList.getItems().clear();
        partnerIds.clear();

        List<Message> recent = messageService.getRecentMessages(myId, 100);
        recent.stream()
                .map(m -> m.getSenderId().equals(myId)
                        ? m.getReceiverId() : m.getSenderId())
                .distinct()
                .forEach(pid -> {
                    User p = userDao.findById(pid).orElse(null);
                    if (p != null) {
                        partnerIds.add(pid);
                        chatList.getItems().add(
                                p.getName() != null ? p.getName() : "?"
                        );
                    }
                });
    }

    public void openChatWith(Long partnerId) {
        selectedPartnerId = partnerId;
        User partner = userDao.findById(partnerId).orElse(null);
        if (partner != null) {
            chatPartnerName.setText(partner.getName());
            chatInitial.setText(partner.getName() != null && !partner.getName().isEmpty()
                    ? String.valueOf(partner.getName().charAt(0)).toUpperCase() : "?");
            chatPartnerStatus.setText("онлайн");
        }
        loadConversation(partnerId);

        Long myId = sessionManager.getCurrentUserId();
        loadChatList(myId);
        for (int i = 0; i < partnerIds.size(); i++) {
            if (partnerIds.get(i).equals(partnerId)) {
                chatList.getSelectionModel().select(i);
                break;
            }
        }
    }

    private void filterChats(Long myId, String query) {
        chatList.getItems().clear();
        partnerIds.clear();

        List<Message> recent = messageService.getRecentMessages(myId, 100);
        recent.stream()
                .map(m -> m.getSenderId().equals(myId)
                        ? m.getReceiverId() : m.getSenderId())
                .distinct()
                .forEach(pid -> {
                    User p = userDao.findById(pid).orElse(null);
                    if (p != null && p.getName() != null) {
                        if (query == null || query.isBlank()
                                || p.getName().toLowerCase()
                                .contains(query.toLowerCase())) {
                            partnerIds.add(pid);
                            chatList.getItems().add(p.getName());
                        }
                    }
                });
    }

    @FXML
    private void sendMessage() {
        String text = messageInput.getText().trim();
        if (text.isEmpty() || selectedPartnerId == null) return;

        Long myId = sessionManager.getCurrentUserId();
        messageService.sendMessage(myId, selectedPartnerId, text);
        messageInput.clear();

        loadChatList(myId);
        loadConversation(selectedPartnerId);
    }

    private void loadConversation(Long partnerId) {
        Long myId = sessionManager.getCurrentUserId();
        List<Message> messages = messageService.getConversation(myId, partnerId);

        messagesContainer.getChildren().clear();

        if (messages.isEmpty()) {
            Label empty = new Label("Почніть розмову! 💬");
            empty.setStyle("-fx-text-fill: #9B93C0; -fx-font-size: 13px;");
            HBox emptyRow = new HBox(empty);
            emptyRow.setAlignment(Pos.CENTER);
            emptyRow.setStyle("-fx-padding: 40 0;");
            messagesContainer.getChildren().add(emptyRow);
            return;
        }

        for (Message msg : messages) {
            boolean isMe = msg.getSenderId().equals(myId);
            Label bubble = new Label(msg.getMessageText());
            bubble.setWrapText(true);
            bubble.setMaxWidth(320);
            bubble.getStyleClass().add(isMe ? "chat-bubble-out" : "chat-bubble-in");

            HBox row = new HBox(bubble);
            row.setAlignment(isMe ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
            row.setStyle("-fx-padding: 2 8;");
            messagesContainer.getChildren().add(row);
        }

        javafx.application.Platform.runLater(() -> {
            chatScrollPane.layout();
            chatScrollPane.setVvalue(1.0);
        });
    }
}