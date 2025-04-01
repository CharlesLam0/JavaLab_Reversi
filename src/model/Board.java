package model;

import java.util.Arrays;

public class Board {
    public static final int SIZE = 8;
    public Piece[][] board = new Piece[SIZE][SIZE];
    public int currentBoardIndex = 0;

    public Board() {
        initializeBoard();
    }

    private void initializeBoard() {
        for (int i = 0; i < SIZE; i++) {
            Arrays.fill(board[i], Piece.EMPTY);
        }
        
         board[3][3] = Piece.WHITE;
         board[4][4] = Piece.WHITE;
         board[3][4] = Piece.BLACK;
         board[4][3] = Piece.BLACK;
         
        //test case
        //board[1][1] = Piece.WHITE;
        //board[2][2] = Piece.WHITE;
        //board[1][2] = Piece.BLACK;
        //board[2][1] = Piece.BLACK;
    }

    public Piece getWhatPiece(int col, int row) {
        if (col < 0 || col >= SIZE || row < 0 || row >= SIZE) {
            return null;
        }
        return board[col][row];
    }

    public boolean isBoardFull() {
        for (int i = 0; i < Board.SIZE; i++) {
            for (int j = 0; j < Board.SIZE; j++) {
                if (board[i][j] == Piece.EMPTY || board[i][j] == Piece.CANPLACE) {
                    return false;
                }
            }
        }
        return true;
    }

    public void setPiece(int col, int row, Piece piece) {
        board[col][row] = piece;
    }
}
