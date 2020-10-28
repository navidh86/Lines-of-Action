package AI;

import Mechanics.Board;

class Evaluator {
    private final static double INF = 100000;
    private double mc, cc, dc, pc, qc, ac;
    private Board board;
    private int dim;

    Evaluator(int dim) {
        this.dim = dim;
        mc = cc = dc = pc = qc = ac = 1.0;
    }

    void setCoefficients(double pc, double ac, double mc, double cc, double qc, double dc) {
        this.mc = mc;
        this.cc = cc;
        this.dc = dc;
        this.pc = pc;
        this.qc = qc;
        this.ac = ac;
    }

    //calculate score from position of pieces using the pst
    private double getPositionScore() {
        return board.positionalScores[Board.BLACK] / board.pieceCount[Board.BLACK] - board.positionalScores[Board.WHITE] / board.pieceCount[Board.WHITE];
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
        double blackMoves = board.getAllAvailableMoves(Board.BLACK).size();
        double whiteMoves = board.getAllAvailableMoves(Board.WHITE).size();

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
    private double getQuadScore() {
        int blackQuadScore = 0, whiteQuadScore = 0;
        int[] temp;

        //black first
        int iStart = (int) Math.max(0, board.centerOfMass[Board.BLACK][0] - 2),
                iEnd = (int) Math.min(dim-2, board.centerOfMass[Board.BLACK][0] + 2),
                jStart = (int) Math.max(0, board.centerOfMass[Board.BLACK][1] - 2),
                jEnd = (int) Math.min(dim-2, board.centerOfMass[Board.BLACK][1] + 2);

        for (int i=iStart; i<=iEnd; i++) {
            for (int j=jStart; j<=jEnd; j++) {
                temp = new int[3];

                //count the number of pieces of each type in the quad starting from (i, j) and ending in (i+1, j+1)
                temp[board.board[i][j]]++;
                temp[board.board[i+1][j]]++;
                temp[board.board[i][j+1]]++;
                temp[board.board[i+1][j+1]]++;

                if (temp[Board.BLACK] >= 3) {
                    blackQuadScore++;
                }
            }
        }

        //white
        iStart = (int) Math.max(0, board.centerOfMass[Board.WHITE][0] - 2);
        iEnd = (int) Math.min(dim-2, board.centerOfMass[Board.WHITE][0] + 2);
        jStart = (int) Math.max(0, board.centerOfMass[Board.WHITE][1] - 2);
        jEnd = (int) Math.min(dim-2, board.centerOfMass[Board.WHITE][1] + 2);

        for (int i=iStart; i<=iEnd; i++) {
            for (int j=jStart; j<=jEnd; j++) {
                temp = new int[3];

                //count the number of pieces of each type in the quad starting from (i, j) and ending in (i+1, j+1)
                temp[board.board[i][j]]++;
                temp[board.board[i+1][j]]++;
                temp[board.board[i][j+1]]++;
                temp[board.board[i+1][j+1]]++;

                if (temp[Board.WHITE] >= 3) {
                    whiteQuadScore++;
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
                    double rowDist = Math.abs(i - board.centerOfMass[color][0]);
                    double colDist = Math.abs(j - board.centerOfMass[color][1]);
                    scores[color] += rowDist + colDist;
                }
            }
        }

        scores[Board.BLACK] /= board.pieceCount[Board.BLACK];
        scores[Board.WHITE] /= board.pieceCount[Board.WHITE];

        return scores[Board.WHITE] - scores[Board.BLACK]; //less avg distance preferable
    }

    double evaluate(Board board, int color) {
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
