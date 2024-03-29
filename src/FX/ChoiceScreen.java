package FX;

import javafx.scene.Group;
import Main.Main;
import javafx.scene.control.Button;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class ChoiceScreen {
    private Group root;
    private Main main;
    private Button choice1, choice2;
    private Text text;
    private int type;
    private double baseX, baseY;
    private final double btnWidth = 150, btnHeight = 60;

    public ChoiceScreen(int type, Main main) {
        this.main = main;
        this.type = type;

        this.baseX = (Main.width - btnWidth) / 2;
        this.baseY = (Main.height - btnHeight * 2) / 2;

        root = new Group();
        
        text = new Text();
        text.setX(baseX - btnWidth / 2);
        text.setY(baseY - btnHeight);
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
            text.setText("      Choose color");

            choice1 = new Button("Black");
            choice2 = new Button("White");
        }

        choice1.setMinSize(btnWidth, btnHeight);
        choice2.setMinSize(btnWidth, btnHeight);
        choice1.setLayoutX(baseX);
        choice2.setLayoutX(baseX);
        choice1.setLayoutY(baseY);
        choice2.setLayoutY(baseY  + 2 * btnHeight);

        Button back = new Button("Go back");
        back.setMinSize(btnWidth/2, btnHeight/2);
        back.setLayoutX(baseX + btnWidth * 1.5);
        back.setLayoutY(baseY - btnHeight * 3);

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
