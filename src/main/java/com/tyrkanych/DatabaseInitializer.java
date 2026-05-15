package com.tyrkanych;

import com.tyrkanych.config.ConnectionPool;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {

    public static void createTables() {
        String[] createStatements = {
                """
            CREATE TABLE IF NOT EXISTS users (
                id INT AUTO_INCREMENT PRIMARY KEY,
                email VARCHAR(191) UNIQUE NOT NULL,
                password VARCHAR(255) NOT NULL,
                name VARCHAR(100) NOT NULL,
                gender VARCHAR(20),
                birth_date DATE,
                city VARCHAR(100),
                bio TEXT,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            );
            """,
                """
            CREATE TABLE IF NOT EXISTS interests (
                id INT AUTO_INCREMENT PRIMARY KEY,
                name VARCHAR(100) UNIQUE NOT NULL
            );
            """,
                """
            CREATE TABLE IF NOT EXISTS user_interests (
                user_id INT NOT NULL,
                interest_id INT NOT NULL,
                level INT DEFAULT 1,
                PRIMARY KEY (user_id, interest_id),
                FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                FOREIGN KEY (interest_id) REFERENCES interests(id) ON DELETE CASCADE
            );
            """,
                """
            CREATE TABLE IF NOT EXISTS likes (
                id INT AUTO_INCREMENT PRIMARY KEY,
                from_user_id INT NOT NULL,
                to_user_id INT NOT NULL,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (from_user_id) REFERENCES users(id) ON DELETE CASCADE,
                FOREIGN KEY (to_user_id) REFERENCES users(id) ON DELETE CASCADE
            );
            """,
                """
            CREATE TABLE IF NOT EXISTS messages (
                id INT AUTO_INCREMENT PRIMARY KEY,
                sender_id INT NOT NULL,
                receiver_id INT NOT NULL,
                message_text TEXT NOT NULL,
                sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (sender_id) REFERENCES users(id) ON DELETE CASCADE,
                FOREIGN KEY (receiver_id) REFERENCES users(id) ON DELETE CASCADE
            );
            """,
                """
            CREATE TABLE IF NOT EXISTS preferences (
                id INT AUTO_INCREMENT PRIMARY KEY,
                user_id INT UNIQUE NOT NULL,
                preferred_gender VARCHAR(20),
                min_age INT DEFAULT 18,
                max_age INT DEFAULT 100,
                city VARCHAR(100),
                FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
            );
            """,
                """
            CREATE TABLE IF NOT EXISTS user_settings (
                id INT AUTO_INCREMENT PRIMARY KEY,
                user_id INT UNIQUE NOT NULL,
                theme VARCHAR(50) DEFAULT 'light',
                language VARCHAR(10) DEFAULT 'uk',
                notifications_enabled BOOLEAN DEFAULT TRUE,
                privacy_level INT DEFAULT 1,
                FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
            );
            """
                // Додай інші таблиці пізніше при потребі
        };

        try (Connection conn = ConnectionPool.getConnection();
                Statement stmt = conn.createStatement()) {

            for (String sql : createStatements) {
                stmt.execute(sql);
            }
            System.out.println(" Таблиці створено успішно (IF NOT EXISTS)");

        } catch (SQLException e) {
            throw new RuntimeException("Помилка створення таблиць", e);
        }
    }

    // Метод для тестів
    public static void clearTables() {
        try (Connection conn = ConnectionPool.getConnection();
                Statement stmt = conn.createStatement()) {

            stmt.executeUpdate("DELETE FROM user_interests");
            stmt.executeUpdate("DELETE FROM likes");
            stmt.executeUpdate("DELETE FROM messages");
            stmt.executeUpdate("DELETE FROM preferences");
            stmt.executeUpdate("DELETE FROM user_settings");
            stmt.executeUpdate("DELETE FROM interests");
            stmt.executeUpdate("DELETE FROM users");

            System.out.println(" Таблиці очищено");

        } catch (SQLException e) {
            System.err.println(" Помилка очищення таблиць: " + e.getMessage());
        }
    }
}