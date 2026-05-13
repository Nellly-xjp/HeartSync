package com.tyrkanych.dao.impl;

import com.tyrkanych.dao.LikeDao;
import com.tyrkanych.entity.Like;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
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
        Timestamp ts = rs.getTimestamp("created_at");
        like.setCreatedAt(ts != null ? ts.toLocalDateTime() : null);
        return like;
    }

    @Override
    protected void setGeneratedId(Like like, Long id) {
        like.setId(id);
    }

    @Override
    public Optional<Like> findByUsers(Long fromUserId, Long toUserId) {
        String sql = "SELECT * FROM likes WHERE from_user_id = ? AND to_user_id = ?";
        return findBy(sql, fromUserId, toUserId);
    }

    @Override
    public List<Like> findByFromUserId(Long fromUserId) {
        return findList("SELECT * FROM likes WHERE from_user_id = ?", fromUserId);
    }

    @Override
    public List<Like> findByToUserId(Long toUserId) {
        return findList("SELECT * FROM likes WHERE to_user_id = ?", toUserId);
    }

    @Override
    public boolean existsLike(Long fromUserId, Long toUserId) {
        String sql = "SELECT 1 FROM likes WHERE from_user_id = ? AND to_user_id = ?";
        return exists(sql, fromUserId, toUserId);
    }

    @Override
    public void deleteLike(Long fromUserId, Long toUserId) {
        String sql = "DELETE FROM likes WHERE from_user_id = ? AND to_user_id = ?";
        executeUpdate(sql, fromUserId, toUserId);
    }
}