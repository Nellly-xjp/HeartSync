package com.tyrkanych.dao.impl;

import com.tyrkanych.entity.AuditLog;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import org.springframework.stereotype.Repository;

@Repository
public class AuditLogDaoImpl extends BaseJdbcDao<AuditLog, Long> {

    @Override
    protected String getTableName() {
        return "audit_logs";
    }

    @Override
    protected String getIdColumnName() {
        return "id";
    }

    @Override
    protected String getInsertSql() {
        return "INSERT INTO audit_logs (admin_id, action, target_user_id) VALUES (?, ?, ?)";
    }

    @Override
    protected void setInsertParameters(PreparedStatement ps, AuditLog log) throws SQLException {
        ps.setLong(1, log.getAdminId());
        ps.setString(2, log.getAction());
        ps.setLong(3, log.getTargetUserId() != null ? log.getTargetUserId() : 0);
    }

    @Override
    protected AuditLog mapRow(ResultSet rs) throws SQLException {
        AuditLog log = new AuditLog();
        log.setId(rs.getLong("id"));
        log.setAdminId(rs.getLong("admin_id"));
        log.setAction(rs.getString("action"));
        log.setTargetUserId(rs.getLong("target_user_id"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        log.setCreatedAt(createdAt != null ? createdAt.toLocalDateTime() : null);

        return log;
    }

    @Override
    protected void setGeneratedId(AuditLog log, Long id) {
        log.setId(id);
    }
}