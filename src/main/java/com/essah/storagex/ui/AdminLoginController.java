package com.essah.storagex.ui;

import com.essah.storagex.App;
import com.essah.storagex.util.AdminAuthService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;

/** Controller for the separate administrator sign-in screen. */
public class AdminLoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label statusLabel;

    @FXML
    private void handleAdminLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (username.isBlank() || password.isBlank()) {
            statusLabel.setText("Enter an administrator username and password.");
            return;
        }
        if (!AdminAuthService.isConfigured()) {
            statusLabel.setText("Admin credentials have not been configured.");
            return;
        }
        if (!AdminAuthService.authenticate(username, password)) {
            statusLabel.setText("Invalid administrator credentials.");
            passwordField.clear();
            return;
        }

        try {
            Parent dashboard = FXMLLoader.load(App.class.getResource("/fxml/admin-dashboard.fxml"));
            statusLabel.getScene().setRoot(dashboard);
        } catch (IOException e) {
            statusLabel.setText("Could not open the admin dashboard.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleBackToLogin() {
        try {
            Parent loginView = FXMLLoader.load(App.class.getResource("/fxml/login.fxml"));
            statusLabel.getScene().setRoot(loginView);
        } catch (IOException e) {
            statusLabel.setText("Could not return to the login screen.");
            e.printStackTrace();
        }
    }
}
