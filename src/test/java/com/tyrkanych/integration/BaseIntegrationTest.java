package com.tyrkanych.integration;

import com.tyrkanych.DatabaseInitializer;
import com.tyrkanych.config.ConnectionPool;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class BaseIntegrationTest {

    @BeforeAll
    void setup() {
        ConnectionPool.initialize();
        DatabaseInitializer.createTables();
    }

    @BeforeEach
    void clean() {
        DatabaseInitializer.clearTables();
    }

    @AfterAll
    void teardown() {
        ConnectionPool.shutdown();
    }
}