package com.tyrkanych.integration;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.tyrkanych.dao.MatchDao;
import com.tyrkanych.entity.Match;
import com.tyrkanych.entity.User;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class MatchDaoIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MatchDao matchDao;          // ← Spring інжектить

    @Autowired
    private com.tyrkanych.dao.UserDao userDao;   // ← теж через інтерфейс

    @Test
    void testCreateMatch() {
        User u1 = userDao.save(
                new User("m1@mail.com", "1", "A", "male", LocalDate.now()));
        User u2 = userDao.save(
                new User("m2@mail.com", "1", "B", "female", LocalDate.now()));

        Match match = new Match(u1.getId(), u2.getId(), 0.9);
        matchDao.save(match);

        assertNotNull(match.getId());
    }

    @Test
    void testFindByUsers() {
        User u1 = userDao.save(
                new User("m3@mail.com", "1", "C", "male", LocalDate.now()));
        User u2 = userDao.save(
                new User("m4@mail.com", "1", "D", "female", LocalDate.now()));

        Match match = new Match(u1.getId(), u2.getId(), 0.75);
        matchDao.save(match);

        assertTrue(matchDao.findByUsers(u1.getId(), u2.getId()).isPresent());
    }
}