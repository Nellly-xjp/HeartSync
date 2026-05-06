package com.tyrkanych.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.tyrkanych.dao.impl.UserDaoImpl;
import com.tyrkanych.entity.User;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;

public class UserDaoIntegrationTest extends BaseIntegrationTest {

    private final UserDaoImpl userDao = new UserDaoImpl();

    @Test
    void testSaveAndFind() {
        User user = new User("test@mail.com", "123", "Test", "male", LocalDate.now());
        userDao.save(user);

        assertNotNull(user.getId());

        User found = userDao.findById(user.getId()).orElse(null);
        assertNotNull(found);
    }

    @Test
    void testFindAll() {
        userDao.save(new User("a@mail.com", "1", "A", "male", LocalDate.now()));
        userDao.save(new User("b@mail.com", "1", "B", "female", LocalDate.now()));

        List<User> users = userDao.findAll();
        assertTrue(users.size() >= 2);
    }

    @Test
    void testDelete() {
        User user = userDao.save(new User("del@mail.com", "1", "Del", "male", LocalDate.now()));
        userDao.deleteById(user.getId());

        assertFalse(userDao.existsById(user.getId()));
    }

    @Test
    void testCount() {
        int before = userDao.count();
        userDao.save(new User("count@mail.com", "1", "Count", "male", LocalDate.now()));
        int after = userDao.count();

        assertEquals(before + 1, after);
    }
}