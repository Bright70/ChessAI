package chessai;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class GUIMain extends Application{

    StackPane layout = new StackPane();
    Scene scene = new Scene(layout, 800, 600);

    Button undo = new Button("Undo");

    public static void main(String args[]){
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("ChessAI");
        layout.getChildren().add(undo);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
