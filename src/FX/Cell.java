package FX;

import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

class Cell {
    final static int EMPTY = 0;
    final static int BLACK = 1;
    final static int WHITE = 2;

    private double size;
    private int row, col;
    double x, y;
    private int val; //1 black, 2 white

    private Color darkColor = Color.rgb(129,130,134);
    private Color lightColor = Color.rgb(205,206,208);

    private Circle circle;
    private StackPane sp;

    private BoardFX parent;

    private int state = 0; //1 means this cell is a dest currently

    Cell(int row, int col, BoardFX parent) {
        this.parent = parent;

        double xOffset = parent.baseX;
        double yOffset = parent.baseY;

        this.row = row;
        this.col = col;
        this.size = parent.size;
        this.x = xOffset + (col * size);
        this.y = yOffset - (row * size);
        this.val = -1;

        createCell();
        createCircle();
        setVal(0);
    }

    StackPane getObject() {
        return sp;
    }

    void setVal(int val) {
        if (this.val != val) {
            this.val = val;
            setCircle(val);
        }
    }

    private void createCell() {
        sp = new StackPane();
        sp.setLayoutX(x);
        sp.setLayoutY(y);

        Rectangle rectangle = new Rectangle();
        rectangle.setHeight(size);
        rectangle.setWidth(size);
        if ((row+col) % 2 == 0) {
            rectangle.setFill(darkColor);
        }
        else {
            rectangle.setFill(lightColor);
        }
        sp.getChildren().add(rectangle);

        sp.setOnMouseClicked(e -> handleClick());
    }

    private void createCircle() {
        circle = new Circle();
        circle.setCenterX(size/2);
        circle.setCenterY(size/2);
        circle.setRadius(size*.35);
        circle.setStrokeWidth(3);
        sp.getChildren().add(circle);
    }

    private void setCircle(int val) {
        if (val != EMPTY) {
            circle.setDisable(false);
            circle.setVisible(true);
            if (val == 1) {
                circle.setFill(Color.BLACK);
                circle.setStroke(Color.WHITE);
            }
            else {
                circle.setFill(Color.WHITE);
                circle.setStroke(Color.BLACK);
            }
        }
        else {
            circle.setVisible(false);
            circle.setDisable(true);
        }
    }

    private void handleClick() {
        if (parent.state == 0) {
            if (val == parent.board.moveOf) {
                parent.setState(1);
                parent.setFrom(row, col);
            }
        }
        else if (parent.state == 1 && state == 1) {
            parent.setState(0);
            parent.move(row, col);
        }
        else if (parent.state == 1) {
            parent.setState(0);
        }
    }

    void setState(int state) {
        this.state = state;
    }
}
