package com.essah.storagex.util;

import com.essah.storagex.model.Folder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/** Database access for folders. */
public class FolderDao {
    public Folder create(UUID ownerId, UUID parentId, String name) throws SQLException {
        String sql = "INSERT INTO folders (owner_id, parent_id, name) VALUES (?, ?, ?) " +
                "RETURNING id, owner_id, parent_id, name, is_trashed, created_at, updated_at";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setObject(1, ownerId);
            statement.setObject(2, parentId);
            statement.setString(3, name);
            try (ResultSet result = statement.executeQuery()) {
                result.next();
                return map(result);
            }
        }
    }

    public List<Folder> list(UUID ownerId, UUID parentId) throws SQLException {
        String sql = "SELECT id, owner_id, parent_id, name, is_trashed, created_at, updated_at " +
                "FROM folders WHERE owner_id = ? AND parent_id IS NOT DISTINCT FROM ? AND is_trashed = false ORDER BY name";
        List<Folder> folders = new ArrayList<>();
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setObject(1, ownerId);
            statement.setObject(2, parentId);
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    folders.add(map(result));
                }
            }
        }
        return folders;
    }

    private Folder map(ResultSet result) throws SQLException {
        Timestamp createdAt = result.getTimestamp("created_at");
        Timestamp updatedAt = result.getTimestamp("updated_at");
        return new Folder(result.getObject("id", UUID.class), result.getObject("owner_id", UUID.class),
                result.getObject("parent_id", UUID.class), result.getString("name"), result.getBoolean("is_trashed"),
                createdAt.toInstant().atOffset(java.time.ZoneOffset.UTC), updatedAt.toInstant().atOffset(java.time.ZoneOffset.UTC));
    }
}
