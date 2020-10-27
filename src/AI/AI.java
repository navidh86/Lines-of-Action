package AI;

import Mechanics.*;
import javafx.util.Pair;

import java.util.*;

public class AI {
    public final static double INF = 100000;
    private final double eps = .00001;

    int dim;
    int currentDepth;
    int maxDepth;
    double timeLimit, startTime;
    Evaluator evaluator;
    boolean timeUp = false;
    double scoreCutOff = INF / 2;

    double nodesVisited;

    List<Move> moveList; //list of moves sorted on current depth

    public AI(int dim) {
        this.dim = dim;
        this.timeLimit = (dim == 8 ? 2000: 1000);
        this.currentDepth = 0;
        this.maxDepth = 10;
        this.evaluator = new Evaluator(dim);
    }

    public void setCoefficients(double pc, double ac, double mc, double cc, double qc, double dc) {
        this.evaluator.setCoefficients(pc, ac, mc, cc, qc, dc);
    }

    public double evaluate(Board board, int color) {
        return evaluator.evaluate(board, color);
    }

    double minimax(Board board, int color, int depth, double alpha, double beta) {
        double score = evaluate(board, color);
        nodesVisited++;

        if (score == INF)
            return INF - depth;
        else if (score == -INF)
            return -INF + depth;
        else if(depth == 0)
            return score;
        else if(System.currentTimeMillis()-startTime > timeLimit) {
            timeUp = true;
            return score;
        }

        Random random = new Random(System.currentTimeMillis());

        Board temp;
        List<Move> moveList = board.getAllAvailableMoves(board.moveOf);

        if (board.moveOf == color) {
            score = -2 * INF;

            for (int i=0; i<moveList.size(); i++) {
                temp = new Board(board);
                temp.move(moveList.get(i));

                double score2 = minimax(temp, color, depth-1, alpha, beta);
                if (score2 > score) {
                    score = score2;
                }
                else if (Math.abs(score2 - score) < eps) {
                    if (random.nextBoolean()) {
                        score = score2;
                    }
                }

                alpha = Math.max(alpha, score);

                if (beta <= alpha)
                    break;
            }
        }
        else {
            score = 2 * INF;

            for (int i=0; i<moveList.size(); i++) {
                temp = new Board(board);
                temp.move(moveList.get(i));

                double score2 = minimax(temp, color, depth-1, alpha, beta);
                if (score2 < score) {
                    score = score2;
                }
                else if (Math.abs(score2 - score) < eps) {
                    if (random.nextBoolean()) {
                        score = score2;
                    }
                }

                beta = Math.min(beta, score);

                if (beta <= alpha)
                    break;
            }
        }

        return score;
    }

    public Move getMove(Board board) {
        Move bestCurrent = null, bestAllTime = null;
        double score;
        int color = board.moveOf;

        int completedDepth = 0;

        Random random = new Random(System.currentTimeMillis());

        Board temp;

        moveList = board.getAllAvailableMoves(color);
        MoveComparator moveComparator = new MoveComparator();

        startTime = System.currentTimeMillis();
        timeUp = false;
        nodesVisited = 0;

        for (currentDepth = 1; currentDepth <= maxDepth && !timeUp; currentDepth++) {
            score = -2 * INF;

            for (int i=0; i<moveList.size(); i++) {
                Move m = moveList.get(i);

                if (System.currentTimeMillis()-startTime > timeLimit) {
                    timeUp = true;
                    break;
                }

                temp = new Board(board);
                temp.move(m);

                double score2 = minimax(temp, color, currentDepth - 1, -INF, INF);

                //save the score
                m.setScore(score2);

                if (score2 > score) {
                    score = score2;
                    bestCurrent = m;
                }
                else if (Math.abs(score2 - score) < eps) {
                    if (random.nextBoolean()) {
                        score = score2;
                        bestCurrent = m;
                    }
                }
            }

            if (!timeUp) {
                completedDepth = currentDepth;
                bestAllTime = new Move(bestCurrent);
                System.out.println("At depth: " + currentDepth + ", Score: " + bestCurrent.getscore());
            }
            else {
                System.out.println("Time up before ending: " + currentDepth);
                if (bestCurrent.getscore() > scoreCutOff) {
                    bestAllTime = bestCurrent;
                }
            }

            if (bestAllTime.getscore() > scoreCutOff) break;

            Collections.sort(moveList, moveComparator);
        }

        System.out.println("Found at " + completedDepth + " score: " + bestAllTime.getscore());
        System.out.println("Time needed: " + (System.currentTimeMillis()-startTime));
        System.out.println("Nodes visited: " + nodesVisited);

        return bestAllTime;
    }
}
