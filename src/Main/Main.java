import Mechanics.Board;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import FX.*;

public class Main extends Application {
    final int NUMBER_OF_SCENES = 4;
    final int CHOOSE_TYPE = 0; //single player of multiplayer
    final int CHOOSE_SIZE = 1;
    final int CHOOSE_COLOR = 2;
    final int MAIN_GAME = 3;

    Scene[] scenes;


//    private Scene showScene(int type) {
//        return
//    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        scenes = new Scene[NUMBER_OF_SCENES];

        //showScene(CHOOSE_TYPE);

        BoardFX boardFX = new BoardFX(new Board(6));

        Scene scene = new Scene(boardFX.getRoot(), 1300, 1000);
        scene.setFill(Color.rgb(90, 120, 70));


        primaryStage.setTitle("LOA");
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
