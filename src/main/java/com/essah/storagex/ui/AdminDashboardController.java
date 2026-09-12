package com.essah.storagex.ui;

import com.essah.storagex.App;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;

import java.io.IOException;

/** Controller for the placeholder administrator dashboard. */
public class AdminDashboardController {

    @FXML
    private Label statusLabel;

    @FXML
    private void handleLogout() {
        try {
            Parent loginView = FXMLLoader.load(App.class.getResource("/fxml/login.fxml"));
            statusLabel.getScene().setRoot(loginView);
        } catch (IOException e) {
            statusLabel.setText("Could not return to the login screen.");
            e.printStackTrace();
        }
    }
}
