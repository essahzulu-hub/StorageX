package com.essah.storagex.model;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Represents a row in the `users` table.
 * Note: password_hash is included here since the DAO needs it for login checks,
 * but the UI layer should never display it - just pass User objects around
 * carefully and don't dump toString() into logs.
 */
public class User {
    private UUID id;
    private String username;
    private String email;
    private String passwordHash;
    private long storageQuotaBytes;
    private long storageUsedBytes;
    private OffsetDateTime createdAt;

    public User() {
    }

    public User(UUID id, String username, String email, String passwordHash,
                long storageQuotaBytes, long storageUsedBytes, OffsetDateTime createdAt) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.storageQuotaBytes = storageQuotaBytes;
        this.storageUsedBytes = storageUsedBytes;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public long getStorageQuotaBytes() {
        return storageQuotaBytes;
    }

    public void setStorageQuotaBytes(long storageQuotaBytes) {
        this.storageQuotaBytes = storageQuotaBytes;
    }

    public long getStorageUsedBytes() {
        return storageUsedBytes;
    }

    public void setStorageUsedBytes(long storageUsedBytes) {
        this.storageUsedBytes = storageUsedBytes;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    /** Handy for showing "512 MB / 1 GB used" in the UI. */
    public double getUsagePercent() {
        if (storageQuotaBytes == 0) return 0;
        return (storageUsedBytes * 100.0) / storageQuotaBytes;
    }

    @Override
    public String toString() {
        // Deliberately excludes passwordHash
        return "User{id=" + id + ", username='" + username + "', email='" + email + "'}";
    }
}
