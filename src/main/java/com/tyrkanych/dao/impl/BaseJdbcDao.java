package com.tyrkanych.dao.impl;

import com.tyrkanych.config.ConnectionPool;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class BaseJdbcDao<T, ID> {

    protected abstract String getTableName();

    protected abstract String getIdColumnName();

    protected abstract T mapRow(ResultSet rs) throws SQLException;

    protected abstract void setInsertParameters(PreparedStatement ps, T entity) throws SQLException;

    protected abstract String getInsertSql();

    // ====================== COMMON METHODS ======================

    public T save(T entity) {
        try (Connection conn = ConnectionPool.getConnection();
                PreparedStatement ps = conn.prepareStatement(getInsertSql(),
                        Statement.RETURN_GENERATED_KEYS)) {

            setInsertParameters(ps, entity);
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    setGeneratedId(entity, rs.getLong(1));
                }
            }
            return entity;
        } catch (SQLException e) {
            throw new RuntimeException("Error saving " + entity.getClass().getSimpleName(), e);
        }
    }

    public Optional<T> findById(ID id) {
        String sql = "SELECT * FROM " + getTableName() + " WHERE " + getIdColumnName() + " = ?";
        return findBy(sql, id);
    }

    public List<T> findAll() {
        String sql = "SELECT * FROM " + getTableName();
        return findList(sql, null);
    }

    public void deleteById(ID id) {
        String sql = "DELETE FROM " + getTableName() + " WHERE " + getIdColumnName() + " = ?";
        executeUpdate(sql, id);
    }

    public boolean existsById(ID id) {
        String sql = "SELECT 1 FROM " + getTableName() + " WHERE " + getIdColumnName() + " = ?";
        return exists(sql, id);
    }

    public int count() {
        String sql = "SELECT COUNT(*) FROM " + getTableName();
        try (Connection conn = ConnectionPool.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error counting records", e);
        }
    }

    // ====================== HELPER METHODS ======================

    protected Optional<T> findBy(String sql, Object... params) {
        try (Connection conn = ConnectionPool.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            setParameters(ps, params);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error executing findBy", e);
        }
        return Optional.empty();
    }

    protected List<T> findList(String sql, Object... params) {
        List<T> list = new ArrayList<>();

        try (Connection conn = ConnectionPool.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            setParameters(ps, params);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error executing findList", e);
        }
        return list;
    }

    protected void executeUpdate(String sql, Object... params) {
        try (Connection conn = ConnectionPool.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            setParameters(stmt, params);
            stmt.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException("Error executing update", e);
        }
    }

    protected boolean exists(String sql, Object... params) {
        try (Connection conn = ConnectionPool.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            setParameters(ps, params);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error checking existence", e);
        }
    }

    private void setParameters(PreparedStatement ps, Object... params) throws SQLException {
        if (params == null) {
            return;
        }

        int expectedParams = ps.getParameterMetaData().getParameterCount();

        if (params.length != expectedParams) {
            throw new RuntimeException(
                    "❌ Wrong number of parameters: expected " + expectedParams + ", got "
                            + params.length
            );
        }

        for (int i = 0; i < params.length; i++) {
            ps.setObject(i + 1, params[i]);
        }
    }

    protected void setGeneratedId(T entity, Long id) {
        // буде перевизначено в конкретних DAO при потребі
    }
}