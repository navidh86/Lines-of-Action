package Main;

import Mechanics.Board;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import FX.*;

import java.awt.*;

public class Main extends Application {
    final int NUMBER_OF_SCENES = 4;

    public final static int CHOOSE_TYPE = 0; //single player of multiplayer
    public final static int CHOOSE_SIZE = 1;
    public final static int CHOOSE_COLOR = 2;
    public final static int MAIN_GAME = 3;

    public final static int SINGLEPLAYER = 0, MULTIPLAYER = 1;
    public final static int BLACK = 1, WHITE = 2;

    //choices
    int type = SINGLEPLAYER;
    int size = 8;
    int color = BLACK;

    Scene[] scenes;

    Stage primaryStage;

    private void showScene(int type) {
        if (type != MAIN_GAME) {
            if (scenes[type] == null) {
                scenes[type] = new Scene(new ChoiceScreen(type, this).getRoot(), 1300, 1000);
            }
        }
        else {
            scenes[type] = new Scene(new BoardFX(type, size, color).getRoot(), 1300, 1000);
        }

        scenes[type].setFill(Color.rgb(90, 120, 70));

        primaryStage.setTitle("LOA");
        primaryStage.setScene(scenes[type]);
        primaryStage.show();
    }

    public void setSize(int size) {
        this.size = size;
        System.out.println("size chosen: " + size);
        if (type == SINGLEPLAYER) {
            showScene(CHOOSE_COLOR);
        }
        else {
            showScene(MAIN_GAME);
        }
    }

    public void setType(int type) {
        this.type = type;
        System.out.println("type chosen: " + type);
        showScene(CHOOSE_SIZE);
    }

    public void setColor(int color) {
        this.color = color;
        System.out.println("Color chosen: " + color);
        showScene(MAIN_GAME);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        scenes = new Scene[NUMBER_OF_SCENES];

        this.primaryStage = primaryStage;

        showScene(CHOOSE_TYPE);

        //showScene(CHOOSE_TYPE);

        //BoardFX boardFX = new BoardFX(new Board(6));
        //ChoiceScreen cs = new ChoiceScreen(CHOOSE_SIZE, this);

        //Scene scene = new Scene(cs.getRoot(), 1300, 1000);
        //Scene scene = new Scene(boardFX.getRoot(), 1300, 1000);
        //scene.setFill(Color.rgb(90, 120, 70));


//        primaryStage.setTitle("LOA");
//        primaryStage.setScene(scene);
//        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
