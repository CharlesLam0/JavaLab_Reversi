package model;

import java.util.Arrays;

public class Board {
    private int SIZE = 8; // Default size
    private Piece[][] board;
    public int currentBoardIndex = 0;

    public Board() {
        this(8); // Default size
    }

    public Board(int size) {
        this.SIZE = size;
        this.board = new Piece[SIZE][SIZE];
        initializeBoard();
    }

    public void setSize(int size) {
        SIZE = size;
        board = new Piece[SIZE][SIZE];
        initializeBoard();
    }

    public int getSize() {
        return SIZE;
    }

    public void initializeBoard() {
        board = new Piece[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++) {
            Arrays.fill(board[i], Piece.EMPTY);
        }
    }

    public void initializeCenter() {
        int mid = SIZE / 2;
        board[mid - 1][mid - 1] = Piece.WHITE;
        board[mid][mid] = Piece.WHITE;
        board[mid - 1][mid] = Piece.BLACK;
        board[mid][mid - 1] = Piece.BLACK;
    }

    public Piece getWhatPiece(int col, int row) {
        if (col < 0 || col >= SIZE || row < 0 || row >= SIZE) {
            return null;
        }
        return board[col][row];
    }

    public boolean isBoardFull() {
        for (int i = 0; i < getSize(); i++) {
            for (int j = 0; j < getSize(); j++) {
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
