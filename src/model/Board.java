package model;
import java.util.Arrays;

public class Board {
    public static final int SIZE = 8;
    public static final int NUM_BOARDS = 3;
    public static Piece[][][] boards = new Piece[NUM_BOARDS][SIZE][SIZE];
    public static int currentBoardIndex = 0;

    public Board() {
        for (int i = 0; i < NUM_BOARDS; i++) {
            boards[i] = new Piece[SIZE][SIZE];
            initializeBoard(i);
        }
    }

    private void initializeBoard(int boardIndex) {
        for (int i = 0; i < SIZE; i++) {
            Arrays.fill(boards[boardIndex][i], Piece.EMPTY);
        }
        boards[boardIndex][3][3] = Piece.WHITE;
        boards[boardIndex][4][4] = Piece.WHITE;
        boards[boardIndex][3][4] = Piece.BLACK;
        boards[boardIndex][4][3] = Piece.BLACK;
    }

    public static boolean canPlacePiece(int row, int col, Piece piece) {
        if (boards[currentBoardIndex][row][col] != Piece.EMPTY) return false;
        return true;
    }

    public Piece getWhatPiece(int row, int col) {
        if (row < 0 || row >= SIZE || col < 0 || col >= SIZE) {
            throw new IllegalArgumentException("Invalid board position");
        }
        return boards[currentBoardIndex][row][col];
    }

    public static void switchBoard(int boardIndex) {
        if (boardIndex < 0 || boardIndex >= NUM_BOARDS) {
            throw new IllegalArgumentException("Invalid board index");
        }
        currentBoardIndex = boardIndex;
    }
}