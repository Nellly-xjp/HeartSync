package com.tyrkanych.integration;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.tyrkanych.dao.impl.MatchDaoImpl;
import com.tyrkanych.dao.impl.UserDaoImpl;
import com.tyrkanych.entity.Match;
import com.tyrkanych.entity.User;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

public class MatchDaoIntegrationTest extends BaseIntegrationTest {

    private final MatchDaoImpl dao = new MatchDaoImpl();
    private final UserDaoImpl userDao = new UserDaoImpl();

    @Test
    void testMatch() {
        User u1 = userDao.save(new User("m1@mail.com", "1", "A", "male", LocalDate.now()));
        User u2 = userDao.save(new User("m2@mail.com", "1", "B", "female", LocalDate.now()));

        Match match = new Match(u1.getId(), u2.getId(), 0.9);
        dao.save(match);

        assertNotNull(match.getId());
    }
}