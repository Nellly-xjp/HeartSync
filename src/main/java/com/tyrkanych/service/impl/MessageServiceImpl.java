package com.tyrkanych.service.impl;

import com.tyrkanych.dao.MessageDao;
import com.tyrkanych.entity.Message;
import com.tyrkanych.service.MessageService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class MessageServiceImpl implements MessageService {

    private final MessageDao messageDao;

    @Autowired
    public MessageServiceImpl(MessageDao messageDao) {
        this.messageDao = messageDao;
    }

    @Override
    public Message sendMessage(Long senderId, Long receiverId, String text) {
        if (text == null || text.trim().isEmpty()) {
            throw new IllegalArgumentException("Message cannot be empty");
        }

        Message message = new Message(senderId, receiverId, text);
        return messageDao.save(message);
    }

    @Override
    public List<Message> getConversation(Long user1Id, Long user2Id) {
        return messageDao.findConversation(user1Id, user2Id);
    }

    @Override
    public List<Message> getRecentMessages(Long userId, int limit) {
        return messageDao.findRecentMessages(userId, limit);
    }
}