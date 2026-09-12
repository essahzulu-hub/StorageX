package com.essah.storagex.util;

import com.essah.storagex.model.Folder;
import java.sql.*;
import java.time.ZoneOffset;
import java.util.*;

public class FolderDao {
    public Folder create(UUID owner, UUID parent, String name) throws SQLException {
        String sql = "INSERT INTO folders (owner_id, parent_id, name) VALUES (?, ?, ?) RETURNING id, owner_id, parent_id, name, is_trashed, created_at, updated_at";
        try (Connection c = DatabaseManager.getConnection(); PreparedStatement s = c.prepareStatement(sql)) {
            s.setObject(1, owner); s.setObject(2, parent); s.setString(3, name);
            try (ResultSet r = s.executeQuery()) { r.next(); return map(r); }
        }
    }
    public List<Folder> list(UUID owner, UUID parent) throws SQLException {
        String sql = "SELECT id, owner_id, parent_id, name, is_trashed, created_at, updated_at FROM folders WHERE owner_id=? AND parent_id IS NOT DISTINCT FROM ? AND is_trashed=false ORDER BY name";
        List<Folder> all = new ArrayList<>();
        try (Connection c = DatabaseManager.getConnection(); PreparedStatement s = c.prepareStatement(sql)) {
            s.setObject(1, owner); s.setObject(2, parent); try (ResultSet r = s.executeQuery()) { while(r.next()) all.add(map(r)); }
        } return all;
    }
    private Folder map(ResultSet r) throws SQLException { return new Folder(r.getObject("id", UUID.class), r.getObject("owner_id", UUID.class), r.getObject("parent_id", UUID.class), r.getString("name"), r.getBoolean("is_trashed"), r.getTimestamp("created_at").toInstant().atOffset(ZoneOffset.UTC), r.getTimestamp("updated_at").toInstant().atOffset(ZoneOffset.UTC)); }
}
