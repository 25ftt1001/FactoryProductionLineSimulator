package controllers;

public class UserSession {
    public static String email = "";
    public static String displayName = "";
    public static String idToken = "";
    public static String localId = "";

    public static void clear() {
        email = "";
        displayName = "";
        idToken = "";
        localId = "";
    }
}