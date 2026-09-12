package com.essah.storagex.util;

import com.essah.storagex.model.User;
import java.sql.*;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;

public class UserDao {
    private static final String COLUMNS = "id, username, email, password_hash, storage_quota_bytes, storage_used_bytes, created_at";

    public User create(String username, String email, String passwordHash) throws SQLException {
        String sql = "INSERT INTO users (username, email, password_hash) VALUES (?, ?, ?) RETURNING " + COLUMNS;
        try (Connection c = DatabaseManager.getConnection(); PreparedStatement s = c.prepareStatement(sql)) {
            s.setString(1, username); s.setString(2, email); s.setString(3, passwordHash);
            try (ResultSet r = s.executeQuery()) { r.next(); return map(r); }
        }
    }

    public Optional<User> findByUsername(String username) throws SQLException { return find("username", username); }
    public Optional<User> findById(UUID id) throws SQLException { return find("id", id); }

    private Optional<User> find(String column, Object value) throws SQLException {
        String sql = "SELECT " + COLUMNS + " FROM users WHERE " + column + " = ?";
        try (Connection c = DatabaseManager.getConnection(); PreparedStatement s = c.prepareStatement(sql)) {
            s.setObject(1, value);
            try (ResultSet r = s.executeQuery()) { return r.next() ? Optional.of(map(r)) : Optional.empty(); }
        }
    }

    private User map(ResultSet r) throws SQLException {
        return new User(r.getObject("id", UUID.class), r.getString("username"), r.getString("email"),
                r.getString("password_hash"), r.getLong("storage_quota_bytes"), r.getLong("storage_used_bytes"),
                r.getTimestamp("created_at").toInstant().atOffset(ZoneOffset.UTC));
    }
}
