package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class CreateAccountController {

    @FXML private TextField fullNameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;

    // ⚠️ REPLACE THIS WITH YOUR FIREBASE WEB API KEY
    private static final String FIREBASE_API_KEY = "AIzaSyDr6A01zbMJuJ6UBYEtzUVuJijEApssn8A";

    @FXML
    private void handleRegister() {
        String fullName = fullNameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        // Validation
        if (fullName.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showAlert("Error", "Please fill in all fields.");
            return;
        }
        if (!password.equals(confirmPassword)) {
            showAlert("Error", "Passwords do not match.");
            return;
        }
        if (password.length() < 6) {
            showAlert("Error", "Password must be at least 6 characters long.");
            return;
        }

        // Register with Firebase
        try {
            String jsonBody = String.format(
                "{\"email\":\"%s\",\"password\":\"%s\",\"returnSecureToken\":true}",
                email, password
            );

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://identitytoolkit.googleapis.com/v1/accounts:signUp?key=" + FIREBASE_API_KEY))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("Firebase Response: " + response.body());

            if (response.statusCode() == 200) {
                System.out.println("User registered successfully!");
                showAlert("Success", "Account created successfully! You can now log in.");

                fullNameField.clear();
                emailField.clear();
                passwordField.clear();
                confirmPasswordField.clear();
            } else {
                String errorBody = response.body();
                String errorMsg = "Registration failed.";
                if (errorBody.contains("EMAIL_EXISTS")) {
                    errorMsg = "This email is already registered.";
                } else if (errorBody.contains("INVALID_EMAIL")) {
                    errorMsg = "Please enter a valid email address.";
                } else if (errorBody.contains("WEAK_PASSWORD")) {
                    errorMsg = "Password is too weak (min 6 characters).";
                }
                showAlert("Registration Failed", errorMsg);
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            showAlert("Error", "Connection error: " + e.getMessage());
        }
    }

    @FXML
    private void handleSignIn(ActionEvent event) {
        Navigation.switchScene(event, "/views/login_page.fxml");
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}