package Mechanics;

import javafx.util.Pair;

import java.util.*;

public class Board {
    public static final int EMPTY = 0;
    public static final int BLACK = 1;
    public static final int WHITE = 2;

    public int[][] board; //0 means empty, 1 means black, 2 means white
    public int moveOf;
    int dim;
    int moveCount;
    public int[] pieceCount, rowCount, colCount, rdiagCount, ldiagCount;
    public double[][] centerOfMass;

    //positional value
    double[][] pst;
    public double[] positionalScores;

    public Board(int dim) {
        this.dim = dim;
        board = new int[dim][dim];
        rowCount = new int[dim];
        colCount = new int[dim];
        rdiagCount = new int[2*dim - 1];
        ldiagCount = new int[2*dim - 1];
        pieceCount = new int[3];
        centerOfMass = new double[3][2];

        for (int i=0; i<dim; i++) {
            boolean flag = (i == 0) || (i == (dim-1));

            for (int j=0; j<dim; j++) {
                if (flag && (j > 0 && j < (dim-1))) {
                    board[i][j] = BLACK;
                }
                else if(!flag && (j == 0 || (j == dim-1))) {
                    board[i][j] = WHITE;
                }
                else {
                    board[i][j] = EMPTY;
                }

                //set colCount
                if (j == 0 || (j == dim - 1)) {
                    colCount[j] = dim - 2;
                }
                else {
                    colCount[j] = 2;
                }
            }

            //set rowCount
            if (flag) {
                rowCount[i] = dim - 2;
            }
            else {
                rowCount[i] = 2;
            }
        }

        //set diag count
        for (int i=0; i<2*dim-1; i++) {
            if (i == 0 || (i == (dim-1) || (i == (2*dim - 2)))) {
                rdiagCount[i] = ldiagCount[i] = 0;
            }
            else {
                rdiagCount[i] = ldiagCount[i] = 2;
            }
        }

        pieceCount = new int[3];
        pieceCount[BLACK] = pieceCount[WHITE] = 2 * (dim - 2);
        
        //init center of mass, [][0] -> row, [][1] -> column
        centerOfMass = new double[3][2];
        centerOfMass[BLACK][0] = centerOfMass[BLACK][1] = centerOfMass[WHITE][0] = centerOfMass[WHITE][1] = (dim - 1) / 2.0;

        //pst
        setPst(dim);
        positionalScores = new double[3];
        positionalScores[BLACK] = positionalScores[WHITE] = dim == 8 ? -260 : -140;

        moveOf = BLACK;
        moveCount = 0;
    }

    public Board(Board b) {
        dim = b.dim;
        moveOf = b.moveOf;
        moveCount = b.moveCount;

        board = new int[dim][dim];
        rowCount = new int[dim];
        colCount = new int[dim];
        rdiagCount = new int[2*dim - 1];
        ldiagCount = new int[2*dim - 1];
        pieceCount = new int[3];
        centerOfMass = new double[3][2];

        for (int i=0; i<dim; i++) {
            rowCount[i] = b.rowCount[i];
            colCount[i] = b.colCount[i];

            for (int j=0; j<dim; j++) {
                board[i][j] = b.board[i][j];
            }
        }

        for (int i=0; i<2*dim-1; i++) {
            rdiagCount[i] = b.rdiagCount[i];
            ldiagCount[i] = b.ldiagCount[i];
        }

        pieceCount[BLACK] = b.pieceCount[BLACK];
        pieceCount[WHITE] = b.pieceCount[WHITE];

        centerOfMass[BLACK][0] = b.centerOfMass[BLACK][0];
        centerOfMass[BLACK][1] = b.centerOfMass[BLACK][1];
        centerOfMass[WHITE][0] = b.centerOfMass[WHITE][0];
        centerOfMass[WHITE][1] = b.centerOfMass[WHITE][1];

        //pst
        setPst(dim);
        positionalScores = new double[3];
        positionalScores[BLACK] = b.positionalScores[BLACK];
        positionalScores[WHITE] = b.positionalScores[WHITE];
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
                    {-60, -20, -15,  -15, -20, -60},
                    {-20,  20,  20,   20,  20, -20},
                    {-15,  20,  40,   40,  20, -15},
                    {-15,  20,  40,   40,  20, -15},
                    {-20,  20,  20,   20,  20, -20},
                    {-60, -20, -15,  -15, -20, -60}
            };
            pst = temp;
        }
    }

    public Board move(String from, String to) {
        int opponent = 3 - moveOf;

        List<Integer> ret = getPos(to);

        if (getColor(to) != EMPTY) {
            //opponent piece has been captured
            //update center of mass for opponent

            //denormalize
            centerOfMass[opponent][0] *= pieceCount[opponent];
            centerOfMass[opponent][1] *= pieceCount[opponent];

            //subtract the positions of the removed piece
            centerOfMass[opponent][0] -= ret.get(0);
            centerOfMass[opponent][1] -= ret.get(1);

            //update pieceCount
            pieceCount[opponent]--;

            //normalize
            centerOfMass[opponent][0] /= pieceCount[opponent];
            centerOfMass[opponent][1] /= pieceCount[opponent];

            //update positional score
            positionalScores[opponent] -= pst[ret.get(0)][ret.get(1)];
        }
        else {
            rowCount[ret.get(0)]++;
            colCount[ret.get(1)]++;
            rdiagCount[ret.get(2)]++;
            ldiagCount[ret.get(3)]++;
        }

        //add the new positions to the center of mass of the moving piece
        centerOfMass[moveOf][0] += (1.0 * ret.get(0)) / pieceCount[moveOf];
        centerOfMass[moveOf][1] += (1.0 * ret.get(1)) / pieceCount[moveOf];

        //update positional score
        positionalScores[moveOf] += pst[ret.get(0)][ret.get(1)];

        ret = getPos(from);
        rowCount[ret.get(0)]--;
        colCount[ret.get(1)]--;
        rdiagCount[ret.get(2)]--;
        ldiagCount[ret.get(3)]--;

        //subtract the old positions from the center of mass of the moving piece
        centerOfMass[moveOf][0] -= (1.0 * ret.get(0)) / pieceCount[moveOf];
        centerOfMass[moveOf][1] -= (1.0 * ret.get(1)) / pieceCount[moveOf];

        //update positional score
        positionalScores[moveOf] -= pst[ret.get(0)][ret.get(1)];

        //update board array
        setColor(from, EMPTY);
        setColor(to, moveOf);

        moveOf = opponent;
        moveCount++;

        return this;
    }

    public Board move(Move m) {
        String from = getCell(m.srcRow, m.srcCol), to = getCell(m.destRow, m.destCol);

        return move(from, to);
    }

    private int getColor(String cell) {
        return board[cell.charAt(1)-'1'][cell.charAt(0)-'A'];
    }

    private void setColor(String cell, int color) {
        board[cell.charAt(1)-'1'][cell.charAt(0)-'A'] = color;
    }

    List<Integer> getPos(String cell) {
        //row, column, rdiag and ldiag number of that cell
        int row = cell.charAt(1) - '1', col = cell.charAt(0) - 'A';
        int rdiag, ldiag;

        rdiag = dim - row - 1 + col;
        ldiag = dim - row - 1 + dim - col - 1;

        List<Integer> ret = new ArrayList<>();
        ret.add(row);
        ret.add(col);
        ret.add(rdiag);
        ret.add(ldiag);

        return ret;
    }

    static String getCell(int row, int col) {
        String ret = "";

        char c = 'A';
        c += col;
        ret += c;

        c = '1';
        c += row;
        ret += c;

        return ret;
    }

    public int getResult() {
        //0 means game not over yet
        int result = 0;
        //check for black first
        if (isConnected(BLACK))
            result = BLACK;

        //check for white
        if (isConnected(WHITE)) {
            if (result == BLACK) {
                //both black and white connected
                result = 3 - moveOf; //winner is the one whose move led to this position
            }
            else {
                result = WHITE;
            }
        }

        if (result == 0 && getAllAvailableMoves(moveOf).size() == 0) {
            //if the current player to move has no valid moves, the other player wins
            result = 3 - moveOf;
        }

        return result;
    }

    private boolean isConnected(int color) {
        //check if all pieces of that color are connected using bfs
        int[] fr = {1, 1, 1, 0, -1, -1, -1, 0};
        int[] fc = {-1, 0, 1, 1, 1, 0, -1, -1};

        boolean[][] vis = new boolean[dim][dim];
        int cnt = 0; //number of disjoint links
        Queue<Pair<Integer,Integer>> q;

        for (int i=0; i<dim; i++) {
            for (int j=0; j<dim; j++) {
                if (board[i][j] == color && !vis[i][j]) {
                    cnt++;

                    if (cnt > 1) {
                        break;
                    }

                    q = new LinkedList<>();
                    q.add(new Pair<>(i, j));
                    vis[i][j] = true;

                    while (!q.isEmpty()) {
                        Pair<Integer, Integer> top = q.poll();
                        int tr = top.getKey(), tc = top.getValue();
                        for (int k=0; k<8; k++) {
                            int dr = tr + fr[k];
                            int dc = tc + fc[k];

                            if (dr >= 0 && dr < dim && dc >= 0 && dc < dim && board[dr][dc] == color && !vis[dr][dc]) {
                                vis[dr][dc] = true;
                                q.add(new Pair<>(dr, dc));
                            }
                        }
                    }
                }
            }
        }

        return (cnt == 1);
    }

    public List<Pair<Integer, Integer>> getAvailableMoves(int row, int col) {
        List<Pair<Integer, Integer>> ret = new ArrayList<>();

        int color = board[row][col];
        int opColor = 3 - color;

        int rdiag = dim - row - 1 + col;
        int ldiag = dim - row - 1 + dim - col - 1;

        int rc = rowCount[row], cc = colCount[col], rdc = rdiagCount[rdiag], ldc = ldiagCount[ldiag];
        boolean flag;

        //up
        if (row+cc < dim) {
            flag = true;
            for (int i=1; i<=cc; i++) {
                if ((i < cc && board[row+i][col] == opColor) || (i == cc && board[row+i][col] == color)) {
                    flag = false;
                    break;
                }
            }
            if (flag) {
                ret.add(new Pair<>(row+cc, col));
            }
        }

        //down
        if (row-cc >= 0) {
            flag = true;
            for (int i=1; i<=cc; i++) {
                if ((i < cc && board[row-i][col] == opColor) || (i == cc && board[row-i][col] == color)) {
                    flag = false;
                    break;
                }
            }
            if (flag) {
                ret.add(new Pair<>(row-cc, col));
            }
        }

        //right
        if (col+rc < dim) {
            flag = true;
            for (int i=1; i<=rc; i++) {
                if ((i < rc && board[row][col+i] == opColor) || (i == rc && board[row][col+i] == color)) {
                    flag = false;
                    break;
                }
            }
            if (flag) {
                ret.add(new Pair<>(row, col+rc));
            }
        }

        //left
        if (col-rc >= 0) {
            flag = true;
            for (int i=1; i<=rc; i++) {
                if ((i < rc && board[row][col-i] == opColor) || (i == rc && board[row][col-i] == color)) {
                    flag = false;
                    break;
                }
            }
            if (flag) {
                ret.add(new Pair<>(row, col-rc));
            }
        }

        //up-right
        if ((row+rdc) < dim && (col+rdc) < dim) {
            flag = true;
            for (int i=1; i<=rdc; i++) {
                if ((i < rdc && board[row+i][col+i] == opColor) || (i == rdc && board[row+i][col+i] == color)) {
                    flag = false;
                    break;
                }
            }
            if (flag) {
                ret.add(new Pair<>(row+rdc, col+rdc));
            }
        }

        //down-left
        if ((row-rdc) >= 0 && (col-rdc) >= 0) {
            flag = true;
            for (int i=1; i<=rdc; i++) {
                if ((i < rdc && board[row-i][col-i] == opColor) || (i == rdc && board[row-i][col-i] == color)) {
                    flag = false;
                    break;
                }
            }
            if (flag) {
                ret.add(new Pair<>(row-rdc, col-rdc));
            }
        }

        //up-left
        if ((row+ldc) < dim && (col-ldc) >= 0) {
            flag = true;
            for (int i=1; i<=ldc; i++) {
                if ((i < ldc && board[row+i][col-i] == opColor) || (i == ldc && board[row+i][col-i] == color)) {
                    flag = false;
                    break;
                }
            }
            if (flag) {
                ret.add(new Pair<>(row+ldc, col-ldc));
            }
        }

        //down-right
        if ((row-ldc) >= 0 && (col+ldc) < dim) {
            flag = true;
            for (int i=1; i<=ldc; i++) {
                if ((i < ldc && board[row-i][col+i] == opColor) || (i == ldc && board[row-i][col+i] == color)) {
                    flag = false;
                    break;
                }
            }
            if (flag) {
                ret.add(new Pair<>(row-ldc, col+ldc));
            }
        }

        return ret;
    }

    public List<String> getAvailableMovesString(String cell) {
        List<Pair<Integer, Integer>> temp = getAvailableMoves(cell.charAt(1)-'1', cell.charAt(0)-'A');
        List<String> ret = new ArrayList<>();

        for (int i=0; i<temp.size(); i++) {
            ret.add(getCell(temp.get(i).getKey(), temp.get(i).getValue()));
        }

        return ret;
    }

    public List<Move> getAllAvailableMoves(int color) {
        //list of all available moves for pieces of specified color
        List<Move> ret = new ArrayList<>();
        List<Pair<Integer, Integer>> temp;
        Pair<Integer, Integer> src;

        for (int i=0; i<dim; i++) {
            for (int j=0; j<dim; j++) {
                if (board[i][j] == color) {
                    src = new Pair<>(i, j);
                    temp = getAvailableMoves(i, j);
                    for (int k=0; k<temp.size(); k++) {
                        ret.add(new Move(moveOf, src, temp.get(k)));
                    }
                }
            }
        }

        return ret;
    }

    public int getDim() {
        return dim;
    }

    public void printBoard() {
        System.out.print(" ");
        for (int i=0; i<dim; i++) {
            char c = 'A';
            c += i;
            System.out.print(" | " + c);
        }
        System.out.println(" |");
        System.out.println("   --------------------------------");


        for (int i=dim-1; i>=0; i--) {
            System.out.print((i+1) + " | ");
            for (int j=0; j<dim; j++) {
                System.out.print(board[i][j] + " | ");
            }
            System.out.println();
            System.out.println("   --------------------------------");
        }
    }
}
