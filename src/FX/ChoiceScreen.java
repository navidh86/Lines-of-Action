package FX;

import javafx.event.EventHandler;
import javafx.scene.Group;
import Main.Main;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class ChoiceScreen {
    Group root;
    Main main;
    Button choice1, choice2, back;
    Text text;
    int type;

    public ChoiceScreen(int type, Main main) {
        this.main = main;
        this.type = type;

        root = new Group();
        
        text = new Text();
        text.setX(400);
        text.setY(370);
        text.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 30));
        root.getChildren().add(text);
        
        setupButtons(type);
    }

    private void setupButtons(int type) {
        if (type == Main.CHOOSE_SIZE) {
            text.setText("Choose size of board");

            choice1 = new Button("6 * 6");
            choice2 = new Button("8 * 8");
        }
        else if (type == Main.CHOOSE_TYPE) {
            text.setText("Choose game type");

            choice1 = new Button("Single Player");
            choice2 = new Button("Multiplayer");
        }
        else if (type == Main.CHOOSE_COLOR) {
            text.setText("Choose color");

            choice1 = new Button("Black");
            choice2 = new Button("White");
        }

        choice1.setMinSize(150, 80);
        choice2.setMinSize(150, 80);
        choice1.setLayoutX(500);
        choice2.setLayoutX(500);
        choice1.setLayoutY(400);
        choice2.setLayoutY(550);

        back = new Button("Go back");
        back.setMinSize(80, 40);
        back.setLayoutX(800);
        back.setLayoutY(200);

        if (type == Main.CHOOSE_TYPE) {
            //nowhere to go back to
            back.setDisable(true);
            back.setVisible(false);
        }

        back.setOnMouseClicked(e -> goBack());

        choice1.setOnMouseClicked(e ->choose(1));

        choice2.setOnMouseClicked(e -> choose(2));

        root.getChildren().addAll(choice1, choice2, back);
    }

    public Group getRoot() {
        return this.root;
    }

    private void goBack() {
        main.goBack(type);
    }

    private void choose(int choice) {
        if (type == Main.CHOOSE_SIZE) {
            main.setSize(choice == 1 ? 6 : 8);
        }
        else if (type == Main.CHOOSE_TYPE) {
            main.setType(choice == 1 ? Main.SINGLEPLAYER : Main.MULTIPLAYER);
        }
        else if (type == Main.CHOOSE_COLOR) {
            main.setColor(choice == 1 ? Main.BLACK : Main.WHITE);
        }
    }
}
