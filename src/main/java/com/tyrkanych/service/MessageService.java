package com.tyrkanych.service;

import com.tyrkanych.entity.Message;
import java.util.List;

public interface MessageService {

    Message sendMessage(Long senderId, Long receiverId, String text);

    List<Message> getConversation(Long user1Id, Long user2Id);

    List<Message> getRecentMessages(Long userId, int limit);
}