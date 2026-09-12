-- StorageX database schema
-- Run this once against your Postgres database to set everything up:
--   psql -U postgres -d storagex -f schema.sql

-- Enable UUID generation (Postgres has this built in via pgcrypto or uuid-ossp)
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- Enable trigram matching for fast fuzzy filename search
CREATE EXTENSION IF NOT EXISTS pg_trgm;

-- ============================================================
-- USERS
-- ============================================================
CREATE TABLE IF NOT EXISTS users (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    username        VARCHAR(50) UNIQUE NOT NULL,
    email           VARCHAR(255) UNIQUE NOT NULL,
    password_hash   VARCHAR(255) NOT NULL,   -- BCrypt hash, never plaintext
    storage_quota_bytes BIGINT NOT NULL DEFAULT 1073741824, -- 1 GB default quota
    storage_used_bytes  BIGINT NOT NULL DEFAULT 0,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- ============================================================
-- FOLDERS
-- Self-referencing tree structure: parent_id NULL = root folder for that user
-- ============================================================
CREATE TABLE IF NOT EXISTS folders (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    owner_id        UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    parent_id       UUID REFERENCES folders(id) ON DELETE CASCADE,
    name            VARCHAR(255) NOT NULL,
    is_trashed      BOOLEAN NOT NULL DEFAULT FALSE,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now(),

    -- Two folders with the same name can't sit in the same parent folder
    CONSTRAINT unique_folder_name_per_parent UNIQUE (owner_id, parent_id, name)
);

-- ============================================================
-- FILES
-- stored_filename = the UUID-based name on disk (never trust user input as a filesystem path)
-- original_filename = what the user sees in the UI
-- ============================================================
CREATE TABLE IF NOT EXISTS files (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    owner_id        UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    folder_id       UUID REFERENCES folders(id) ON DELETE CASCADE, -- NULL = lives in root
    original_filename VARCHAR(255) NOT NULL,
    stored_filename   VARCHAR(255) NOT NULL UNIQUE, -- e.g. <uuid>.bin on disk
    size_bytes      BIGINT NOT NULL,
    mime_type       VARCHAR(100),
    is_trashed      BOOLEAN NOT NULL DEFAULT FALSE,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- ============================================================
-- Helpful indexes for the queries we'll run constantly:
-- "list everything in folder X for user Y", searches, trash view
-- ============================================================
CREATE INDEX IF NOT EXISTS idx_folders_owner_parent ON folders(owner_id, parent_id);
CREATE INDEX IF NOT EXISTS idx_files_owner_folder ON files(owner_id, folder_id);
CREATE INDEX IF NOT EXISTS idx_files_owner_trashed ON files(owner_id, is_trashed);
CREATE INDEX IF NOT EXISTS idx_folders_owner_trashed ON folders(owner_id, is_trashed);

-- Case-insensitive filename search (used by the search bar)
CREATE INDEX IF NOT EXISTS idx_files_filename_search ON files USING gin (original_filename gin_trgm_ops);
