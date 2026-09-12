package com.essah.storagex.util;

import io.github.cdimascio.dotenv.Dotenv;
import org.mindrot.jbcrypt.BCrypt;

/** Verifies the separately configured administrator account. */
public final class AdminAuthService {

    private static final Dotenv DOTENV = Dotenv.configure().ignoreIfMissing().load();
    // Development defaults. Override both values in .env before distributing the app.
    private static final String DEFAULT_ADMIN_USERNAME = "essahzulu-hub";
    private static final String DEFAULT_ADMIN_PASSWORD_HASH =
            "$2b$12$KEP0kaiPm5NoLR6iycr4VeOqA1hl2hEej7bpWsKgMxNEvJasgAMsu";

    private AdminAuthService() {
    }

    /**
     * Returns true only when the supplied credentials match the configured
     * administrator username and BCrypt password hash.
     */
    public static boolean authenticate(String username, String password) {
        String adminUsername = DOTENV.get("ADMIN_USERNAME", DEFAULT_ADMIN_USERNAME);
        String passwordHash = DOTENV.get("ADMIN_PASSWORD_HASH", DEFAULT_ADMIN_PASSWORD_HASH);

        try {
            return adminUsername.equals(username) && BCrypt.checkpw(password, passwordHash);
        } catch (IllegalArgumentException e) {
            return false; // Invalid BCrypt configuration should not reveal details to an attacker.
        }
    }

    public static boolean isConfigured() {
        return true;
    }
}
