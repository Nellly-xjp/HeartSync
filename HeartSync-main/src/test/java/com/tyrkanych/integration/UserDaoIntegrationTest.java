package com.tyrkanych.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.tyrkanych.dao.impl.UserDaoImpl;
import com.tyrkanych.entity.User;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class UserDaoIntegrationTest extends BaseIntegrationTest {

    @Autowired  // Spring інжектить — не new UserDaoImpl()
    private UserDaoImpl userDao;

    @Test
    void testSaveAndFind() {
        User user = new User("test@mail.com", "123", "Test", "male", LocalDate.now());
        userDao.save(user);

        assertNotNull(user.getId());

        User found = userDao.findById(user.getId()).orElse(null);
        assertNotNull(found);
        assertEquals("test@mail.com", found.getEmail());
    }

    @Test
    void testFindAll() {
        userDao.save(new User("a@mail.com", "1", "A", "male", LocalDate.now()));
        userDao.save(new User("b@mail.com", "1", "B", "female", LocalDate.now()));

        List<User> users = userDao.findAll();
        assertEquals(2, users.size()); // точно 2, бо @BeforeEach очищує
    }

    @Test
    void testDelete() {
        User user = userDao.save(new User("del@mail.com", "1", "Del", "male", LocalDate.now()));
        userDao.deleteById(user.getId());

        assertFalse(userDao.existsById(user.getId()));
    }

    @Test
    void testCount() {
        assertEquals(0, userDao.count()); // після очищення = 0
        userDao.save(new User("count@mail.com", "1", "Count", "male", LocalDate.now()));
        assertEquals(1, userDao.count());
    }
}