package com.tyrkanych.dao.impl;

import com.tyrkanych.dao.InterestDao;
import com.tyrkanych.entity.Interest;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class InterestDaoImpl extends BaseJdbcDao<Interest, Long> implements InterestDao {

    @Override
    protected String getTableName() {
        return "interests";
    }

    @Override
    protected String getIdColumnName() {
        return "id";
    }

    @Override
    protected String getInsertSql() {
        return "INSERT INTO interests (name) VALUES (?)";
    }

    @Override
    protected void setInsertParameters(PreparedStatement ps, Interest interest)
            throws SQLException {
        ps.setString(1, interest.getName());
    }

    @Override
    protected Interest mapRow(ResultSet rs) throws SQLException {
        Interest interest = new Interest();
        interest.setId(rs.getLong("id"));
        interest.setName(rs.getString("name"));
        return interest;
    }

    @Override
    protected void setGeneratedId(Interest interest, Long id) {
        interest.setId(id);
    }

    @Override
    public Optional<Interest> findByName(String name) {
        String sql = "SELECT * FROM interests WHERE name = ?";
        return findBy(sql, name);
    }

    @Override
    public List<Interest> findByNameContaining(String keyword) {
        String sql = "SELECT * FROM interests WHERE name LIKE ?";
        return findList(sql, "%" + keyword + "%");
    }

    @Override
    public boolean existsByName(String name) {
        String sql = "SELECT 1 FROM interests WHERE name = ?";
        return exists(sql, name);
    }
}