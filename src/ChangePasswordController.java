// AIzaSyDr6A01zbMJuJ6UBYEtzUVuJijEApssn8A

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

public class ChangePasswordController {

    @FXML private TextField emailField;
    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmPasswordField;

    // ⚠️ REPLACE THIS WITH YOUR FIREBASE WEB API KEY
    private static final String FIREBASE_API_KEY = "AIzaSyDr6A01zbMJuJ6UBYEtzUVuJijEApssn8A";

    @FXML
    private void handleUpdatePassword() {
        String email = emailField.getText().trim();
        String newPwd = newPasswordField.getText();
        String confirmPwd = confirmPasswordField.getText();

        // Validation
        if (email.isEmpty() || newPwd.isEmpty() || confirmPwd.isEmpty()) {
            showAlert("Error", "Please fill in all fields.");
            return;
        }
        if (!newPwd.equals(confirmPwd)) {
            showAlert("Error", "Passwords do not match.");
            return;
        }
        if (!isValidPassword(newPwd)) {
            showAlert("Error", "Password must be at least 8 characters, contain 1 number, and 1 uppercase letter.");
            return;
        }

        // Firebase REST API doesn't allow changing password without being logged in,
        // so we send a password reset email to the user's registered address.
        try {
            String jsonBody = String.format("{\"requestType\":\"PASSWORD_RESET\",\"email\":\"%s\"}", email);

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://identitytoolkit.googleapis.com/v1/accounts:sendOobCode?key=" + FIREBASE_API_KEY))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Firebase Response: " + response.body());

            if (response.statusCode() == 200) {
                showAlert("Success", "A password reset link has been sent to " + email + ".\nPlease check your inbox.");
                
                // Navigate back to Login Page
                Navigation.switchScene(null, "/views/login_page.fxml");
                
            } else {
                String errorMsg = "Could not send reset email.";
                if (response.body().contains("EMAIL_NOT_FOUND")) {
                    errorMsg = "No account found with this email.";
                }
                showAlert("Failed", errorMsg);
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            showAlert("Error", "Connection error: " + e.getMessage());
        }
    }

    @FXML
    private void handleBack(ActionEvent event) {
        Navigation.switchScene(event, "/views/login_page.fxml");
    }

    private boolean isValidPassword(String password) {
        if (password.length() < 8) return false;
        boolean hasNumber = password.matches(".*\\d.*");
        boolean hasUpper = password.matches(".*[A-Z].*");
        return hasNumber && hasUpper;
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}