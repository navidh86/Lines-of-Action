package FX;

import Mechanics.Board;
import Mechanics.Move;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;
import javafx.util.Pair;
import Main.Main;
import AI.AI;

import java.util.ArrayList;
import java.util.List;

public class BoardFX {
    private Group root;
    private Cell[][] cells;
    Board board;
    private Main main;
    private int dim;

    double baseX, baseY; //base of every other coordinate
    double size; //size of cells

    private int type; //single or multi

    int state = 0; //0 means src to be selected, 1 means dst to be selected, 2 means ai to move, 3 means game over

    private List<Pair<Integer, Integer>> moves; //list of available moves when state = 1
    private List<Line> srcLines; //list of current lines, when state = 1
    private List<Circle> srcCircles; //list of current circles, when state = 1
    private Line destLine; //line when state = 0
    private Circle destCircle; //circle when state = 0

    private Pair<Integer, Integer> from;

    private Text gameStatus, aiStatus;

    private AI ai;

    public BoardFX(int type, int dim, int color, Main main) {
        this.main = main;
        this.type = type;
        this.board = new Board(dim);
        this.dim = dim;

        this.size = Math.floor(Main.height / 100) * 10;
        this.baseX = (Main.width - dim * size) / 2;
        this.baseY = (Main.height - dim * size) / 2 + (dim-1.1) * size;

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

        Button back = new Button("Go back");
        back.setMinSize(80, 40);
        back.setLayoutX(baseX + dim * size + size/2);
        back.setLayoutY(baseY - dim * size + size/3);
        back.setOnMouseClicked(e -> goBack());

        root.getChildren().addAll(gameStatus, back);

        setUpLabels();
        loadBoard();

        if (type == Main.MULTIPLAYER) {
            state = 0;
        }
        else {
            ai = new AI(dim);
            ai.setCoefficients(1, 0, .7, 5, 2, 7);
            
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

            if (color == Board.BLACK) {
                state = 0;
            }
            else {
                state = 2;
                aiStatus.setVisible(true);
                aiMove();
            }
        }
    }

    void setFrom(int row, int col) {
        from = new Pair<>(row, col);

        moves = board.getAvailableMoves(row, col);

        //check if any move available
        if (moves.size() == 0) {
            setState(0);
            return;
        }

        srcLines = new ArrayList<>();
        srcCircles = new ArrayList<>();

        Line tempLine;
        Circle tempCircle;
        double offset = size/2;

        for (int i=0; i<moves.size(); i++) {
            //draw a line to that cell
            tempLine = new Line();
            tempLine.setStartX(cells[row][col].x + offset);
            tempLine.setStartY(cells[row][col].y + offset);
            tempLine.setEndX(cells[moves.get(i).getKey()][moves.get(i).getValue()].x + offset);
            tempLine.setEndY(cells[moves.get(i).getKey()][moves.get(i).getValue()].y + offset);
            tempLine.setStrokeWidth(4);
            tempLine.setStroke(Color.RED);
            tempLine.setMouseTransparent(true);
            srcLines.add(tempLine);

            tempCircle = new Circle();
            tempCircle.setCenterX(cells[moves.get(i).getKey()][moves.get(i).getValue()].x + offset);
            tempCircle.setCenterY(cells[moves.get(i).getKey()][moves.get(i).getValue()].y + offset);
            tempCircle.setRadius(.05 * size);
            tempCircle.setFill(Color.RED);
            tempCircle.setMouseTransparent(true);
            srcCircles.add(tempCircle);

            //set the state of that cell to 1
            cells[moves.get(i).getKey()][moves.get(i).getValue()].setState(1);
        }

        //add a small circle in the 'from' cell
        tempCircle = new Circle();
        tempCircle.setCenterX(cells[row][col].x + offset);
        tempCircle.setCenterY(cells[row][col].y + offset);
        tempCircle.setRadius(.065 * size);
        tempCircle.setFill(Color.RED);
        tempCircle.setMouseTransparent(true);
        srcCircles.add(tempCircle);
        
        root.getChildren().addAll(srcLines);
        root.getChildren().addAll(srcCircles);
    }

    void move(int destRow, int destCol) {
        this.board.move(new Move(board.moveOf, from, new Pair<>(destRow, destCol)));

        //clear previous line and circle
        if (destLine != null) {
            root.getChildren().remove(destLine);
            root.getChildren().remove(destCircle);
        }

        //update cells on fx
        cells[from.getKey()][from.getValue()].setVal(Cell.EMPTY);
        cells[destRow][destCol].setVal(3 - board.moveOf);

        double offset = size/2;
        //add new dest line and circle
        //line
        destLine = new Line();
        destLine.setStartX(cells[from.getKey()][from.getValue()].x + offset);
        destLine.setStartY(cells[from.getKey()][from.getValue()].y + offset);
        destLine.setEndX(cells[destRow][destCol].x + offset);
        destLine.setEndY(cells[destRow][destCol].y + offset);
        destLine.setStrokeWidth(3);
        destLine.setStroke(Color.BLUE);
        destLine.setMouseTransparent(true);

        //circle
        destCircle = new Circle();
        destCircle.setCenterX(cells[destRow][destCol].x + offset);
        destCircle.setCenterY(cells[destRow][destCol].y + offset);
        destCircle.setRadius(.05 * size);
        destCircle.setFill(Color.BLUE);
        destCircle.setMouseTransparent(true);

        //add them
        root.getChildren().addAll(destLine, destCircle);

        if (board.getResult() != 0) {
            this.state = 3; //game over
            gameStatus.setFont(Font.font(50));
            gameStatus.setText((board.getResult() == Board.BLACK ? "Black" : "White") + " won!!!!");
            gameStatus.setFill(Color.RED);
            gameStatus.setStroke(Color.BLACK);
            gameStatus.setX(baseX + (dim/2 - 1) * size);
            gameStatus.setY(baseY - (dim/2 - 1) * size);

            if (type == Main.SINGLEPLAYER) {
                aiStatus.setVisible(false);
            }

            //blink the result
            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(0.4), evt -> gameStatus.setVisible(false)),
                    new KeyFrame(Duration.seconds(0.8), evt -> gameStatus.setVisible(true)));
            timeline.setCycleCount(Animation.INDEFINITE);
            timeline.play();
        }
        else {
            gameStatus.setText("Move of: " + (board.moveOf == Board.BLACK ? "Black" : "White"));
            if (board.moveOf == Board.BLACK) {
                gameStatus.setFill(Color.BLACK);
            }
            else {
                gameStatus.setFill(Color.WHITE);
                gameStatus.setStroke(Color.BLACK);
            }

            if (type == Main.SINGLEPLAYER) {
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

    private void setUpLabels() {
        Text temp1, temp2;

        //set cols
        char c = 'A';
        double x = baseX + size / 2;
        double y1 = baseY - (dim-1) * size - size/4, y2 = baseY + size + .4*size;

        for (int i=0; i<dim; i++) {
            temp1 = new Text(); temp2 = new Text();
            temp1.setText("" + c); temp2.setText("" + c);
            temp1.setX(x); temp2.setX(x);
            temp1.setY(y1); temp2.setY(y2);
            temp1.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 20));
            temp2.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 20));
            root.getChildren().addAll(temp1, temp2);
            c++;
            x += size;
        }

        //set rows
        c = '1';
        double x1 = baseX - .4*size, x2 = baseX + dim * size + .35*size;
        double y = baseY + (size/2);

        for (int i=0; i<dim; i++) {
            temp1 = new Text(); temp2 = new Text();
            temp1.setText("" + c); temp2.setText("" + c);
            temp1.setX(x1); temp2.setX(x2);
            temp1.setY(y); temp2.setY(y);
            temp1.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 20));
            temp2.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 20));
            root.getChildren().addAll(temp1, temp2);
            c++;
            y -= size;
        }
    }

    void setState(int state) {
        this.state = state;

        if (state == 0) {
            //set cell states to 0
            for (int i=0; i<moves.size(); i++) {
                cells[moves.get(i).getKey()][moves.get(i).getValue()].setState(0);
            }
            moves.clear();

            //remove the src lines and circle
            root.getChildren().removeAll(srcLines);
            root.getChildren().removeAll(srcCircles);
        }
    }

    private void aiMove() {
        new Thread(() -> {
            Move move = ai.getMove(board);
            from = new Pair<>(move.srcRow, move.srcCol);

            Platform.runLater(() -> move(move.destRow, move.destCol));
        }).start();
    }

    private void goBack() {
        main.goBack(1);
    }

    private void loadBoard() {
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
