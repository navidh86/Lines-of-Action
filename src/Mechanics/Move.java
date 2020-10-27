package Mechanics;

import javafx.util.Pair;

import java.util.Comparator;

public class Move {
    int srcRow, srcCol, destRow, destCol, moveBy;
    int score = 0;

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

    public void setScore(double s) {
        this.score = (int) s;
    }

    public String toString() {
        return (moveBy == 1 ? "Black" : "White") + " chose (" + Board.getCell(srcRow, srcCol) + ") to (" + Board.getCell(destRow, destCol) + ")";
    }
}

class MoveComparator implements Comparator<Move> {
    public int compare(Move m1, Move m2) {
        return (m2.score - m1.score);
    }
}
