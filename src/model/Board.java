package model;
import java.util.Arrays;

public class Board {
    public static final int SIZE = 8;
    public static Piece [][] grid;

    public Board(){
        grid = new Piece[SIZE][SIZE];
        initializeBoard();
    }

    private void initializeBoard(){
        for (int i=0; i<SIZE; i++){
            Arrays.fill(grid[i], Piece.EMPTY);
        }
        grid[3][3] = Piece.WHITE;
        grid[4][4] = Piece.WHITE;
        grid[3][4] = Piece.BLACK;
        grid[4][3] = Piece.BLACK;
    }

    public static boolean canplacePiece(int row, int col, Piece piece) {
        if (grid[row][col] != Piece.EMPTY) return false;
        return true;
    }

    public Piece getwhatPiece(int row, int col){
        if (row < 0 || row >= SIZE || col < 0 || col >= SIZE) {
            throw new IllegalArgumentException("Invalid board position");
        }
        return grid[row][col];
    }
}