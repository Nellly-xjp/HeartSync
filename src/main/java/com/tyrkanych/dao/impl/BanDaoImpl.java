package com.tyrkanych.dao.impl;

import com.tyrkanych.dao.BanDao;
import com.tyrkanych.entity.Ban;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

public class BanDaoImpl extends BaseJdbcDao<Ban, Long> implements BanDao {

    @Override
    protected String getTableName() {
        return "bans";
    }

    @Override
    protected String getIdColumnName() {
        return "id";
    }

    @Override
    protected String getInsertSql() {
        return "INSERT INTO bans (user_id, admin_id, reason, end_date) VALUES (?, ?, ?, ?)";
    }

    @Override
    protected void setInsertParameters(PreparedStatement ps, Ban ban) throws SQLException {
        ps.setLong(1, ban.getUserId());
        ps.setLong(2, ban.getAdminId());
        ps.setString(3, ban.getReason());
        ps.setTimestamp(4, ban.getEndDate() != null ? Timestamp.valueOf(ban.getEndDate()) : null);
    }

    @Override
    protected Ban mapRow(ResultSet rs) throws SQLException {
        Ban ban = new Ban();
        ban.setId(rs.getLong("id"));
        ban.setUserId(rs.getLong("user_id"));
        ban.setAdminId(rs.getLong("admin_id"));
        ban.setReason(rs.getString("reason"));

        Timestamp start = rs.getTimestamp("start_date");
        ban.setStartDate(start != null ? start.toLocalDateTime() : null);

        Timestamp end = rs.getTimestamp("end_date");
        ban.setEndDate(end != null ? end.toLocalDateTime() : null);

        return ban;
    }

    @Override
    protected void setGeneratedId(Ban ban, Long id) {
        ban.setId(id);
    }

    @Override
    public Optional<Ban> findActiveBanByUserId(Long userId) {
        String sql = """
                SELECT * FROM bans 
                WHERE user_id = ? 
                  AND (end_date IS NULL OR end_date > CURRENT_TIMESTAMP)
                ORDER BY start_date DESC LIMIT 1""";
        return findBy(sql, userId);
    }

    @Override
    public List<Ban> findByUserId(Long userId) {
        String sql = "SELECT * FROM bans WHERE user_id = ? ORDER BY start_date DESC";
        return findList(sql, userId);
    }
}