package com.tyrkanych.dao.impl;

import com.tyrkanych.dao.ReportDao;
import com.tyrkanych.entity.Report;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

public class ReportDaoImpl extends BaseJdbcDao<Report, Long> implements ReportDao {

    @Override
    protected String getTableName() {
        return "reports";
    }

    @Override
    protected String getIdColumnName() {
        return "id";
    }

    @Override
    protected String getInsertSql() {
        return """
                INSERT INTO reports (from_user_id, reported_user_id, reason, status) 
                VALUES (?, ?, ?, ?)""";
    }

    @Override
    protected void setInsertParameters(PreparedStatement ps, Report report) throws SQLException {
        ps.setLong(1, report.getFromUserId());
        ps.setLong(2, report.getReportedUserId());
        ps.setString(3, report.getReason());
        ps.setString(4, report.getStatus());
    }

    @Override
    protected Report mapRow(ResultSet rs) throws SQLException {
        Report report = new Report();
        report.setId(rs.getLong("id"));
        report.setFromUserId(rs.getLong("from_user_id"));
        report.setReportedUserId(rs.getLong("reported_user_id"));
        report.setReason(rs.getString("reason"));
        report.setStatus(rs.getString("status"));
        report.setReviewedByAdminId(rs.getLong("reviewed_by"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        report.setCreatedAt(createdAt != null ? createdAt.toLocalDateTime() : null);

        return report;
    }

    @Override
    protected void setGeneratedId(Report report, Long id) {
        report.setId(id);
    }

    @Override
    public List<Report> findByReportedUserId(Long reportedUserId) {
        String sql = "SELECT * FROM reports WHERE reported_user_id = ? ORDER BY created_at DESC";
        return findList(sql, reportedUserId);
    }

    @Override
    public List<Report> findByStatus(String status) {
        String sql = "SELECT * FROM reports WHERE status = ? ORDER BY created_at DESC";
        return findList(sql, status);
    }

    @Override
    public List<Report> findPendingReports() {
        String sql = "SELECT * FROM reports WHERE status = 'pending' ORDER BY created_at ASC";
        return findList(sql, null);
    }
}