package com.tyrkanych.dao.impl;

import com.tyrkanych.config.ConnectionPool;
import com.tyrkanych.dao.PreferencesDao;
import com.tyrkanych.entity.Preferences;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class PreferencesDaoImpl extends BaseJdbcDao<Preferences, Long> implements PreferencesDao {

    @Override
    protected String getTableName() {
        return "preferences";
    }

    @Override
    protected String getIdColumnName() {
        return "id";
    }

    @Override
    protected String getInsertSql() {
        return """
                INSERT INTO preferences (user_id, preferred_gender, min_age, max_age, city) 
                VALUES (?, ?, ?, ?, ?)""";
    }

    @Override
    protected void setInsertParameters(PreparedStatement ps, Preferences pref) throws SQLException {
        ps.setLong(1, pref.getUserId());
        ps.setString(2, pref.getPreferredGender());
        ps.setInt(3, pref.getMinAge() != null ? pref.getMinAge() : 18);
        ps.setInt(4, pref.getMaxAge() != null ? pref.getMaxAge() : 100);
        ps.setString(5, pref.getCity());
    }

    @Override
    protected Preferences mapRow(ResultSet rs) throws SQLException {
        Preferences pref = new Preferences();
        pref.setId(rs.getLong("id"));
        pref.setUserId(rs.getLong("user_id"));
        pref.setPreferredGender(rs.getString("preferred_gender"));
        pref.setMinAge(rs.getInt("min_age"));
        pref.setMaxAge(rs.getInt("max_age"));
        pref.setCity(rs.getString("city"));
        return pref;
    }

    @Override
    protected void setGeneratedId(Preferences preferences, Long id) {
        preferences.setId(id);
    }

    @Override
    public Optional<Preferences> findByUserId(Long userId) {
        String sql = "SELECT * FROM preferences WHERE user_id = ?";
        return findBy(sql, userId);
    }

    @Override
    public void updateByUserId(Preferences preferences) {
        String sql = """
                UPDATE preferences 
                SET preferred_gender = ?, min_age = ?, max_age = ?, city = ? 
                WHERE user_id = ?""";

        try (Connection conn = ConnectionPool.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, preferences.getPreferredGender());
            ps.setInt(2, preferences.getMinAge());
            ps.setInt(3, preferences.getMaxAge());
            ps.setString(4, preferences.getCity());
            ps.setLong(5, preferences.getUserId());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating preferences", e);
        }
    }
}