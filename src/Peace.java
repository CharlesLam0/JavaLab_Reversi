import java.util.Scanner;

import model.Board;
import model.Piece;
import model.Player;

public class Peace implements GameEngine {
    private Board board = new Board();
    private int currentPlayerIndice; // Array to track current player for each board
    public int GameID;

    public int getCurrentPlayerIndice() {
        return currentPlayerIndice;
    }

    public void setCurrentPlayerIndice(int currentPlayerIndice) {
        this.currentPlayerIndice = currentPlayerIndice;
    }

    public Board getBoard() {
        return board;
    }

    public Peace(Player blackPlayer, Player whitePlayer, Scanner scanner) {
        this.board = new Board();
        this.currentPlayerIndice = 0;
    }

    public boolean isGameOver() {
        if (canPlacePiece(null)) {
            return false;
        }
        return true;
    }

    public int getGameID() {
        return GameID;
    }

    public void setGameID(int id) {
        GameID = id;
    }

    public boolean canPlacePiece(Piece piece) {
        for (int i = 0; i < Board.SIZE; i++) {
            for (int j = 0; j < Board.SIZE; j++) {
                if (board.getWhatPiece(i, j) == Piece.EMPTY) {
                    return true;
                }
            }
        }
        return false;
    }

    public void placePiece(int col, int row, Piece piece) {
        if (board.getWhatPiece(col, row) == Piece.EMPTY) {
            board.setPiece(col, row, piece);
        }
    }

    public int getHowManyPieces(Piece piece) {
        return 0;
    }

    public void PassCounterAdd() {
    }

    public int getPassCounter() {
        return 0;
    }
}
