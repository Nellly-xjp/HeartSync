package com.tyrkanych.dao.impl;

import com.tyrkanych.dao.UserDao;
import com.tyrkanych.entity.User;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class UserDaoImpl extends BaseJdbcDao<User, Long> implements UserDao {

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
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;
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
        user.setPhotoPath(rs.getString("photo_path"));
        try { user.setRole(rs.getString("role")); } catch (SQLException ignored) {}
        try { user.setIsBanned(rs.getBoolean("is_banned")); } catch (SQLException ignored) {}
        try { user.setInterests(rs.getString("interests")); } catch (SQLException ignored) {}
        return user;
    }

    @Override
    protected void setGeneratedId(User user, Long id) {
        user.setId(id);
    }

    public void updatePhoto(Long userId, String photoPath) {
        String sql = "UPDATE users SET photo_path = ? WHERE id = ?";
        executeUpdate(sql, photoPath, userId);
    }

    // ==================== SPECIFIC METHODS ====================
    @Override
    public Optional<User> findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        return findBy(sql, email);
    }
    public void updateInterests(Long userId, String interests) {
        String sql = "UPDATE users SET interests = ? WHERE id = ?";
        executeUpdate(sql, interests, userId);
    }

    @Override
    public boolean existsByEmail(String email) {
        String sql = "SELECT 1 FROM users WHERE email = ?";
        return exists(sql, email);
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

    public void updateProfile(Long userId, String name, String city, String bio) {
        String sql = "UPDATE users SET name = ?, city = ?, bio = ? WHERE id = ?";
        executeUpdate(sql, name, city, bio, userId);
    }

    @Override
    public void updatePassword(Long userId, String newPassword) {
        String sql = "UPDATE users SET password = ? WHERE id = ?";
        executeUpdate(sql, newPassword, userId);
    }
    public void setBanned(Long userId, boolean banned) {
        String sql = "UPDATE users SET is_banned = ? WHERE id = ?";
        executeUpdate(sql, banned, userId);
    }

    public Optional<User> findByEmailAndPassword(String email, String password) {
        String sql = "SELECT * FROM users WHERE email = ?";
        return findBy(sql, email);
    }
}