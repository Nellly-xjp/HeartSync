package com.tyrkanych.controller;

import com.tyrkanych.dao.impl.UserDaoImpl;
import com.tyrkanych.entity.Message;
import com.tyrkanych.entity.User;
import com.tyrkanych.service.MessageService;
import com.tyrkanych.session.SessionManager;
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
    @FXML
    private TextField chatSearch;
    @FXML
    private ListView<String> chatList;
    @FXML
    private Label chatInitial;
    @FXML
    private Label chatPartnerName;
    @FXML
    private Label chatPartnerStatus;
    @FXML
    private ScrollPane chatScrollPane;
    @FXML
    private VBox messagesContainer;
    @FXML
    private TextField messageInput;
    private Long selectedPartnerId;

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
        // Завантаження списку співрозмовників
        Long myId = sessionManager.getCurrentUserId();
        if (myId == null) {
            return;
        }

        List<Message> recent = messageService.getRecentMessages(myId, 50);
        recent.stream()
                .map(m -> m.getSenderId().equals(myId)
                        ? m.getReceiverId() : m.getSenderId())
                .distinct()
                .forEach(pid -> {
                    User p = userDao.findById(pid).orElse(null);
                    if (p != null) {
                        chatList.getItems().add(p.getName());
                    }
                });

        chatList.getSelectionModel().selectedIndexProperty()
                .addListener((obs, old, val) -> {
                    // TODO: знайти partnerId за індексом і завантажити чат
                });
    }

    @FXML
    private void sendMessage() {
        String text = messageInput.getText().trim();
        if (text.isEmpty() || selectedPartnerId == null) {
            return;
        }

        Long myId = sessionManager.getCurrentUserId();
        messageService.sendMessage(myId, selectedPartnerId, text);
        messageInput.clear();
        loadConversation(selectedPartnerId);
    }

    private void loadConversation(Long partnerId) {
        Long myId = sessionManager.getCurrentUserId();
        List<Message> messages =
                messageService.getConversation(myId, partnerId);

        messagesContainer.getChildren().clear();

        for (Message msg : messages) {
            boolean isMe = msg.getSenderId().equals(myId);
            Label bubble = new Label(msg.getMessageText());
            bubble.setWrapText(true);
            bubble.getStyleClass().add(
                    isMe ? "chat-bubble-out" : "chat-bubble-in");

            HBox row = new HBox(bubble);
            row.setAlignment(isMe ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
            messagesContainer.getChildren().add(row);
        }

        // Скрол донизу
        chatScrollPane.layout();
        chatScrollPane.setVvalue(1.0);
    }
}