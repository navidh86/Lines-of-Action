package FX;

import Mechanics.Board;
import Mechanics.Move;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Pair;
import Main.Main;
import AI.AI;

import java.util.ArrayList;
import java.util.List;

public class BoardFX {
    Group root;
    Cell[][] cells;
    Board board;
    Main main;
    int dim;

    int type; //single or multi
    int playerColor; //in singleplayer

    int state = 0; //0 means src to be selected, 1 means dst to be selected, 2 means ai to move, 3 means game over

    List<Pair<Integer, Integer>> moves; //list of available moves when state = 1
    List<Line> srcLines; //list of current lines, when state = 1
    Line destLine; //line when state = 0

    Pair<Integer, Integer> from;

    Button back;

    Text gameStatus;
    List<Text> labels;

    AI ai;

    public BoardFX(int type, int dim, int color, Main main) {
        this.main = main;
        this.type = type;
        this.board = new Board(dim);
        this.dim = dim;
        this.playerColor = color;

        root = new Group();

        //set cells
        cells = new Cell[8][8];

        for (int i=0; i<dim; i++) {
            for (int j=0; j<dim; j++) {
                cells[i][j] = new Cell(i, j, this);
                root.getChildren().add(cells[i][j].getObject());
            }
        }

        gameStatus = new Text();
        gameStatus.setText("Move of: Black");
        gameStatus.setX(600);
        gameStatus.setY(970);
        gameStatus.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 26));

        back = new Button("Go back");
        back.setMinSize(80, 40);
        back.setLayoutX(900);
        back.setLayoutY(dim == 6 ? 100 : 10);
        back.setOnMouseClicked(e -> goBack());

        root.getChildren().addAll(gameStatus, back);

        setUpLabels();
        loadBoard();

        if (type == Main.MULTIPLAYER) {
            state = 0;
        }
        else {
            ai = new AI(dim);
            ai.setCoefficients(1, 0.1, 3, 10, 2, 2);

            if (playerColor == Board.BLACK) {
                state = 0;
            }
            else {
                state = 2;
                aiMove();
            }
        }
    }

    public void setFrom(int row, int col) {
        from = new Pair<>(row, col);

        moves = board.getAvailableMoves(row, col);

        //check if any move available
        if (moves.size() == 0) {
            setState(0);
            return;
        }

        srcLines = new ArrayList<>();

        Line temp;
        for (int i=0; i<moves.size(); i++) {
            //draw a line to that cell
            temp = new Line();
            temp.setStartX(cells[row][col].x + 50);
            temp.setStartY(cells[row][col].y + 50);
            temp.setEndX(cells[moves.get(i).getKey()][moves.get(i).getValue()].x + 50);
            temp.setEndY(cells[moves.get(i).getKey()][moves.get(i).getValue()].y + 50);
            temp.setStrokeWidth(4);
            temp.setStroke(Color.RED);
            temp.setMouseTransparent(true);
            srcLines.add(temp);
            root.getChildren().add(temp);

            //set the state of that cell to 1
            cells[moves.get(i).getKey()][moves.get(i).getValue()].setState(1);
        }
    }

    public void move(int destRow, int destCol) {
        this.board.move(new Move(board.moveOf, from, new Pair<>(destRow, destCol)));

        //clear previous line
        if (destLine != null) {
            root.getChildren().remove(destLine);
        }

        //update cells on fx
        cells[from.getKey()][from.getValue()].setVal(Cell.EMPTY);
        cells[destRow][destCol].setVal(3 - board.moveOf);

        destLine = new Line();
        destLine.setStartX(cells[from.getKey()][from.getValue()].x + 50);
        destLine.setStartY(cells[from.getKey()][from.getValue()].y + 50);
        destLine.setEndX(cells[destRow][destCol].x + 50);
        destLine.setEndY(cells[destRow][destCol].y + 50);
        destLine.setStrokeWidth(3);
        destLine.setStroke(Color.BLUE);
        destLine.setMouseTransparent(true);
        root.getChildren().add(destLine);

        if (board.getResult() != 0) {
            this.state = 3; //game over
            gameStatus.setFont(Font.font(35));
            gameStatus.setText((board.getResult() == board.BLACK ? "Black" : "White") + " won!!!!");
            gameStatus.setFill(Color.RED);
            gameStatus.setStroke(Color.BLACK);
        }
        else {
            gameStatus.setText("Move of: " + (board.moveOf == board.BLACK ? "Black" : "White"));
            if (board.moveOf == board.BLACK) {
                gameStatus.setFill(Color.BLACK);
            }
            else {
                gameStatus.setFill(Color.WHITE);
                gameStatus.setStroke(Color.BLACK);
            }

            if (type == Main.SINGLEPLAYER) {
                System.out.println("state = " + state);
                if (state != 2) {
                    state = 2;
                    aiMove();
                }
                else state = 0;
            }
        }
    }

    public void setUpLabels() {
        labels = new ArrayList<>();
        Text temp1, temp2;

        //set cols
        char c = 'A';
        int x = 340;
        int y1 = (dim == 8 ? 80 : 280), y2 = 935;

        for (int i=0; i<dim; i++) {
            temp1 = new Text(); temp2 = new Text();
            temp1.setText("" + c); temp2.setText("" + c);
            temp1.setX(x); temp2.setX(x);
            temp1.setY(y1); temp2.setY(y2);
            temp1.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 20));
            temp2.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 20));
            labels.add(temp1); labels.add(temp2);
            root.getChildren().addAll(temp1, temp2);
            c++;
            x += 100;
        }

        //set rows
        c = '1';
        int x1 = 265, x2 = (dim == 8 ? 1130 : 930);
        int y = 850;

        for (int i=0; i<dim; i++) {
            temp1 = new Text(); temp2 = new Text();
            temp1.setText("" + c); temp2.setText("" + c);
            temp1.setX(x1); temp2.setX(x2);
            temp1.setY(y); temp2.setY(y);
            temp1.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 20));
            temp2.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 20));
            labels.add(temp1); labels.add(temp2);
            root.getChildren().addAll(temp1, temp2);
            c++;
            y -= 100;
        }
    }

    public void setState(int state) {
        this.state = state;

        if (state == 0) {
            //set cell states to 0
            for (int i=0; i<moves.size(); i++) {
                cells[moves.get(i).getKey()][moves.get(i).getValue()].setState(0);
            }
            moves.clear();

            for (int i=0; i<srcLines.size(); i++) {
                root.getChildren().remove(srcLines.get(i));
            }
        }
    }

    public void aiMove() {
        new Thread(() -> {
            Move move = ai.getMove(board);
            from = new Pair<>(move.srcRow, move.srcCol);

            Platform.runLater(() -> move(move.destRow, move.destCol));
        }).start();
    }

    private void goBack() {
        main.goBack(1);
    }

    public void loadBoard() {
        for (int i=0; i<dim; i++) {
            for (int j=0; j<dim; j++) {
                cells[i][j].setVal(board.board[i][j]);
            }
        }
    }

    public Group getRoot() {
        return this.root;
    }
}
