package com.essah.storagex.util;

import com.essah.storagex.model.FileItem;
import java.sql.*;
import java.time.ZoneOffset;
import java.util.*;

public class FileItemDao {
    public void create(Connection c, UUID owner, UUID folder, String original, String stored, long size, String mime) throws SQLException {
        String sql = "INSERT INTO files (owner_id, folder_id, original_filename, stored_filename, size_bytes, mime_type) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement s = c.prepareStatement(sql)) { s.setObject(1,owner); s.setObject(2,folder); s.setString(3,original); s.setString(4,stored); s.setLong(5,size); s.setString(6,mime); s.executeUpdate(); }
    }
    public List<FileItem> list(UUID owner, UUID folder) throws SQLException {
        String sql = "SELECT id, owner_id, folder_id, original_filename, stored_filename, size_bytes, mime_type, is_trashed, created_at, updated_at FROM files WHERE owner_id=? AND folder_id IS NOT DISTINCT FROM ? AND is_trashed=false ORDER BY original_filename";
        List<FileItem> all = new ArrayList<>();
        try (Connection c = DatabaseManager.getConnection(); PreparedStatement s = c.prepareStatement(sql)) { s.setObject(1,owner); s.setObject(2,folder); try(ResultSet r=s.executeQuery()){while(r.next()) all.add(map(r));} } return all;
    }
    private FileItem map(ResultSet r) throws SQLException { return new FileItem(r.getObject("id",UUID.class),r.getObject("owner_id",UUID.class),r.getObject("folder_id",UUID.class),r.getString("original_filename"),r.getString("stored_filename"),r.getLong("size_bytes"),r.getString("mime_type"),r.getBoolean("is_trashed"),r.getTimestamp("created_at").toInstant().atOffset(ZoneOffset.UTC),r.getTimestamp("updated_at").toInstant().atOffset(ZoneOffset.UTC)); }
}
