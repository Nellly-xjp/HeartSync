package com.tyrkanych.dao.impl;

import com.tyrkanych.dao.AdminDao;
import com.tyrkanych.entity.Admin;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class AdminDaoImpl extends BaseJdbcDao<Admin, Long> implements AdminDao {

    @Override
    protected String getTableName() {
        return "admins";
    }

    @Override
    protected String getIdColumnName() {
        return "id";
    }

    @Override
    protected String getInsertSql() {
        return "INSERT INTO admins (name, email, password, role) VALUES (?, ?, ?, ?)";
    }

    @Override
    protected void setInsertParameters(PreparedStatement ps, Admin admin) throws SQLException {
        ps.setString(1, admin.getName());
        ps.setString(2, admin.getEmail());
        ps.setString(3, admin.getPassword());
        ps.setString(4, admin.getRole());
    }

    @Override
    protected Admin mapRow(ResultSet rs) throws SQLException {
        Admin admin = new Admin();
        admin.setId(rs.getLong("id"));
        admin.setName(rs.getString("name"));
        admin.setEmail(rs.getString("email"));
        admin.setPassword(rs.getString("password"));
        admin.setRole(rs.getString("role"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        admin.setCreatedAt(createdAt != null ? createdAt.toLocalDateTime() : null);

        return admin;
    }

    @Override
    protected void setGeneratedId(Admin admin, Long id) {
        admin.setId(id);
    }

    @Override
    public Optional<Admin> findByEmail(String email) {
        String sql = "SELECT * FROM admins WHERE email = ?";
        return findBy(sql, email);
    }

    @Override
    public boolean existsByEmail(String email) {
        String sql = "SELECT 1 FROM admins WHERE email = ?";
        return exists(sql, email);
    }
}