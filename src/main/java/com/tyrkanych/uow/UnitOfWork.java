package com.tyrkanych.uow;

import com.tyrkanych.config.ConnectionPool;
import java.sql.Connection;
import java.sql.SQLException;

public class UnitOfWork {

    private Connection connection;

    public void begin() {
        try {
            connection = ConnectionPool.getConnection();
            connection.setAutoCommit(false);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public void commit() {
        try {
            connection.commit();
            connection.setAutoCommit(true);
            ConnectionPool.releaseConnection(connection);
        } catch (SQLException e) {
            rollback();
        }
    }

    public void rollback() {
        try {
            connection.rollback();
            connection.setAutoCommit(true);
            ConnectionPool.releaseConnection(connection);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}