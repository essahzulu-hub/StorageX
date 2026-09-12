package com.essah.storagex.model;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Represents a row in the `files` table.
 *
 * IMPORTANT naming distinction:
 *  - originalFilename: what the user typed/uploaded, shown in the UI (e.g. "resume.pdf")
 *  - storedFilename: the UUID-based name actually used on disk (e.g. "a1b2c3d4....bin")
 *
 * We never use the user's original filename as an actual filesystem path -
 * that avoids path traversal issues and filename collisions entirely.
 */
public class FileItem {
    private UUID id;
    private UUID ownerId;
    private UUID folderId; // nullable - null means root level
    private String originalFilename;
    private String storedFilename;
    private long sizeBytes;
    private String mimeType;
    private boolean trashed;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public FileItem() {
    }

    public FileItem(UUID id, UUID ownerId, UUID folderId, String originalFilename,
                     String storedFilename, long sizeBytes, String mimeType,
                     boolean trashed, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
        this.id = id;
        this.ownerId = ownerId;
        this.folderId = folderId;
        this.originalFilename = originalFilename;
        this.storedFilename = storedFilename;
        this.sizeBytes = sizeBytes;
        this.mimeType = mimeType;
        this.trashed = trashed;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(UUID ownerId) {
        this.ownerId = ownerId;
    }

    public UUID getFolderId() {
        return folderId;
    }

    public void setFolderId(UUID folderId) {
        this.folderId = folderId;
    }

    public String getOriginalFilename() {
        return originalFilename;
    }

    public void setOriginalFilename(String originalFilename) {
        this.originalFilename = originalFilename;
    }

    public String getStoredFilename() {
        return storedFilename;
    }

    public void setStoredFilename(String storedFilename) {
        this.storedFilename = storedFilename;
    }

    public long getSizeBytes() {
        return sizeBytes;
    }

    public void setSizeBytes(long sizeBytes) {
        this.sizeBytes = sizeBytes;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public boolean isTrashed() {
        return trashed;
    }

    public void setTrashed(boolean trashed) {
        this.trashed = trashed;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    /** Human-readable size, e.g. "4.2 MB" - handy for the UI table. */
    public String getReadableSize() {
        if (sizeBytes < 1024) return sizeBytes + " B";
        int exp = (int) (Math.log(sizeBytes) / Math.log(1024));
        String unit = "KMGTPE".charAt(exp - 1) + "B";
        return String.format("%.1f %s", sizeBytes / Math.pow(1024, exp), unit);
    }

    @Override
    public String toString() {
        return originalFilename + " (" + getReadableSize() + ")";
    }
}
