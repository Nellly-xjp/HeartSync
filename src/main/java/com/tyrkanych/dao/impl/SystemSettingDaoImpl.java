package com.tyrkanych.dao.impl;

import com.tyrkanych.entity.SystemSetting;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class SystemSettingDaoImpl extends BaseJdbcDao<SystemSetting, Long> {

    @Override
    protected String getTableName() {
        return "system_settings";
    }

    @Override
    protected String getIdColumnName() {
        return "id";
    }

    @Override
    protected String getInsertSql() {
        return "INSERT INTO system_settings (setting_key, setting_value) VALUES (?, ?)";
    }

    @Override
    protected void setInsertParameters(PreparedStatement ps, SystemSetting s) throws SQLException {
        ps.setString(1, s.getSettingKey());
        ps.setString(2, s.getSettingValue());
    }

    @Override
    protected SystemSetting mapRow(ResultSet rs) throws SQLException {
        SystemSetting s = new SystemSetting();
        s.setId(rs.getLong("id"));
        s.setSettingKey(rs.getString("setting_key"));
        s.setSettingValue(rs.getString("setting_value"));
        return s;
    }

    public Optional<SystemSetting> findByKey(String key) {
        String sql = "SELECT * FROM system_settings WHERE setting_key = ?";
        return findBy(sql, key);
    }

    @Override
    protected void setGeneratedId(SystemSetting setting, Long id) {
        setting.setId(id);
    }
}