package com.tyrkanych.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class ConnectionPool {

    private static final String URL =
            "jdbc:h2:./data/heartsyncdb;AUTO_SERVER=TRUE;DB_CLOSE_DELAY=-1;MODE=MySQL;DATABASE_TO_UPPER=FALSE";

    private static final String USER = "sa";
    private static final String PASSWORD = "";
    private static final int POOL_SIZE = 10;
    private static final int TIMEOUT_SECONDS = 5;

    private static final BlockingQueue<Connection> pool = new LinkedBlockingQueue<>(POOL_SIZE);
    private static volatile boolean initialized = false;

    private ConnectionPool() {
    }

    public static void initialize() {
        if (initialized) {
            return;
        }

        synchronized (ConnectionPool.class) {
            if (initialized) {
                return;
            }

            try {
                for (int i = 0; i < POOL_SIZE; i++) {
                    pool.offer(createConnection());
                }
                initialized = true;
                System.out.println(
                        "✅ ConnectionPool initialized with " + POOL_SIZE + " connections");
                System.out.println("📁 База даних: файлова (heartsyncdb)");
            } catch (SQLException e) {
                throw new RuntimeException("Failed to initialize ConnectionPool", e);
            }
        }
    }

    private static Connection createConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }


    public static Connection getConnection() throws SQLException {
        if (!initialized) {
            initialize();
        }

        try {
            Connection conn = pool.poll(TIMEOUT_SECONDS, TimeUnit.SECONDS);

            if (conn == null) {
                // Якщо пул порожній — створюємо новий (навантаження)
                return createConnection();
            }

            // Перевіряємо, чи з'єднання живе
            if (conn.isClosed()) {
                return createConnection();
            }

            return conn;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new SQLException("Interrupted while waiting for connection", e);
        }
    }

    public static void releaseConnection(Connection connection) {
        if (connection == null) {
            return;
        }

        try {
            if (connection.isClosed()) {
                return;
            }

            // Повертаємо в пул, якщо є місце
            if (!pool.offer(connection)) {
                // Якщо пул переповнений — закриваємо зайве з'єднання
                connection.close();
            }
        } catch (SQLException e) {
            try {
                connection.close();
            } catch (SQLException ex) {
                // ignore
            }
        }
    }

    public static void shutdown() {
        pool.forEach(conn -> {
            try {
                if (conn != null && !conn.isClosed()) {
                    conn.close();
                }
            } catch (SQLException ignored) {
            }
        });
        pool.clear();
        initialized = false;
    }
}