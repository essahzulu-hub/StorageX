package com.essah.storagex.util;

import com.essah.storagex.model.FileItem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/** Database access for stored-file metadata. */
public class FileItemDao {
    public void create(Connection connection, UUID ownerId, UUID folderId, String originalFilename,
                       String storedFilename, long sizeBytes, String mimeType) throws SQLException {
        String sql = "INSERT INTO files (owner_id, folder_id, original_filename, stored_filename, size_bytes, mime_type) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setObject(1, ownerId);
            statement.setObject(2, folderId);
            statement.setString(3, originalFilename);
            statement.setString(4, storedFilename);
            statement.setLong(5, sizeBytes);
            statement.setString(6, mimeType);
            statement.executeUpdate();
        }
    }

    public List<FileItem> list(UUID ownerId, UUID folderId) throws SQLException {
        String sql = "SELECT id, owner_id, folder_id, original_filename, stored_filename, size_bytes, mime_type, " +
                "is_trashed, created_at, updated_at FROM files WHERE owner_id = ? " +
                "AND folder_id IS NOT DISTINCT FROM ? AND is_trashed = false ORDER BY original_filename";
        List<FileItem> files = new ArrayList<>();
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setObject(1, ownerId);
            statement.setObject(2, folderId);
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    files.add(map(result));
                }
            }
        }
        return files;
    }

    private FileItem map(ResultSet result) throws SQLException {
        Timestamp createdAt = result.getTimestamp("created_at");
        Timestamp updatedAt = result.getTimestamp("updated_at");
        return new FileItem(result.getObject("id", UUID.class), result.getObject("owner_id", UUID.class),
                result.getObject("folder_id", UUID.class), result.getString("original_filename"),
                result.getString("stored_filename"), result.getLong("size_bytes"), result.getString("mime_type"),
                result.getBoolean("is_trashed"), createdAt.toInstant().atOffset(java.time.ZoneOffset.UTC),
                updatedAt.toInstant().atOffset(java.time.ZoneOffset.UTC));
    }
}
