package Main;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import FX.*;

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
    BoardFX bfx;

    Stage primaryStage;

    private void showScene(int screenIdx) {
        if (screenIdx != MAIN_GAME) {
            if (scenes[screenIdx] == null) {
                scenes[screenIdx] = new Scene(new ChoiceScreen(screenIdx, this).getRoot(), 1300, 1000);
            }
        }
        else {
            bfx = new BoardFX(type, size, color, this);
            scenes[screenIdx] = new Scene(bfx.getRoot(), 1300, 1000);
        }

        scenes[screenIdx].setFill(Color.rgb(90, 120, 70));

        primaryStage.setTitle("LOA");
        primaryStage.setScene(scenes[screenIdx]);
        primaryStage.show();
    }

    public void goBack(int type) {
        showScene(type - 1);
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
    }


    public static void main(String[] args) {
        launch(args);
    }
}
