package com.tyrkanych.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import com.tyrkanych.dao.impl.MessageDaoImpl;
import com.tyrkanych.dao.impl.UserDaoImpl;
import com.tyrkanych.entity.Message;
import com.tyrkanych.entity.User;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class MessageDaoIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MessageDaoImpl messageDao;

    @Autowired
    private UserDaoImpl userDao;

    @Test
    void testConversation() {
        User u1 = userDao.save(
                new User("msg1@mail.com", "1", "A", "male", LocalDate.now()));
        User u2 = userDao.save(
                new User("msg2@mail.com", "1", "B", "female", LocalDate.now()));

        messageDao.save(new Message(u1.getId(), u2.getId(), "Привіт!"));
        messageDao.save(new Message(u2.getId(), u1.getId(), "Привіт! Як справи?"));

        List<Message> messages = messageDao.findConversation(u1.getId(), u2.getId());

        assertFalse(messages.isEmpty());
        assertEquals(2, messages.size());
    }
}