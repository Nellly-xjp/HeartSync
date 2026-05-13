// ===== UserInterestDaoImpl.java =====
package com.tyrkanych.dao.impl;

import com.tyrkanych.dao.UserInterestDao;
import com.tyrkanych.entity.UserInterest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class UserInterestDaoImpl implements UserInterestDao {

    private static final String INSERT_SQL =
            "INSERT INTO user_interests (user_id, interest_id, level) VALUES (?, ?, ?)";
    private static final String FIND_BY_USER_SQL =
            "SELECT * FROM user_interests WHERE user_id = ?";
    private static final String FIND_BY_INTEREST_SQL =
            "SELECT * FROM user_interests WHERE interest_id = ?";
    private static final String DELETE_BY_USER_SQL =
            "DELETE FROM user_interests WHERE user_id = ?";
    private static final String DELETE_SQL =
            "DELETE FROM user_interests WHERE user_id = ? AND interest_id = ?";
    private static final String EXISTS_SQL =
            "SELECT 1 FROM user_interests WHERE user_id = ? AND interest_id = ?";
    @Autowired // Spring DI замість ConnectionPool
    private DataSource dataSource;

    @Override
    public void save(UserInterest ui) {
        try (Connection conn = dataSource.getConnection();
                PreparedStatement ps = conn.prepareStatement(INSERT_SQL)) {
            ps.setLong(1, ui.getUserId());
            ps.setLong(2, ui.getInterestId());
            ps.setInt(3, ui.getLevel());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error saving user interest", e);
        }
    }

    @Override
    public void saveAll(List<UserInterest> userInterests) {
        if (userInterests.isEmpty()) {
            return;
        }
        try (Connection conn = dataSource.getConnection();
                PreparedStatement ps = conn.prepareStatement(INSERT_SQL)) {
            for (UserInterest ui : userInterests) {
                ps.setLong(1, ui.getUserId());
                ps.setLong(2, ui.getInterestId());
                ps.setInt(3, ui.getLevel());
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException("Error saving all user interests", e);
        }
    }

    @Override
    public List<UserInterest> findByUserId(Long userId) {
        return findList(FIND_BY_USER_SQL, userId);
    }

    @Override
    public List<UserInterest> findByInterestId(Long interestId) {
        return findList(FIND_BY_INTEREST_SQL, interestId);
    }

    @Override
    public void deleteByUserId(Long userId) {
        executeUpdate(DELETE_BY_USER_SQL, userId);
    }

    @Override
    public void delete(Long userId, Long interestId) {
        try (Connection conn = dataSource.getConnection();
                PreparedStatement ps = conn.prepareStatement(DELETE_SQL)) {
            ps.setLong(1, userId);
            ps.setLong(2, interestId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting user interest", e);
        }
    }

    @Override
    public boolean exists(Long userId, Long interestId) {
        try (Connection conn = dataSource.getConnection();
                PreparedStatement ps = conn.prepareStatement(EXISTS_SQL)) {
            ps.setLong(1, userId);
            ps.setLong(2, interestId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error checking user interest existence", e);
        }
    }

    private List<UserInterest> findList(String sql, Long param) {
        List<UserInterest> list = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, param);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding user interests", e);
        }
        return list;
    }

    private UserInterest mapRow(ResultSet rs) throws SQLException {
        UserInterest ui = new UserInterest();
        ui.setUserId(rs.getLong("user_id"));
        ui.setInterestId(rs.getLong("interest_id"));
        ui.setLevel(rs.getInt("level"));
        return ui;
    }

    private void executeUpdate(String sql, Long param) {
        try (Connection conn = dataSource.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, param);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error executing update", e);
        }
    }
}