package com.essah.storagex.util;

import com.essah.storagex.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Optional;
import java.util.UUID;

/** Database access for users. */
public class UserDao {
    private static final String COLUMNS =
            "id, username, email, password_hash, storage_quota_bytes, storage_used_bytes, created_at";

    public User create(String username, String email, String passwordHash) throws SQLException {
        String sql = "INSERT INTO users (username, email, password_hash) VALUES (?, ?, ?) RETURNING " + COLUMNS;
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            statement.setString(2, email);
            statement.setString(3, passwordHash);
            try (ResultSet result = statement.executeQuery()) {
                result.next();
                return map(result);
            }
        }
    }

    public Optional<User> findByUsername(String username) throws SQLException {
        String sql = "SELECT " + COLUMNS + " FROM users WHERE username = ?";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            try (ResultSet result = statement.executeQuery()) {
                return result.next() ? Optional.of(map(result)) : Optional.empty();
            }
        }
    }

    public Optional<User> findById(UUID id) throws SQLException {
        String sql = "SELECT " + COLUMNS + " FROM users WHERE id = ?";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setObject(1, id);
            try (ResultSet result = statement.executeQuery()) {
                return result.next() ? Optional.of(map(result)) : Optional.empty();
            }
        }
    }

    private User map(ResultSet result) throws SQLException {
        Timestamp createdAt = result.getTimestamp("created_at");
        return new User(
                result.getObject("id", UUID.class),
                result.getString("username"),
                result.getString("email"),
                result.getString("password_hash"),
                result.getLong("storage_quota_bytes"),
                result.getLong("storage_used_bytes"),
                createdAt.toInstant().atOffset(java.time.ZoneOffset.UTC)
        );
    }
}
