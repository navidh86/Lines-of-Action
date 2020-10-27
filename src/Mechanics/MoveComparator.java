package Mechanics;


import java.util.Comparator;

public class MoveComparator implements Comparator<Move> {
    public int compare(Move m1, Move m2) {
        return (int) (m2.score * 100 - m1.score * 100);
    }
}