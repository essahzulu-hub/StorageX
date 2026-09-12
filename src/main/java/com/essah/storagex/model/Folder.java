package com.essah.storagex.model;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Represents a row in the `folders` table.
 * parentId == null means this folder sits at the user's root level.
 */
public class Folder {
    private UUID id;
    private UUID ownerId;
    private UUID parentId; // nullable
    private String name;
    private boolean trashed;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public Folder() {
    }

    public Folder(UUID id, UUID ownerId, UUID parentId, String name,
                  boolean trashed, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
        this.id = id;
        this.ownerId = ownerId;
        this.parentId = parentId;
        this.name = name;
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

    public UUID getParentId() {
        return parentId;
    }

    public void setParentId(UUID parentId) {
        this.parentId = parentId;
    }

    public boolean isRoot() {
        return parentId == null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    @Override
    public String toString() {
        return name;
    }
}
