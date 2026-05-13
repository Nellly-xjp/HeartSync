package com.tyrkanych.dao.impl;

import com.tyrkanych.dao.BaseDao;
import com.tyrkanych.entity.UserSettings;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.stereotype.Repository;

@Repository
public class UserSettingsDaoImpl extends BaseJdbcDao<UserSettings, Long> implements
        BaseDao<UserSettings, Long> {

    @Override
    protected String getTableName() {
        return "user_settings";
    }

    @Override
    protected String getIdColumnName() {
        return "id";
    }

    @Override
    protected String getInsertSql() {
        return """
                INSERT INTO user_settings (user_id, theme, language, notifications_enabled, privacy_level) 
                VALUES (?, ?, ?, ?, ?)""";
    }

    @Override
    protected void setInsertParameters(PreparedStatement ps, UserSettings settings)
            throws SQLException {
        ps.setLong(1, settings.getUserId());
        ps.setString(2, settings.getTheme());
        ps.setString(3, settings.getLanguage());
        ps.setBoolean(4,
                settings.getNotificationsEnabled() != null ? settings.getNotificationsEnabled()
                        : true);
        ps.setInt(5, settings.getPrivacyLevel() != null ? settings.getPrivacyLevel() : 1);
    }

    @Override
    protected UserSettings mapRow(ResultSet rs) throws SQLException {
        UserSettings settings = new UserSettings();
        settings.setId(rs.getLong("id"));
        settings.setUserId(rs.getLong("user_id"));
        settings.setTheme(rs.getString("theme"));
        settings.setLanguage(rs.getString("language"));
        settings.setNotificationsEnabled(rs.getBoolean("notifications_enabled"));
        settings.setPrivacyLevel(rs.getInt("privacy_level"));
        return settings;
    }

    @Override
    protected void setGeneratedId(UserSettings settings, Long id) {
        settings.setId(id);
    }

    // Додатковий метод
    public UserSettings findByUserId(Long userId) {
        String sql = "SELECT * FROM user_settings WHERE user_id = ?";
        return findBy(sql, userId).orElse(null);
    }
}