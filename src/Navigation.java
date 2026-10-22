package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;

public class Navigation {

    public static void switchScene(ActionEvent event, String fxmlPath) {
        try {
            URL fxmlLocation = Navigation.class.getResource(fxmlPath);
            
            if (fxmlLocation == null) {
                System.out.println("ERROR: Could not find " + fxmlPath);
                return;
            }

            FXMLLoader loader = new FXMLLoader(fxmlLocation);
            Parent root = loader.load();
            
            Stage stage;
            if (event != null) {
                stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            } else {
                // Fallback: use any open window
                stage = (Stage) javafx.stage.Window.getWindows().stream()
                        .filter(javafx.stage.Window::isShowing)
                        .findFirst()
                        .orElse(null);
                if (stage == null) {
                    System.out.println("ERROR: No active window.");
                    return;
                }
            }
            
            Scene scene = new Scene(root);
            
            URL cssLocation = Navigation.class.getResource("/css/styles.css");
            if (cssLocation != null) {
                scene.getStylesheets().add(cssLocation.toExternalForm());
            }
            
            stage.setScene(scene);
            stage.show();
            
        } catch (IOException e) {
            System.out.println("ERROR loading scene: " + e.getMessage());
            e.printStackTrace();
        }
    }
}