// ===== InterestDaoIntegrationTest.java =====
package com.tyrkanych.integration;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.tyrkanych.dao.impl.InterestDaoImpl;
import com.tyrkanych.entity.Interest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class InterestDaoIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private InterestDaoImpl interestDao;

    @Test
    void testSaveFindDelete() {
        Interest interest = new Interest("Music");
        interestDao.save(interest);

        assertNotNull(interest.getId());
        assertTrue(interestDao.findByName("Music").isPresent());

        interestDao.deleteById(interest.getId());
        assertFalse(interestDao.existsById(interest.getId()));
    }
}