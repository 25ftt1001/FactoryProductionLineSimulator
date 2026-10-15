import java.util.Random;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class App extends Application {

    private String[] quotes = {
        "Jangan stress",
        "Wafiiqah balikan aku matcha",
        "bunda belanja aku otacos",
        "ums belanja aku coffee beans",
    };

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage arg0) throws Exception {
        Random rand = new Random();
        VBox root = new VBox();

        Font font = Font.font("Times New Roman", FontWeight.BOLD, FontPosture.ITALIC, 32);
        Label label = new Label(quotes[rand.nextInt(quotes.length)]);
        label.setFont(font);

        Button button = new Button("Change Quote");
        button.setFont(font);
        button.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent arg0) {
                System.out.println(quotes[rand.nextInt(quotes.length)]);
            }
            
        });
        root.getChildren().addAll(label, button);

        Scene scene = new Scene(root, 600, 400);

        arg0.setScene(scene);
        arg0.show();
    }
}