package com.essah.storagex.util;

import com.essah.storagex.model.User;
import org.mindrot.jbcrypt.BCrypt;
import java.sql.SQLException;

public class AuthService {
    private final UserDao users = new UserDao();
    public User register(String username, String email, String password) throws AuthException {
        try { return users.create(username, email, BCrypt.hashpw(password, BCrypt.gensalt())); }
        catch (SQLException e) {
            if ("23505".equals(e.getSQLState())) throw new AuthException("That username or email address is already registered.");
            throw new AuthException("Could not create your account. Check the database connection.", e);
        }
    }
    public User login(String username, String password) throws AuthException {
        try {
            User user = users.findByUsername(username).orElseThrow(() -> new AuthException("Invalid username or password."));
            if (!BCrypt.checkpw(password, user.getPasswordHash())) throw new AuthException("Invalid username or password.");
            return user;
        } catch (IllegalArgumentException e) { throw new AuthException("Invalid username or password."); }
        catch (SQLException e) { throw new AuthException("Could not sign in. Check the database connection.", e); }
    }
    public static class AuthException extends Exception {
        public AuthException(String message) { super(message); }
        public AuthException(String message, Throwable cause) { super(message, cause); }
    }
}
