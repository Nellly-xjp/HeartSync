package com.tyrkanych.dao.impl;

import com.tyrkanych.config.ConnectionPool;
import com.tyrkanych.dao.MessageDao;
import com.tyrkanych.entity.Message;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class MessageDaoImpl extends BaseJdbcDao<Message, Long> implements MessageDao {

    @Override
    protected String getTableName() {
        return "messages";
    }

    @Override
    protected String getIdColumnName() {
        return "id";
    }

    @Override
    protected String getInsertSql() {
        return "INSERT INTO messages (sender_id, receiver_id, message_text) VALUES (?, ?, ?)";
    }

    @Override
    protected void setInsertParameters(PreparedStatement ps, Message message) throws SQLException {
        ps.setLong(1, message.getSenderId());
        ps.setLong(2, message.getReceiverId());
        ps.setString(3, message.getMessageText());
    }

    @Override
    protected Message mapRow(ResultSet rs) throws SQLException {
        Message msg = new Message();
        msg.setId(rs.getLong("id"));
        msg.setSenderId(rs.getLong("sender_id"));
        msg.setReceiverId(rs.getLong("receiver_id"));
        msg.setMessageText(rs.getString("message_text"));

        Timestamp sentAt = rs.getTimestamp("sent_at");
        msg.setSentAt(sentAt != null ? sentAt.toLocalDateTime() : null);

        return msg;
    }

    @Override
    protected void setGeneratedId(Message message, Long id) {
        message.setId(id);
    }

    // ====================== SPECIFIC METHODS ======================

    @Override
    public List<Message> findBySenderId(Long senderId) {
        String sql = "SELECT * FROM messages WHERE sender_id = ? ORDER BY sent_at DESC";
        return findList(sql, senderId);
    }

    @Override
    public List<Message> findByReceiverId(Long receiverId) {
        String sql = "SELECT * FROM messages WHERE receiver_id = ? ORDER BY sent_at DESC";
        return findList(sql, receiverId);
    }

    @Override
    public List<Message> findConversation(Long user1Id, Long user2Id) {
        String sql = """
                SELECT * FROM messages 
                WHERE (sender_id = ? AND receiver_id = ?) 
                   OR (sender_id = ? AND receiver_id = ?) 
                ORDER BY sent_at ASC""";

        List<Message> messages = new ArrayList<>();
        try (Connection conn = ConnectionPool.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, user1Id);
            ps.setLong(2, user2Id);
            ps.setLong(3, user2Id);
            ps.setLong(4, user1Id);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    messages.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding conversation", e);
        }
        return messages;
    }

    @Override
    public List<Message> findRecentMessages(Long userId, int limit) {
        String sql = """
                SELECT * FROM messages 
                WHERE sender_id = ? OR receiver_id = ? 
                ORDER BY sent_at DESC LIMIT ?""";

        List<Message> messages = new ArrayList<>();
        try (Connection conn = ConnectionPool.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, userId);
            ps.setLong(2, userId);
            ps.setInt(3, limit);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    messages.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding recent messages", e);
        }
        return messages;
    }
}