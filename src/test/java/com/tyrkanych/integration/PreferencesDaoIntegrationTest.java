package com.tyrkanych.integration;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.tyrkanych.dao.impl.PreferencesDaoImpl;
import com.tyrkanych.dao.impl.UserDaoImpl;
import com.tyrkanych.entity.Preferences;
import com.tyrkanych.entity.User;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

public class PreferencesDaoIntegrationTest extends BaseIntegrationTest {

    private final PreferencesDaoImpl dao = new PreferencesDaoImpl();
    private final UserDaoImpl userDao = new UserDaoImpl();

    @Test
    void testPreferences() {
        User user = userDao.save(new User("pref@mail.com", "1", "Pref", "male", LocalDate.now()));

        Preferences p = new Preferences(user.getId());
        dao.save(p);

        assertTrue(dao.findByUserId(user.getId()).isPresent());
    }
}