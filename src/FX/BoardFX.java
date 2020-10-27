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

    double baseX, baseY; //base of every other coordinate
    int size = 100; //size of cells

    int type; //single or multi
    int playerColor; //in singleplayer

    int state = 0; //0 means src to be selected, 1 means dst to be selected, 2 means ai to move, 3 means game over

    List<Pair<Integer, Integer>> moves; //list of available moves when state = 1
    List<Line> srcLines; //list of current lines, when state = 1
    Line destLine; //line when state = 0

    Pair<Integer, Integer> from;

    Button back;

    Text gameStatus, aiStatus;
    List<Text> labels;

    AI ai;

    public BoardFX(int type, int dim, int color, Main main) {
        this.main = main;
        this.type = type;
        this.board = new Board(dim);
        this.dim = dim;
        this.playerColor = color;

        this.baseX = dim == 8 ? 270 : 370;
        this.baseY = dim == 8 ? 800 : 700;

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
        gameStatus.setX(baseX + (dim/2 - 1) * size);
        gameStatus.setY(baseY + 1.8 * size);
        gameStatus.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 26));

        back = new Button("Go back");
        back.setMinSize(80, 40);
        back.setLayoutX(baseX + dim * size + 50);
        back.setLayoutY(baseY - dim * size + dim);
        back.setOnMouseClicked(e -> goBack());

        root.getChildren().addAll(gameStatus, back);

        setUpLabels();
        loadBoard();

        if (type == Main.MULTIPLAYER) {
            state = 0;
        }
        else {
            ai = new AI(dim);
            ai.setCoefficients(1, 0, 5, 10, 0, 1);
            
            aiStatus = new Text();
            aiStatus.setText("Thinking.....");
            aiStatus.setX(baseX - 2.5 * size);
            aiStatus.setY(baseY - (dim / 2) * size);
            aiStatus.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 26));
            aiStatus.setFill(Color.WHITE);
            aiStatus.setStroke(Color.BLACK);
            aiStatus.setStrokeWidth(2);
            aiStatus.setVisible(false);
            root.getChildren().add(aiStatus);

            if (playerColor == Board.BLACK) {
                state = 0;
            }
            else {
                state = 2;
                aiStatus.setVisible(true);
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
            gameStatus.setFont(Font.font(50));
            gameStatus.setText((board.getResult() == board.BLACK ? "Black" : "White") + " won!!!!");
            gameStatus.setFill(Color.RED);
            gameStatus.setStroke(Color.BLACK);
            gameStatus.setX(baseX + (dim/2 - 1) * size);
            gameStatus.setY(baseY - (dim/2 - 1) * size);

            if (type == Main.SINGLEPLAYER) {
                aiStatus.setVisible(false);
            }
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
                    aiStatus.setVisible(true);
                    aiMove();
                }
                else {
                    state = 0;
                    aiStatus.setVisible(false);
                }
            }
        }
    }

    public void setUpLabels() {
        labels = new ArrayList<>();
        Text temp1, temp2;

        //set cols
        char c = 'A';
        double x = baseX + size / 2;
        double y1 = baseY - (dim-1) * size - 25, y2 = baseY + size + 40;

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
            x += size;
        }

        //set rows
        c = '1';
        double x1 = baseX - 40, x2 = baseX + dim * size + 35;
        double y = baseY + (size/2);

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
            y -= size;
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
