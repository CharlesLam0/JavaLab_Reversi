import java.util.Scanner;

import model.Board;
import model.Piece;
import model.Player;

public class Gomoku implements GameEngine {
    private Board board = new Board();
    private int currentPlayerIndice;
    public int GameID;
    public boolean haveLine = false;
    public int pieceCount = 0;

    private int[][] directions = { { 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 }, { 1, 1 }, { -1, 1 }, { 1, -1 },
            { -1, -1 } };

    public Gomoku(Player player, Player player2, Scanner scanner) {
        this.board = new Board();
        this.currentPlayerIndice = 0;
    }

    public int getPassCounter() {
        return passCounter;
    }

    public void PassCounterAdd() {
    }

    public int getCurrentPlayerIndice() {
        return currentPlayerIndice;
    }

    public void setCurrentPlayerIndice(int currentPlayerIndice) {
        this.currentPlayerIndice = currentPlayerIndice;
    }

    public int getGameID() {
        return GameID;
    }

    public void setGameID(int id) {
        GameID = id;
    }

    public boolean canPlacePiece(Piece piece) {
        if (haveLine) {
            return false;
        }
        return true;
    }

    public void placePiece(int col, int row, Piece piece) {
        if (board.getWhatPiece(col, row) == Piece.EMPTY) {
            board.setPiece(col, row, piece);
            pieceCount++;
        }
    }

    public Board getBoard() {
        return board;
    }

    public int getHowManyPieces(Piece piece) {
        return pieceCount;
    }

    public void isLine(int col, int row, Piece piece) {
        for (int[] direction : directions) {
            int count = 1;
            int dx = direction[0];
            int dy = direction[1];

            // one direction
            for (int i = 1; i < 5; i++) {
                int newCol = col + dx * i;
                int newRow = row + dy * i;

                if (newCol < 0 || newCol >= Board.SIZE || newRow < 0 || newRow >= Board.SIZE) {
                    break;
                }

                if (board.getWhatPiece(newCol, newRow) == piece) {
                    count++;
                } else {
                    break;
                }
            }

            // another direction
            for (int i = 1; i < 5; i++) {
                int newCol = col - dx * i;
                int newRow = row - dy * i;

                if (newCol < 0 || newCol >= Board.SIZE || newRow < 0 || newRow >= Board.SIZE) {
                    break;
                }

                if (board.getWhatPiece(newCol, newRow) == piece) {
                    count++;
                } else {
                    break;
                }
            }

           
            if (count >= 5) {
                haveLine = true;
                return; 
            }
        }
    }

    public boolean isGameOver() {
        if (haveLine || board.isBoardFull()) {
            return true;
        }
        return false;
    }
}