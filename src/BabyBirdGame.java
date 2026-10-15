import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class BabyBirdGame extends Application {

    @Override
    public void start(Stage arg0) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Main.fxml"));
        Parent root = loader.load();    
        arg0.setScene(new Scene(root));
        arg0.setTitle("Baby Bird");
        arg0.setResizable(false);
        arg0.sizeToScene();
        arg0.show();
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
    
}