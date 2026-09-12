package com.essah.storagex.ui;

import com.essah.storagex.App;
import com.essah.storagex.model.FileItem;
import com.essah.storagex.model.Folder;
import com.essah.storagex.model.User;
import com.essah.storagex.util.FileItemDao;
import com.essah.storagex.util.FolderDao;
import com.essah.storagex.util.SessionManager;
import com.essah.storagex.util.StorageService;
import com.essah.storagex.util.UserDao;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextInputDialog;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

/** Workspace for a signed-in user. */
public class DashboardController {
    @FXML private Label welcomeLabel;
    @FXML private Label quotaLabel;
    @FXML private Label pathLabel;
    @FXML private Label statusLabel;
    @FXML private ListView<Folder> folderList;
    @FXML private ListView<FileItem> fileList;

    private final FolderDao folderDao = new FolderDao();
    private final FileItemDao fileItemDao = new FileItemDao();
    private final UserDao userDao = new UserDao();
    private final StorageService storageService = new StorageService();
    private UUID currentFolderId;
    private String currentPath = "My Files";

    @FXML
    private void initialize() {
        User user = SessionManager.getCurrentUser();
        if (user == null) {
            return;
        }
        welcomeLabel.setText("Welcome, " + user.getUsername());
        refresh();
    }

    @FXML
    private void handleRefresh() {
        refresh();
    }

    @FXML
    private void handleNewFolder() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("New Folder");
        dialog.setHeaderText("Create a folder in " + currentPath);
        dialog.setContentText("Folder name:");
        Optional<String> result = dialog.showAndWait();
        if (result.isEmpty()) return;
        String name = result.get().trim();
        if (name.isBlank() || name.length() > 255 || name.contains("/")) {
            statusLabel.setText("Enter a folder name without '/'.");
            return;
        }
        try {
            folderDao.create(user().getId(), currentFolderId, name);
            statusLabel.setText("Folder created.");
            refresh();
        } catch (SQLException e) {
            statusLabel.setText("Could not create folder. A folder with that name may already exist.");
        }
    }

    @FXML
    private void handleOpenFolder() {
        Folder folder = folderList.getSelectionModel().getSelectedItem();
        if (folder == null) {
            statusLabel.setText("Select a folder first.");
            return;
        }
        currentFolderId = folder.getId();
        currentPath += " / " + folder.getName();
        refresh();
    }

    @FXML
    private void handleGoToRoot() {
        currentFolderId = null;
        currentPath = "My Files";
        refresh();
    }

    @FXML
    private void handleUpload() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Choose a file to upload");
        java.io.File selected = chooser.showOpenDialog(folderList.getScene().getWindow());
        if (selected == null) return;
        try {
            storageService.upload(user(), currentFolderId, selected.toPath());
            statusLabel.setText("Uploaded " + selected.getName() + ".");
            refresh();
        } catch (StorageService.StorageException e) {
            statusLabel.setText(e.getMessage());
        }
    }

    @FXML
    private void handleDownload() {
        FileItem file = fileList.getSelectionModel().getSelectedItem();
        if (file == null) {
            statusLabel.setText("Select a file first.");
            return;
        }
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Choose where to save " + file.getOriginalFilename());
        java.io.File selected = chooser.showDialog(fileList.getScene().getWindow());
        if (selected == null) return;
        try {
            storageService.download(file, Path.of(selected.toURI()));
            statusLabel.setText("Downloaded " + file.getOriginalFilename() + ".");
        } catch (StorageService.StorageException e) {
            statusLabel.setText(e.getMessage());
        }
    }

    @FXML
    private void handleLogout() {
        SessionManager.signOut();
        try {
            Parent loginView = FXMLLoader.load(App.class.getResource("/fxml/login.fxml"));
            statusLabel.getScene().setRoot(loginView);
        } catch (IOException e) {
            statusLabel.setText("Could not return to the login screen.");
        }
    }

    private void refresh() {
        try {
            User refreshedUser = userDao.findById(user().getId()).orElseThrow();
            SessionManager.signIn(refreshedUser);
            quotaLabel.setText(String.format("Storage: %.1f%% used (%s / %s)", refreshedUser.getUsagePercent(),
                    readableSize(refreshedUser.getStorageUsedBytes()), readableSize(refreshedUser.getStorageQuotaBytes())));
            pathLabel.setText(currentPath);
            folderList.setItems(FXCollections.observableArrayList(folderDao.list(refreshedUser.getId(), currentFolderId)));
            fileList.setItems(FXCollections.observableArrayList(fileItemDao.list(refreshedUser.getId(), currentFolderId)));
            statusLabel.setText("");
        } catch (SQLException | java.util.NoSuchElementException e) {
            statusLabel.setText("Could not load your files. Check the database connection.");
        }
    }

    private User user() {
        return SessionManager.getCurrentUser();
    }

    private String readableSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        return String.format("%.1f %sB", bytes / Math.pow(1024, exp), "KMGTPE".charAt(exp - 1));
    }
}
