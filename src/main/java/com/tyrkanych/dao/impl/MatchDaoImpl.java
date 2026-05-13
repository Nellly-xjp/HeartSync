package com.tyrkanych.dao.impl;

import com.tyrkanych.dao.MatchDao;
import com.tyrkanych.entity.Match;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class MatchDaoImpl extends BaseJdbcDao<Match, Long> implements MatchDao {

    @Override
    protected String getTableName() {
        return "matches";
    }

    @Override
    protected String getIdColumnName() {
        return "id";
    }

    @Override
    protected String getInsertSql() {
        return "INSERT INTO matches (user1_id, user2_id, compatibility_score) VALUES (?, ?, ?)";
    }

    @Override
    protected void setInsertParameters(PreparedStatement ps, Match match) throws SQLException {
        ps.setLong(1, match.getUser1Id());
        ps.setLong(2, match.getUser2Id());
        ps.setDouble(3,
                match.getCompatibilityScore() != null ? match.getCompatibilityScore() : 0.0);
    }

    @Override
    protected Match mapRow(ResultSet rs) throws SQLException {
        Match match = new Match();
        match.setId(rs.getLong("id"));
        match.setUser1Id(rs.getLong("user1_id"));
        match.setUser2Id(rs.getLong("user2_id"));
        match.setCompatibilityScore(rs.getDouble("compatibility_score"));
        Timestamp ts = rs.getTimestamp("created_at");
        match.setCreatedAt(ts != null ? ts.toLocalDateTime() : null);
        return match;
    }

    @Override
    protected void setGeneratedId(Match match, Long id) {
        match.setId(id);
    }

    @Override
    public Optional<Match> findByUsers(Long user1Id, Long user2Id) {
        String sql = """
                SELECT * FROM matches 
                WHERE (user1_id = ? AND user2_id = ?) OR (user1_id = ? AND user2_id = ?)
                """;
        return findBy(sql, user1Id, user2Id, user2Id, user1Id);
    }

    @Override
    public List<Match> findByUserId(Long userId) {
        String sql = "SELECT * FROM matches WHERE user1_id = ? OR user2_id = ?";
        return findList(sql, userId, userId);
    }
}