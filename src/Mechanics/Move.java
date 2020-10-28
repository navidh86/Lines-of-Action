package Mechanics;

import javafx.util.Pair;

public class Move {
    public int srcRow, srcCol, destRow, destCol, moveBy;
    double score = 0;

    public Move(int moveBy, String from, String to) {
        this.moveBy = moveBy;
        this.srcRow = from.charAt(1) - '1';
        this.srcCol = from.charAt(0) - 'A';
        this.destRow = to.charAt(1) - '1';
        this.destCol = to.charAt(0) - 'A';
    }

    public Move(int moveBy, Pair<Integer, Integer> from, Pair<Integer, Integer> to) {
        this.moveBy = moveBy;
        this.srcRow = from.getKey();
        this.srcCol = from.getValue();
        this.destRow = to.getKey();
        this.destCol = to.getValue();
        this.score = 0;
    }

    public Move(Move move) {
        this.moveBy = move.moveBy;
        this.srcRow = move.srcRow;
        this.srcCol = move.srcCol;
        this.destRow = move.destRow;
        this.destCol = move.destCol;
        this.score = move.score;
    }

    public void setScore(double s) {
        this.score = s;
    }

    public double getScore() {
        return score;
    }

    public String toString() {
        return (moveBy == 1 ? "Black" : "White") + " chose (" + Board.getCell(srcRow, srcCol) + ") to (" + Board.getCell(destRow, destCol) + ")";
    }
}
