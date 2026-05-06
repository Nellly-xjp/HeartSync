package com.tyrkanych;

import com.tyrkanych.config.ConnectionPool;
import java.sql.Connection;
import java.sql.Statement;

public class DatabaseInitializer {

    public static void createTables() {
        try (Connection conn = ConnectionPool.getConnection();
                Statement stmt = conn.createStatement()) {

            stmt.execute("""
                        CREATE TABLE IF NOT EXISTS users (
                            id INT AUTO_INCREMENT PRIMARY KEY,
                            email VARCHAR(191) UNIQUE NOT NULL,
                            password VARCHAR(255) NOT NULL,
                            name VARCHAR(100) NOT NULL,
                            gender VARCHAR(10),
                            birth_date DATE,
                            city VARCHAR(100),
                            bio TEXT,
                            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                        );
                    """);

            stmt.execute("""
                        CREATE TABLE IF NOT EXISTS interests (
                            id INT AUTO_INCREMENT PRIMARY KEY,
                            name VARCHAR(100) UNIQUE NOT NULL
                        );
                    """);

            stmt.execute("""
                        CREATE TABLE IF NOT EXISTS likes (
                            id INT AUTO_INCREMENT PRIMARY KEY,
                            from_user_id INT,
                            to_user_id INT
                        );
                    """);

            stmt.execute("""
                        CREATE TABLE IF NOT EXISTS matches (
                            id INT AUTO_INCREMENT PRIMARY KEY,
                            user1_id INT,
                            user2_id INT,
                            compatibility_score DOUBLE
                        );
                    """);

            // 🔥 ВИПРАВЛЕНО ТУТ
            stmt.execute("""
                        CREATE TABLE IF NOT EXISTS messages (
                            id INT AUTO_INCREMENT PRIMARY KEY,
                            sender_id INT,
                            receiver_id INT,
                            message_text TEXT,
                            sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                        );
                    """);

            stmt.execute("""
                        CREATE TABLE IF NOT EXISTS preferences (
                            id INT AUTO_INCREMENT PRIMARY KEY,
                            user_id INT,
                            preferred_gender VARCHAR(10),
                            min_age INT,
                            max_age INT,
                            city VARCHAR(100)
                        );
                    """);

            stmt.execute("""
                        CREATE TABLE IF NOT EXISTS bans (
                            id INT AUTO_INCREMENT PRIMARY KEY,
                            user_id INT,
                            admin_id INT,
                            reason TEXT,
                            end_date DATE
                        );
                    """);

            stmt.execute("""
                        CREATE TABLE IF NOT EXISTS reports (
                            id INT AUTO_INCREMENT PRIMARY KEY,
                            from_user_id INT,
                            reported_user_id INT,
                            reason TEXT,
                            status VARCHAR(50)
                        );
                    """);

            System.out.println("✅ Tables created");

        } catch (Exception e) {
            throw new RuntimeException("Помилка створення таблиць", e);
        }
    }

    public static void clearTables() {
        try (Connection conn = ConnectionPool.getConnection();
                Statement stmt = conn.createStatement()) {

            stmt.executeUpdate("DELETE FROM messages");
            stmt.executeUpdate("DELETE FROM likes");
            stmt.executeUpdate("DELETE FROM matches");
            stmt.executeUpdate("DELETE FROM reports");
            stmt.executeUpdate("DELETE FROM bans");
            stmt.executeUpdate("DELETE FROM preferences");
            stmt.executeUpdate("DELETE FROM interests");
            stmt.executeUpdate("DELETE FROM users");

        } catch (Exception e) {
            throw new RuntimeException("Помилка очищення таблиць", e);
        }
    }
}