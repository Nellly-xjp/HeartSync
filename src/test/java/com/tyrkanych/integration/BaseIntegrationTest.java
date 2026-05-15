package com.tyrkanych.integration;

import com.tyrkanych.config.TestConfig;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestConfig.class)
@ActiveProfiles("test")
public abstract class BaseIntegrationTest {

    @Autowired
    protected DataSource dataSource;

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    @BeforeEach
    void cleanDatabase() {
        try {
            jdbcTemplate.execute("SET REFERENTIAL_INTEGRITY FALSE");
            jdbcTemplate.execute("DELETE FROM user_interests");
            jdbcTemplate.execute("DELETE FROM likes");
            jdbcTemplate.execute("DELETE FROM matches");
            jdbcTemplate.execute("DELETE FROM messages");
            jdbcTemplate.execute("DELETE FROM preferences");
            jdbcTemplate.execute("DELETE FROM reports");
            jdbcTemplate.execute("DELETE FROM bans");
            jdbcTemplate.execute("DELETE FROM interests");
            jdbcTemplate.execute("DELETE FROM users");
            jdbcTemplate.execute("ALTER TABLE users ALTER COLUMN id RESTART WITH 1");
            jdbcTemplate.execute("ALTER TABLE interests ALTER COLUMN id RESTART WITH 1");
            jdbcTemplate.execute("ALTER TABLE likes ALTER COLUMN id RESTART WITH 1");
            jdbcTemplate.execute("ALTER TABLE matches ALTER COLUMN id RESTART WITH 1");
            jdbcTemplate.execute("ALTER TABLE messages ALTER COLUMN id RESTART WITH 1");
            jdbcTemplate.execute("ALTER TABLE preferences ALTER COLUMN id RESTART WITH 1");
            jdbcTemplate.execute("ALTER TABLE reports ALTER COLUMN id RESTART WITH 1");
            jdbcTemplate.execute("ALTER TABLE bans ALTER COLUMN id RESTART WITH 1");

            jdbcTemplate.execute("SET REFERENTIAL_INTEGRITY TRUE");

        } catch (Exception e) {
            System.err.println("Помилка очищення БД: " + e.getMessage());
        }
    }
}