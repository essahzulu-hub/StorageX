package com.essah.storagex.ui;

import com.essah.storagex.App;
import com.essah.storagex.model.User;
import com.essah.storagex.util.AuthService;
import com.essah.storagex.util.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;

/**
 * Controller for login.fxml.
 *
 * NOTE: This is a checkpoint stub. Login doesn't actually authenticate yet -
 * that needs AuthService + UserDao, which we're building next. For now this
 * just proves the FXML/UI wiring works end to end.
 */
public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label statusLabel;

    @FXML
    private Button loginButton;

    @FXML
    private Button goToRegisterButton;

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isBlank() || password.isBlank()) {
            statusLabel.setText("Enter a username and password.");
            return;
        }

        try {
            User user = new AuthService().login(username.trim(), password);
            SessionManager.signIn(user);
            Parent dashboard = FXMLLoader.load(App.class.getResource("/fxml/dashboard.fxml"));
            statusLabel.getScene().setRoot(dashboard);
        } catch (AuthService.AuthException e) {
            statusLabel.setText(e.getMessage());
            passwordField.clear();
        } catch (IOException e) {
            statusLabel.setText("Could not open your dashboard.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleGoToRegister() {
        try {
            Parent registerView = FXMLLoader.load(App.class.getResource("/fxml/register.fxml"));
            statusLabel.getScene().setRoot(registerView);
        } catch (IOException e) {
            statusLabel.setText("Could not open the registration screen.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleGoToAdminLogin() {
        try {
            Parent adminLoginView = FXMLLoader.load(App.class.getResource("/fxml/admin-login.fxml"));
            statusLabel.getScene().setRoot(adminLoginView);
        } catch (IOException e) {
            statusLabel.setText("Could not open the admin login screen.");
            e.printStackTrace();
        }
    }
}
