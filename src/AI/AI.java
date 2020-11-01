package AI;

import Mechanics.*;

import java.util.*;

public class AI {
    private final static double INF = 100000;
    private final static double EPS = .001;
    private final double CUT_OFF = INF / 2;

    private int dim;
    private int currentDepth;
    private int maxDepth;
    private double timeLimit, startTime;
    private Evaluator evaluator;
    private boolean timeUp = false;

    private double nodesVisited;

    public AI(int dim) {
        this.dim = dim;
        this.timeLimit = (dim == 8 ? 1999: 999);
        this.currentDepth = 0;
        this.maxDepth = 10;
        this.evaluator = new Evaluator(dim);
    }

    public void setCoefficients(double pc, double ac, double mc, double cc, double qc, double dc) {
        this.evaluator.setCoefficients(pc, ac, mc, cc, qc, dc);
    }

    private double evaluate(Board board, int color) {
        return evaluator.evaluate(board, color);
    }

    private double minimax(Board board, int color, int depth, double alpha, double beta) {
        double score = evaluate(board, color);
        nodesVisited++;

        if (score == INF)
            return INF + depth;
        else if (score == -INF)
            return -INF - depth;
        else if (depth == 0)
            return score;
        else if (System.currentTimeMillis()-startTime > timeLimit) {
            timeUp = true;
            return score;
        }

        Board temp;
        List<Move> moveList = board.getAllAvailableMoves(board.moveOf);

        if (board.moveOf == color) {
            //maximize
            score = -2 * INF;

            for (Move m :moveList) {
                temp = new Board(board);
                temp.move(m);

                double score2 = minimax(temp, color, depth-1, alpha, beta);
                if (score2 > score) {
                    score = score2;
                }

                alpha = Math.max(alpha, score);

                if (beta <= alpha)
                    break;
            }
        }
        else {
            //minimize
            score = 2 * INF;

            for (Move m :moveList) {
                temp = new Board(board);
                temp.move(m);

                double score2 = minimax(temp, color, depth-1, alpha, beta);
                if (score2 < score) {
                    score = score2;
                }

                beta = Math.min(beta, score);

                if (beta <= alpha)
                    break;
            }
        }

        return score;
    }

    public Move getMove(Board board) {
        double score;
        int color = board.moveOf;

        int completedDepth = 0;

        Random random = new Random(System.currentTimeMillis());

        Board temp;

        List<Move> moveList = board.getAllAvailableMoves(color);
        MoveComparator moveComparator = new MoveComparator();

        Move best = moveList.get(0);

        startTime = System.currentTimeMillis();
        timeUp = false;
        nodesVisited = 0;

        for (currentDepth = 1; currentDepth <= maxDepth && !timeUp; currentDepth++) {
            score = -2 * INF;

            for (Move m : moveList) {
                if (System.currentTimeMillis()-startTime > timeLimit) {
                    timeUp = true;
                    break;
                }

                temp = new Board(board);
                temp.move(m);

                double score2 = minimax(temp, color, currentDepth - 1, -INF, INF);

                if (timeUp) break;

                //save the score
                m.setScore(score2);

                if (score2 > score) {
                    score = score2;
                    best = m;
                }
                else if (Math.abs(score2 - score) < EPS) {
                    if (random.nextBoolean()) {
                        score = score2;
                        best = m;
                    }
                }
            }

            if (!timeUp) {
                System.out.println("At depth: " + currentDepth + ", Score: " + best.getScore());
            }
            else {
                System.out.println("Time up before ending: " + currentDepth);
            }

            completedDepth = currentDepth;

            if (best.getScore() > CUT_OFF) break;

            moveList.sort(moveComparator);
        }

        System.out.println("Found at " + completedDepth + " score: " + best.getScore());
        System.out.println("Time needed: " + (System.currentTimeMillis()-startTime));
        System.out.println("Nodes visited: " + nodesVisited);

        return best;
    }
}
