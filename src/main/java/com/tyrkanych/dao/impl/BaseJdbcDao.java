package com.tyrkanych.dao.impl;

import com.tyrkanych.dao.BaseDao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class BaseJdbcDao<T, ID> implements BaseDao<T, ID> {

    @Autowired
    protected DataSource dataSource;

    protected Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    // ==================== ABSTRACT METHODS ====================
    protected abstract String getTableName();

    protected abstract String getIdColumnName();

    protected abstract String getInsertSql();

    protected abstract void setInsertParameters(PreparedStatement ps, T entity) throws SQLException;

    protected abstract T mapRow(ResultSet rs) throws SQLException;

    protected abstract void setGeneratedId(T entity, Long id);

    // ==================== COMMON CRUD ====================
    @Override
    public T save(T entity) {
        String sql = getInsertSql();
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql,
                        Statement.RETURN_GENERATED_KEYS)) {

            setInsertParameters(ps, entity);
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    setGeneratedId(entity, keys.getLong(1));
                }
            }
            return entity;
        } catch (SQLException e) {
            throw new RuntimeException("Помилка збереження: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<T> findById(ID id) {
        String sql = "SELECT * FROM " + getTableName() + " WHERE " + getIdColumnName() + " = ?";
        return findBy(sql, id);
    }

    @Override
    public List<T> findAll() {
        String sql = "SELECT * FROM " + getTableName();
        return findList(sql);
    }

    @Override
    public void deleteById(ID id) {
        String sql = "DELETE FROM " + getTableName() + " WHERE " + getIdColumnName() + " = ?";
        execute(sql, id);
    }

    @Override
    public boolean existsById(ID id) {
        String sql = "SELECT 1 FROM " + getTableName() + " WHERE " + getIdColumnName() + " = ?";
        return exists(sql, id);
    }

    @Override
    public int count() {
        String sql = "SELECT COUNT(*) FROM " + getTableName();
        return count(sql);
    }

    // ==================== HELPER METHODS ====================
    protected Optional<T> findBy(String sql, Object... params) {
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            setParams(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Помилка пошуку: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    protected List<T> findList(String sql, Object... params) {
        List<T> result = new ArrayList<>();
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            setParams(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Помилка запиту: " + e.getMessage(), e);
        }
        return result;
    }

    /**
     * Для DELETE / UPDATE без повернення ключа (varargs версія).
     */
    protected void execute(String sql, Object... params) {
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            setParams(ps, params);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Помилка виконання: " + e.getMessage(), e);
        }
    }

    /**
     * Аліас execute() — використовується в DAO для UPDATE/DELETE з іменованого методу. Виправляє
     * помилку компіляції "cannot find symbol executeUpdate".
     */
    protected void executeUpdate(String sql, Object... params) {
        execute(sql, params);
    }

    protected boolean exists(String sql, Object... params) {
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            setParams(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Помилка перевірки існування: " + e.getMessage(), e);
        }
    }

    protected int count(String sql, Object... params) {
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            setParams(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Помилка підрахунку: " + e.getMessage(), e);
        }
        return 0;
    }

    private void setParams(PreparedStatement ps, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            ps.setObject(i + 1, params[i]);
        }
    }
}