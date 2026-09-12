package com.essah.storagex.util;

import com.essah.storagex.model.User;

/** Holds the signed-in user for this single-window desktop application. */
public final class SessionManager {
    private static User currentUser;

    private SessionManager() {
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void signIn(User user) {
        currentUser = user;
    }

    public static void signOut() {
        currentUser = null;
    }
}
