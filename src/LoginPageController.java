package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class LoginPageController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;

    // ⚠️ REPLACE THIS WITH YOUR FIREBASE WEB API KEY
    private static final String FIREBASE_API_KEY = "AIzaSyDr6A01zbMJuJ6UBYEtzUVuJijEApssn8A";

    @FXML
    private void handleAccessSystem(ActionEvent event) {
        String email = emailField.getText().trim();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            showAlert("Error", "Please enter your email and password.");
            return;
        }

        try {
            String jsonBody = String.format(
                "{\"email\":\"%s\",\"password\":\"%s\",\"returnSecureToken\":true}",
                email, password
            );

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=" + FIREBASE_API_KEY))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("Firebase Response: " + response.body());

            if (response.statusCode() == 200) {
                System.out.println("Login successful! Loading Dashboard...");

                // --- Extract user info from Firebase JSON response ---
                String body = response.body();
                UserSession.email       = extractJson(body, "email");
                UserSession.displayName = extractJson(body, "displayName");
                UserSession.idToken     = extractJson(body, "idToken");
                UserSession.localId     = extractJson(body, "localId");

                // If displayName is empty, derive a name from the email
                if (UserSession.displayName == null || UserSession.displayName.isEmpty()) {
                    // Firebase doesn't return displayName for email/password accounts
                    // unless it was set during signup. Derive a fallback.
                    String prefix = email.split("@")[0];
                    UserSession.displayName = capitalize(prefix);
                }

                System.out.println("Logged in as: " + UserSession.displayName + " (" + UserSession.email + ")");

                // --- Switch to Dashboard (which now starts on HOME) ---
                DashboardView dashboard = new DashboardView();
                Scene dashboardScene = new Scene(dashboard.getRoot(), 850, 650);

                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(dashboardScene);
                stage.setTitle("Factory Control App");
                stage.show();

            } else {
                String errorBody = response.body();
                String errorMsg = "Invalid email or password.";
                if (errorBody.contains("EMAIL_NOT_FOUND")) {
                    errorMsg = "No account found with this email.";
                } else if (errorBody.contains("INVALID_PASSWORD")) {
                    errorMsg = "Incorrect password.";
                } else if (errorBody.contains("INVALID_LOGIN_CREDENTIALS")) {
                    errorMsg = "Invalid email or password.";
                }
                showAlert("Login Failed", errorMsg);
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            showAlert("Error", "Connection error: " + e.getMessage());
        }
    }

    @FXML
    private void handleRegister(ActionEvent event) {
        Navigation.switchScene(event, "/views/create_account.fxml");
    }

    @FXML
    private void handleForgotPassword(ActionEvent event) {
        Navigation.switchScene(event, "/views/change_password.fxml");
    }

    // --- Helpers ---
    private String extractJson(String json, String key) {
        // Simple JSON string extractor. Looks for "key":"value" pattern.
        String search = "\"" + key + "\":\"";
        int start = json.indexOf(search);
        if (start == -1) {
            // Try with space after colon
            search = "\"" + key + "\": \"";
            start = json.indexOf(search);
            if (start == -1) return "";
        }
        start += search.length();
        int end = json.indexOf("\"", start);
        if (end == -1) return "";
        return json.substring(start, end);
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}