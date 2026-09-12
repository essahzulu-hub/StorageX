package com.essah.storagex.util;

import com.essah.storagex.model.FileItem;
import com.essah.storagex.model.User;
import io.github.cdimascio.dotenv.Dotenv;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

/** Copies files to local storage and keeps their database metadata in sync. */
public class StorageService {
    private static final Path STORAGE_ROOT = Path.of(Dotenv.configure().ignoreIfMissing().load()
            .get("STORAGE_ROOT", "./storage-data")).toAbsolutePath().normalize();
    private final FileItemDao fileItemDao = new FileItemDao();

    public void upload(User user, UUID folderId, Path source) throws StorageException {
        try {
            if (!Files.isRegularFile(source)) {
                throw new StorageException("Choose a regular file to upload.");
            }
            long size = Files.size(source);
            String storedFilename = UUID.randomUUID() + ".bin";
            Path destination = STORAGE_ROOT.resolve(storedFilename);
            Files.createDirectories(STORAGE_ROOT);
            Files.copy(source, destination, StandardCopyOption.COPY_ATTRIBUTES);

            try (Connection connection = DatabaseManager.getConnection()) {
                connection.setAutoCommit(false);
                try {
                    String quotaSql = "UPDATE users SET storage_used_bytes = storage_used_bytes + ? " +
                            "WHERE id = ? AND storage_used_bytes + ? <= storage_quota_bytes";
                    try (PreparedStatement statement = connection.prepareStatement(quotaSql)) {
                        statement.setLong(1, size);
                        statement.setObject(2, user.getId());
                        statement.setLong(3, size);
                        if (statement.executeUpdate() == 0) {
                            throw new StorageException("Upload would exceed your storage quota.");
                        }
                    }
                    String mimeType = Files.probeContentType(source);
                    fileItemDao.create(connection, user.getId(), folderId, source.getFileName().toString(),
                            storedFilename, size, mimeType);
                    connection.commit();
                } catch (SQLException | StorageException e) {
                    connection.rollback();
                    Files.deleteIfExists(destination);
                    throw e;
                }
            }
        } catch (IOException e) {
            throw new StorageException("Could not copy the selected file.", e);
        } catch (SQLException e) {
            throw new StorageException("Could not save file metadata.", e);
        }
    }

    public void download(FileItem file, Path destinationDirectory) throws StorageException {
        try {
            if (!Files.isDirectory(destinationDirectory)) {
                throw new StorageException("Choose a valid destination folder.");
            }
            Path source = STORAGE_ROOT.resolve(file.getStoredFilename()).normalize();
            if (!source.startsWith(STORAGE_ROOT) || !Files.isRegularFile(source)) {
                throw new StorageException("The stored file could not be found.");
            }
            Path destination = destinationDirectory.resolve(file.getOriginalFilename()).normalize();
            if (!destination.startsWith(destinationDirectory.toAbsolutePath().normalize())) {
                throw new StorageException("Invalid destination filename.");
            }
            Files.copy(source, destination);
        } catch (IOException e) {
            throw new StorageException("Could not download the file. It may already exist in that folder.", e);
        }
    }

    public static class StorageException extends Exception {
        public StorageException(String message) {
            super(message);
        }

        public StorageException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
