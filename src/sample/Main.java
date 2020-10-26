package sample;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        BoardFX boardFX = new BoardFX(new Board(6));

        Scene scene = new Scene(boardFX.root, 1300, 1000);
        scene.setFill(Color.rgb(90, 120, 70));


        primaryStage.setTitle("LOA");
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
