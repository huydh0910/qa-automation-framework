package com.automation.utils;

/**
 * Stores per-thread API tokens so parallel API tests never share a token
 * across threads.
 */
public class TokenManager {

    private static final ThreadLocal<String> tokenStore = new ThreadLocal<>();

    private TokenManager() {}

    public static void setToken(String token) {
        tokenStore.set(token);
    }

    public static String getToken() {
        String token = tokenStore.get();
        if (token == null) {
            throw new IllegalStateException("No token stored for this thread. Call setToken() first.");
        }
        return token;
    }

    public static void clearToken() {
        tokenStore.remove();
    }
}
