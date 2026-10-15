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

                // ⭐ SWITCH TO DASHBOARD VIEW ⭐
                DashboardView dashboard = new DashboardView();
                Scene dashboardScene = new Scene(dashboard.getRoot(), 520, 850);

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

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}