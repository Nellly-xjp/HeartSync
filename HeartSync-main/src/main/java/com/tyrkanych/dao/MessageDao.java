package com.tyrkanych.dao;

import com.tyrkanych.entity.Message;
import java.util.List;

public interface MessageDao extends BaseDao<Message, Long> {

    List<Message> findBySenderId(Long senderId);

    List<Message> findByReceiverId(Long receiverId);

    List<Message> findConversation(Long user1Id, Long user2Id);

    List<Message> findRecentMessages(Long userId, int limit);
}