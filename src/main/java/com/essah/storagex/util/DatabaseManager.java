package com.essah.storagex.util;

import io.github.cdimascio.dotenv.Dotenv;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Handles all the plumbing for getting a connection to Postgres.
 *
 * Config comes from a .env file at the project root (see .env.example).
 * We don't hardcode credentials here - keeps secrets out of source control.
 */
public class DatabaseManager {

    private static final Dotenv dotenv = Dotenv.configure()
            .ignoreIfMissing() // fall back to defaults below if .env isn't there yet
            .load();

    private static final String DB_HOST = dotenv.get("DB_HOST", "localhost");
    private static final String DB_PORT = dotenv.get("DB_PORT", "5432");
    private static final String DB_NAME = dotenv.get("DB_NAME", "storagex");
    private static final String DB_USER = dotenv.get("DB_USER", "postgres");
    private static final String DB_PASSWORD = dotenv.get("DB_PASSWORD", "postgres");

    private static final String URL =
            "jdbc:postgresql://" + DB_HOST + ":" + DB_PORT + "/" + DB_NAME;

    /**
     * Opens a fresh connection. Caller is responsible for closing it
     * (use try-with-resources everywhere we call this).
     */
    public static Connection getConnection() throws SQLException {
        try {
            return DriverManager.getConnection(URL, DB_USER, DB_PASSWORD);
        } catch (SQLException e) {
            // Wrap with a clearer message - this is the #1 thing that'll break
            // for someone setting the project up fresh, so make it obvious.
            throw new SQLException(
                    "Could not connect to Postgres at " + URL + " as user '" + DB_USER + "'. " +
                    "Check that: (1) Postgres is running, (2) the database '" + DB_NAME +
                    "' exists (create it with: createdb " + DB_NAME + "), " +
                    "(3) your .env credentials are correct, " +
                    "(4) you've run schema.sql against the database.",
                    e
            );
        }
    }

    /** Quick sanity check you can call at startup to fail fast with a clear message. */
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn.isValid(3);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }
}
