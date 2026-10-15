import java.util.Random;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class GuessMyColour extends App {

    private Rectangle guessRectangle = new Rectangle();
    private Rectangle answerRectangle = new Rectangle();

    private int guessRed = 0;
    private int guessGreen = 0;
    private int guessBlue = 0;
    private int answerRed = 0;
    private int answerGreen = 0;
    private int answerBlue = 0;


    public void initGUI(Stage arg0) {
        BorderPane rootPane = new BorderPane();

        Label titleLabel = new Label("Guess My Colour");
        Font titleFont = Font.font("Serif", FontWeight.EXTRA_BOLD, 32);
        titleLabel.setFont(titleFont);
        titleLabel.setBackground(new Background(
            new BackgroundFill(Color.BROWN, null,null)
        ));

        titleLabel.setTextFill(Color.AZURE);
        titleLabel.prefWidthProperty().bind(rootPane.widthProperty()); 
        titleLabel.setAlignment(Pos.CENTER);
        titleLabel.setPadding(new Insets(10));

        rootPane.setTop(titleLabel);

        guessRectangle.setWidth(50);
        guessRectangle.setHeight(50);
        guessRectangle.setFill(Color.ANTIQUEWHITE);

        answerRectangle.setWidth(50);
        answerRectangle.setHeight(50);
        guessRectangle.setFill(Color.AQUAMARINE);
        

        FlowPane centerPane = new FlowPane();
        centerPane.getChildren().addAll(guessRectangle, answerRectangle);
        centerPane.setAlignment(Pos.CENTER);
        centerPane.setHgap(5);
        centerPane.setVgap(5);

        rootPane.setCenter(centerPane);

        generateGuessColour();
        HBox buttonPane = new HBox();

        Font font = Font.font("Dialog", FontWeight.BOLD, 18);
        Background redBackground = new Background(new BackgroundFill(Color.RED,null, null));
        Background blueBackground = new Background(new BackgroundFill(Color.BLUE,null, null));
        Background greenBackground = new Background(new BackgroundFill(Color.GREEN,null, null));
        


        Button moreRedButton = new Button("+");
        moreRedButton.setFont(font);
        moreRedButton.setBackground(redBackground);
        moreRedButton.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent arg0) {
                if(answerRed <=240){
                    answerRed = answerRed + 15;
                    updateAnswerRectangle();
                }
                
            }
            
        });

        Button lessRedButton = new Button("-");
        lessRedButton.setFont(font);
        lessRedButton.setBackground(redBackground);
        lessRedButton.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent arg0) {
                if(answerRed <=240){
                    answerRed = answerRed + 15;
                    updateAnswerRectangle();
                }
                
            }
            
        });
        

        Button moreGreenButton = new Button("+");
         moreGreenButton.setFont(font);
        moreGreenButton.setBackground(greenBackground);
        moreGreenButton.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent arg0) {
                if(answerGreen <=240){
                    answerGreen = answerGreen + 15;
                    updateAnswerRectangle();
                }
                
            }
            
        });

        Button lessGreenButton = new Button("-");
        lessGreenButton.setFont(font);
        lessGreenButton.setBackground(greenBackground);
        lessGreenButton.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent arg0) {
                if(answerGreen <=240){
                    answerGreen = answerGreen + 15;
                    updateAnswerRectangle();
                }
                
            }
            
        });

        Button moreBlueButton = new Button("+");
         moreBlueButton.setFont(font);
        moreBlueButton.setBackground(blueBackground);
        moreBlueButton.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent arg0) {
                if(answerBlue <=240){
                    answerBlue = answerBlue + 15;
                    updateAnswerRectangle();
                }
                
            }
            
        });

        Button lessBlueButton = new Button("-");
        lessBlueButton.setFont(font);
        lessBlueButton.setBackground(blueBackground);
        moreBlueButton.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent arg0) {
                if(answerBlue <=240){
                    answerBlue = answerBlue + 15;
                    updateAnswerRectangle();
                }
                
            }
            
        });

        buttonPane.getChildren().addAll(
            moreRedButton,lessRedButton,moreGreenButton,lessGreenButton,moreBlueButton,lessBlueButton
        );
        buttonPane.setAlignment(Pos.CENTER);
        buttonPane.setSpacing(10);
        buttonPane.setBackground(new Background(new BackgroundFill(Color.DARKGREY,null,null)));
        

        buttonPane.setPadding(new Insets(10));
        rootPane.setBottom(buttonPane);

        Scene root = new Scene(rootPane);
        arg0.setScene(root);
    }
    public void updateAnswerRectangle(){
        Color color = Color.rgb(answerRed,answerGreen,answerBlue);
        answerRectangle.setFill(color);
        System.out.println("Guess: " + guessRed + "r " + guessGreen + "g " + guessBlue + "g ");
        System.out.println("Answer: " + answerRed + "r " + answerGreen + "g " + answerBlue + "g ");
        if(guessRed == answerRed && 
            guessGreen == answerGreen && 
            guessBlue == answerBlue
        ) {
            String message = "Congratulation! You guessed it right!";
            //System.out.println(message);
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("You did it!");
            alert.setHeaderText("Play Again?");
            alert.setContentText(message);
           // alert.showAndWait();
           ButtonData buttonData = alert.showAndWait().get().getButtonData();
           if(buttonData == ButtonData.OK_DONE) {
            generateGuessColour();
           } else {
            System.exit(0);
           }
        }
    }

    public void generateGuessColour() {
        Random rand = new Random();
        guessRed = rand.nextInt(18)*15;
        guessGreen = rand.nextInt(18)*15;
        guessBlue = rand.nextInt(18)*15;
        Color guessColor = Color.rgb(guessRed, guessGreen, guessBlue);
        guessRectangle.setFill(guessColor);

        answerRed = rand.nextInt(18)*15;
        answerGreen = rand.nextInt(18)*15;
        answerBlue = rand.nextInt(18)*15;
        Color answerColor = Color.rgb(answerRed, answerGreen, answerBlue);
        answerRectangle.setFill(answerColor);
    }
    public static void main(String[] args) {
        App.launch(args);
    }

    @Override 
    public void start (Stage arg0) throws Exception {
        initGUI(arg0);

        arg0.setTitle("Guess My Colour");
        arg0.setResizable(false);
        arg0.sizeToScene(); 
        arg0.show();
    }
    
}