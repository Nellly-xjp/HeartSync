package com.tyrkanych.service.impl;

import com.tyrkanych.dao.MessageDao;
import com.tyrkanych.entity.Message;
import com.tyrkanych.service.MessageEncryptionService;
import com.tyrkanych.service.MessageService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
public class MessageServiceImpl implements MessageService {

    private final MessageDao messageDao;
    private final MessageEncryptionService encryptionService;

    @Autowired
    public MessageServiceImpl(MessageDao messageDao,
            MessageEncryptionService encryptionService) {
        this.messageDao = messageDao;
        this.encryptionService = encryptionService;
    }

    @Override
    public Message sendMessage(Long senderId,
            Long receiverId, String text) {
        // Шифруємо перед збереженням
        String encrypted = encryptionService.encrypt(text);

        Message message = new Message();
        message.setSenderId(senderId);
        message.setReceiverId(receiverId);
        message.setMessageText(encrypted); // зберігаємо зашифроване

        return messageDao.save(message);
    }

    @Override
    public List<Message> getConversation(Long user1Id,
            Long user2Id) {
        List<Message> messages = messageDao
                .findConversation(user1Id, user2Id);

        // Дешифруємо при читанні
        messages.forEach(msg -> {
            String decrypted = encryptionService
                    .decrypt(msg.getMessageText());
            msg.setMessageText(decrypted);
        });

        return messages;
    }

    @Override
    public List<Message> getRecentMessages(Long userId,
            int limit) {
        List<Message> messages = messageDao
                .findRecentMessages(userId, limit);

        // Дешифруємо
        messages.forEach(msg -> {
            try {
                String decrypted = encryptionService
                        .decrypt(msg.getMessageText());
                msg.setMessageText(decrypted);
            } catch (Exception e) {
                // якщо старе повідомлення не зашифроване
                // залишаємо як є
            }
        });

        return messages;
    }
}
