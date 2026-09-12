package com.essah.storagex.ui;

import com.essah.storagex.App;
import com.essah.storagex.model.User;
import com.essah.storagex.util.AuthService;
import com.essah.storagex.util.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;

/** Controller for the registration screen. */
public class RegisterController {

    @FXML
    private TextField usernameField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private Label statusLabel;

    @FXML
    private void handleRegister() {
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (username.isBlank() || email.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
            statusLabel.setText("Complete every field.");
            return;
        }
        if (username.length() < 3) {
            statusLabel.setText("Username must be at least 3 characters.");
            return;
        }
        if (!email.contains("@")) {
            statusLabel.setText("Enter a valid email address.");
            return;
        }
        if (password.length() < 8) {
            statusLabel.setText("Password must be at least 8 characters.");
            return;
        }
        if (!password.equals(confirmPassword)) {
            statusLabel.setText("Passwords do not match.");
            return;
        }

        try {
            User user = new AuthService().register(username, email, password);
            SessionManager.signIn(user);
            Parent dashboard = FXMLLoader.load(App.class.getResource("/fxml/dashboard.fxml"));
            statusLabel.getScene().setRoot(dashboard);
        } catch (AuthService.AuthException e) {
            statusLabel.setText(e.getMessage());
        } catch (IOException e) {
            statusLabel.setText("Could not open your dashboard.");
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
