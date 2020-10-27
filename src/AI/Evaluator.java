package AI;

import Mechanics.Board;

public class Evaluator {
    private final static double INF = 100000;
    private double mc, cc, dc, pc, qc, ac;
    private Board board;
    private int dim;

    private double[][] pst;

    public Evaluator(int dim) {
        this.dim = dim;
        setPst(dim);
        mc = cc = dc = pc = qc = ac = 1.0;
    }

    public void setCoefficients(double pc, double ac, double mc, double cc, double qc, double dc) {
        this.mc = mc;
        this.cc = cc;
        this.dc = dc;
        this.pc = pc;
        this.qc = qc;
        this.ac = ac;
    }

    private void setPst(int dim) {
        if (dim == 8) {
            double[][] temp = {
                    {-80, -25, -20, -20, -20, -20, -25, -80},
                    {-25,  10,  10,  10,  10,  10,  10, -25},
                    {-20,  10,  25,  25,  25,  25,  10, -20},
                    {-20,  10,  25,  50,  50,  25,  10, -20},
                    {-20,  10,  25,  50,  50,  25,  10, -20},
                    {-20,  10,  25,  25,  25,  25,  10, -20},
                    {-25,  10,  10,  10,  10,  10,  10, -25},
                    {-80, -25, -20, -20, -20, -20, -25, -80}
            };
            pst = temp;
        }
        else if (dim == 6) {
            double[][] temp = {
                    {-40, -20,  -5,  -5, -20, -40},
                    {-20,  10,  10,  10,  10, -20},
                    { -5,  10,  25,  25,  10,  -5},
                    { -5,  10,  25,  25,  10,  -5},
                    {-20,  10,  10,  10,  10, -20},
                    {-40, -20,  -5,  -5, -20, -40}
            };
            pst = temp;
        }
    }

    //calculate score from position of pieces using the pst
    private double getPositionScore() {
        //normalized
        double[] score = new double[3];

        for (int i=0; i<dim; i++) {
            for (int j=0; j<dim; j++) {
                if (board.board[i][j] != Board.EMPTY) {
                    score[board.board[i][j]] += pst[i][j];
                }
            }
        }

        return (1.0 * score[Board.BLACK]) / board.pieceCount[Board.BLACK] - (1.0 * score[Board.WHITE]) / board.pieceCount[Board.WHITE];
    }

    //calculate score from area, smaller area better
    private double getAreaScore() {
        int blackRowMin = dim, blackRowMax = -1, blackColMin = dim, blackColMax = -1, whiteRowMin = dim, whiteRowMax = -1, whiteColMin = dim, whiteColMax = -1;

        for (int i=0; i<dim; i++) {
            for (int j=0; j<dim; j++) {
                if (board.board[i][j] == Board.BLACK) {
                    blackRowMin = Math.min(blackRowMin, i);
                    blackRowMax = Math.max(blackRowMax, i);
                    blackColMin = Math.min(blackColMin, j);
                    blackColMax = Math.max(blackColMax, j);
                }
                else if (board.board[i][j] == Board.WHITE) {
                    whiteRowMin = Math.min(whiteRowMin, i);
                    whiteRowMax = Math.max(whiteRowMax, i);
                    whiteColMin = Math.min(whiteColMin, j);
                    whiteColMax = Math.max(whiteColMax, j);
                }
            }
        }

        //return the difference of areas (whiteArea - BlackArea)
        double blackArea = (blackRowMax-blackRowMin+1) * (blackColMax-blackColMin+1);
        double whiteArea = (whiteRowMax-whiteRowMin+1) * (whiteColMax-whiteColMin+1);

        return whiteArea - blackArea;
    }

    //sum of available move count of all pieces
    private double getMobilityScore() {
        //normailzed
        double blackMoves = (1.0 * board.getAllAvailableMoves(Board.BLACK).size()) / board.pieceCount[Board.BLACK];
        double whiteMoves = (1.0 * board.getAllAvailableMoves(Board.WHITE).size()) / board.pieceCount[Board.WHITE];

        return blackMoves - whiteMoves;
    }

    //sum of count of adjacent pieces of all pieces
    private double getConnectednessScore() {
        //normalized
        int[] connections = new int[3];
        int[] fr = {1, 1, 1, 0, -1, -1, -1, 0};
        int[] fc = {-1, 0, 1, 1, 1, 0, -1, -1};

        for (int i=0; i<dim; i++) {
            for (int j=0; j<dim; j++) {
                if (board.board[i][j] != Board.EMPTY) {
                    for (int k=0; k<8; k++) {
                        int dr = i + fr[k];
                        int dc = j + fc[k];

                        if (dr >= 0 && dr < dim && dc >= 0 && dc < dim && board.board[dr][dc] == board.board[i][j] ) {
                            connections[board.board[i][j]]++;
                        }
                    }
                }
            }
        }

        return (1.0 * connections[Board.BLACK]) / board.pieceCount[Board.BLACK] -
                (1.0 * connections[Board.WHITE]) / board.pieceCount[Board.WHITE];
    }

    //quad score
    double getQuadScore() {
        int blackQuadScore = 0, whiteQuadScore = 0;
        
        int[] temp;
        for (int i=0; i<dim-1; i++) {
            for (int j=0; j<dim-1; j++) {
                temp = new int[3];
                
                //count the number of pieces of each type in the quad starting from (i, j) and ending in (i+1, j+1)
                temp[board.board[i][j]]++;
                temp[board.board[i+1][j]]++;
                temp[board.board[i][j+1]]++;
                temp[board.board[i+1][j+1]]++;
                
                if (temp[Board.BLACK] >= 3) {
                    blackQuadScore += temp[Board.BLACK];
                }
                else if (temp[Board.WHITE] >= 3) {
                    whiteQuadScore += temp[Board.WHITE];
                }
            }
        }

        return blackQuadScore - whiteQuadScore;
    }
    
    private double getDensityScore() {
        double[] scores = new double[3];
        
        for (int i=0; i<dim; i++) {
            for (int j=0; j<dim; j++) {
                if (board.board[i][j] != Board.EMPTY) {
                    int color = board.board[i][j];
                    double rowDist = Math.pow(i - board.centerOfMass[color][0], 2);
                    double colDist = Math.pow(j - board.centerOfMass[color][1], 2);
                    scores[color] += Math.sqrt(rowDist + colDist);
                }
            }
        }
        
        scores[Board.BLACK] /= board.pieceCount[Board.BLACK];
        scores[Board.WHITE] /= board.pieceCount[Board.WHITE];
        
        return scores[Board.WHITE] - scores[Board.BLACK]; //less avg distance preferable
    }

    public double evaluate(Board board, int color) {
        this.board = board;
        this.dim = board.getDim();

        double score = 0;

        //evaluate from the perspective of BLACK
        if (board.getResult() == Board.BLACK)
            score = INF;
        else if (board.getResult() == Board.WHITE)
            score = -INF;
        else {
            if (pc > 0) {
                score += pc * getPositionScore();
            }

            if (ac > 0) {
                score += ac * getAreaScore();
            }

            if (mc > 0) {
                score += mc * getMobilityScore();
            }

            if (cc > 0) {
                score += cc * getConnectednessScore();
            }

            if (qc > 0) {
                score += qc * getQuadScore();
            }

            if (dc > 0) {
                score += dc * getDensityScore();
            }
        }

        return (color == Board.BLACK ? score : -score); //negate if evaluating for white
    }
}
