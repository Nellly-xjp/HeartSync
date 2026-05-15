package com.tyrkanych.integration;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.tyrkanych.dao.impl.UserDaoImpl;
import com.tyrkanych.entity.Match;
import com.tyrkanych.entity.User;
import com.tyrkanych.service.MatchService;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class MatchServiceIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MatchService matchService;

    @Autowired
    private UserDaoImpl userDao;

    @Test
    void testCreateMatch() {
        User u1 = userDao.save(
                new User("ms1@mail.com", "1", "A", "male", LocalDate.now()));
        User u2 = userDao.save(
                new User("ms2@mail.com", "1", "B", "female", LocalDate.now()));

        Match match = matchService.createMatch(u1.getId(), u2.getId(), null);

        assertNotNull(match.getId());
    }

    @Test
    void testDuplicateMatchThrows() {
        User u1 = userDao.save(
                new User("ms3@mail.com", "1", "C", "male", LocalDate.now()));
        User u2 = userDao.save(
                new User("ms4@mail.com", "1", "D", "female", LocalDate.now()));

        matchService.createMatch(u1.getId(), u2.getId(), null);

        assertThrows(
                IllegalArgumentException.class,
                () -> matchService.createMatch(u1.getId(), u2.getId(), null)
        );
    }

    @Test
    void testFindByUserId() {
        User u1 = userDao.save(
                new User("ms5@mail.com", "1", "E", "male", LocalDate.now()));
        User u2 = userDao.save(
                new User("ms6@mail.com", "1", "F", "female", LocalDate.now()));

        matchService.createMatch(u1.getId(), u2.getId(), null);

        List<Match> matches = matchService.findByUserId(u1.getId());
        assertTrue(matches.size() >= 1);
    }
}