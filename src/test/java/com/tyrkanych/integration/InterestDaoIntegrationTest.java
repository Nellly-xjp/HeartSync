package com.tyrkanych.integration;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.tyrkanych.dao.impl.InterestDaoImpl;
import com.tyrkanych.entity.Interest;
import org.junit.jupiter.api.Test;

public class InterestDaoIntegrationTest extends BaseIntegrationTest {

    private final InterestDaoImpl dao = new InterestDaoImpl();

    @Test
    void testSaveFindDelete() {
        Interest interest = new Interest("Music");
        dao.save(interest);

        assertNotNull(interest.getId());
        assertTrue(dao.findByName("Music").isPresent());

        dao.deleteById(interest.getId());
        assertFalse(dao.existsById(interest.getId()));
    }
}