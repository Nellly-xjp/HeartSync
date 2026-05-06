package com.tyrkanych.integration;

import static org.junit.jupiter.api.Assertions.assertFalse;

import com.tyrkanych.dao.impl.MessageDaoImpl;
import com.tyrkanych.dao.impl.UserDaoImpl;
import com.tyrkanych.entity.Message;
import com.tyrkanych.entity.User;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;

public class MessageDaoIntegrationTest extends BaseIntegrationTest {

    private final MessageDaoImpl dao = new MessageDaoImpl();
    private final UserDaoImpl userDao = new UserDaoImpl();

    @Test
    void testConversation() {
        User u1 = userDao.save(new User("m1@mail.com", "1", "A", "male", LocalDate.now()));
        User u2 = userDao.save(new User("m2@mail.com", "1", "B", "female", LocalDate.now()));

        dao.save(new Message(u1.getId(), u2.getId(), "Hello"));

        List<Message> messages = dao.findConversation(u1.getId(), u2.getId());

        assertFalse(messages.isEmpty());
    }
}