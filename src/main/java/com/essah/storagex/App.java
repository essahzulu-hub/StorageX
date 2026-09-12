package com.essah.storagex;

import com.essah.storagex.util.DatabaseManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Entry point for StorageX. Loads the login screen first.
 */
public class App extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        // Fail fast with a clear message if Postgres isn't reachable,
        // rather than letting the user click around and hit confusing errors later.
        if (!DatabaseManager.testConnection()) {
            System.err.println("=".repeat(60));
            System.err.println("WARNING: Could not connect to the database.");
            System.err.println("The app will still launch, but nothing that touches");
            System.err.println("the database will work until this is fixed.");
            System.err.println("See the error above for details.");
            System.err.println("=".repeat(60));
        }

        FXMLLoader loader = new FXMLLoader(App.class.getResource("/fxml/login.fxml"));
        Parent root = loader.load();

        stage.setTitle("StorageX — Your files, beautifully organized");
        Scene scene = new Scene(root, 980, 680);
        scene.getStylesheets().add(App.class.getResource("/styles/app.css").toExternalForm());
        stage.setScene(scene);
        stage.setMinWidth(700);
        stage.setMinHeight(450);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
