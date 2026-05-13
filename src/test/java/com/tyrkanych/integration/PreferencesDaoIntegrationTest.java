// ===== PreferencesDaoIntegrationTest.java =====
package com.tyrkanych.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.tyrkanych.dao.impl.PreferencesDaoImpl;
import com.tyrkanych.dao.impl.UserDaoImpl;
import com.tyrkanych.entity.Preferences;
import com.tyrkanych.entity.User;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class PreferencesDaoIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private PreferencesDaoImpl preferencesDao;

    @Autowired
    private UserDaoImpl userDao;

    @Test
    void testSaveAndFind() {
        User user = userDao.save(
                new User("pref@mail.com", "1", "Pref", "male", LocalDate.now()));

        Preferences p = Preferences.builder()
                .userId(user.getId())
                .preferredGender("female")
                .minAge(20)
                .maxAge(35)
                .city("Київ")
                .build();

        preferencesDao.save(p);

        assertTrue(preferencesDao.findByUserId(user.getId()).isPresent());
        assertEquals("female",
                preferencesDao.findByUserId(user.getId()).get().getPreferredGender());
    }
}