package controllers;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class DashboardApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Initialize the main dashboard layout
        DashboardView dashboard = new DashboardView();
        
        // Create the scene with the dashboard's root layout
        Scene scene = new Scene(dashboard.getRoot(), 520, 850);
        
        primaryStage.setTitle("Factory Control App");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}