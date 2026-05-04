package com.tyrkanych.dao.impl;

import com.tyrkanych.config.ConnectionPool;
import com.tyrkanych.dao.LikeDao;
import com.tyrkanych.entity.Like;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

public class LikeDaoImpl extends BaseJdbcDao<Like, Long> implements LikeDao {

    @Override
    protected String getTableName() {
        return "likes";
    }

    @Override
    protected String getIdColumnName() {
        return "id";
    }

    @Override
    protected String getInsertSql() {
        return "INSERT INTO likes (from_user_id, to_user_id) VALUES (?, ?)";
    }

    @Override
    protected void setInsertParameters(PreparedStatement ps, Like like) throws SQLException {
        ps.setLong(1, like.getFromUserId());
        ps.setLong(2, like.getToUserId());
    }

    @Override
    protected Like mapRow(ResultSet rs) throws SQLException {
        Like like = new Like();
        like.setId(rs.getLong("id"));
        like.setFromUserId(rs.getLong("from_user_id"));
        like.setToUserId(rs.getLong("to_user_id"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        like.setCreatedAt(createdAt != null ? createdAt.toLocalDateTime() : null);

        return like;
    }

    @Override
    protected void setGeneratedId(Like like, Long id) {
        like.setId(id);
    }

    // ====================== SPECIFIC METHODS ======================

    @Override
    public Optional<Like> findByUsers(Long fromUserId, Long toUserId) {
        String sql = "SELECT * FROM likes WHERE from_user_id = ? AND to_user_id = ?";
        try (Connection conn = ConnectionPool.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, fromUserId);
            ps.setLong(2, toUserId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding like", e);
        }
        return Optional.empty();
    }

    @Override
    public List<Like> findByFromUserId(Long fromUserId) {
        String sql = "SELECT * FROM likes WHERE from_user_id = ?";
        return findList(sql, fromUserId);
    }

    @Override
    public List<Like> findByToUserId(Long toUserId) {
        String sql = "SELECT * FROM likes WHERE to_user_id = ?";
        return findList(sql, toUserId);
    }

  
    @Override
    public boolean existsLike(Long fromUserId, Long toUserId) {
        String sql = "SELECT 1 FROM likes WHERE from_user_id = ? AND to_user_id = ?";
        return exists(sql, fromUserId, toUserId);
    }

    @Override
    public void deleteLike(Long fromUserId, Long toUserId) {
        String sql = "DELETE FROM likes WHERE from_user_id = ? AND to_user_id = ?";
        try (Connection conn = ConnectionPool.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, fromUserId);
            ps.setLong(2, toUserId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting like", e);
        }
    }
}