package com.tyrkanych.dao.impl;

import com.tyrkanych.dao.UserDao;
import com.tyrkanych.entity.User;
import com.tyrkanych.identity.IdentityMap;
import com.tyrkanych.uow.UnitOfWork;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class UserDaoImpl extends BaseJdbcDao<User, Long> implements UserDao {

    private final IdentityMap<Long, User> cache = new IdentityMap<>();

    @Override
    protected String getTableName() {
        return "users";
    }

    @Override
    protected String getIdColumnName() {
        return "id";
    }

    @Override
    protected String getInsertSql() {
        return """
                INSERT INTO users (email, password, name, gender, birth_date, city, bio)
                VALUES (?, ?, ?, ?, ?, ?, ?)""";
    }

    @Override
    protected void setInsertParameters(PreparedStatement ps, User user) throws SQLException {
        ps.setString(1, user.getEmail());
        ps.setString(2, user.getPassword());
        ps.setString(3, user.getName());
        ps.setString(4, user.getGender());
        ps.setDate(5, user.getBirthDate() != null ? Date.valueOf(user.getBirthDate()) : null);
        ps.setString(6, user.getCity());
        ps.setString(7, user.getBio());
    }

    @Override
    protected User mapRow(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setEmail(rs.getString("email"));
        user.setPassword(rs.getString("password"));
        user.setName(rs.getString("name"));
        user.setGender(rs.getString("gender"));
        user.setBirthDate(
                rs.getDate("birth_date") != null ? rs.getDate("birth_date").toLocalDate() : null);
        user.setCity(rs.getString("city"));
        user.setBio(rs.getString("bio"));
        user.setCreatedAt(rs.getTimestamp("created_at") != null
                ? rs.getTimestamp("created_at").toLocalDateTime()
                : null);

        return user;
    }

    @Override
    protected void setGeneratedId(User user, Long id) {
        user.setId(id);
        cache.put(id, user); // кешуємо після вставки
    }

    // ====================== CACHE ======================

    @Override
    public Optional<User> findById(Long id) {

        if (cache.contains(id)) {
            return Optional.of(cache.get(id));
        }

        Optional<User> userOpt = super.findById(id);
        userOpt.ifPresent(user -> cache.put(id, user));

        return userOpt;
    }

    // ====================== SPECIFIC METHODS ======================

    @Override
    public Optional<User> findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        return findBy(sql, email);
    }

    @Override
    public List<User> findByCity(String city) {
        String sql = "SELECT * FROM users WHERE city = ?";
        return findList(sql, city);
    }

    @Override
    public List<User> findByGender(String gender) {
        String sql = "SELECT * FROM users WHERE gender = ?";
        return findList(sql, gender);
    }

    @Override
    public boolean existsByEmail(String email) {
        String sql = "SELECT 1 FROM users WHERE email = ?";
        return exists(sql, email);
    }

    // ====================== UNIT OF WORK ======================

    @Override
    public void updatePassword(Long userId, String newPassword) {

        UnitOfWork uow = new UnitOfWork();
        uow.begin();

        try {
            Connection conn = uow.getConnection();

            String sql = "UPDATE users SET password = ? WHERE id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setString(1, newPassword);
            ps.setLong(2, userId);
            ps.executeUpdate();

            uow.commit();

        } catch (Exception e) {
            uow.rollback();
            throw new RuntimeException("Error updating password", e);
        }
    }
}