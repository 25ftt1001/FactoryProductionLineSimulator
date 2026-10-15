import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.net.URL;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        URL fxmlLocation = getClass().getResource("/views/login.fxml");
        Parent root = FXMLLoader.load(fxmlLocation);
        
        // Get the screen's visual bounds (accounts for taskbar)
        javafx.geometry.Rectangle2D bounds = javafx.stage.Screen.getPrimary().getVisualBounds();
        
        // Create the scene with the screen's width and height
        Scene scene = new Scene(root, bounds.getWidth(), bounds.getHeight());
        
        URL cssLocation = getClass().getResource("/css/styles.css");
        if (cssLocation != null) {
            scene.getStylesheets().add(cssLocation.toExternalForm());
        }
        
        primaryStage.setTitle("Factory Control System");
        primaryStage.initStyle(StageStyle.UNDECORATED); 
        primaryStage.setScene(scene);
        
        // Set the window to fill the screen
        primaryStage.setX(bounds.getMinX());
        primaryStage.setY(bounds.getMinY());
        primaryStage.setWidth(bounds.getWidth());
        primaryStage.setHeight(bounds.getHeight());
        
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}