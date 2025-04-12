import java.util.Scanner;

import model.Board;
import model.Piece;
import model.Player;

public class Reversi implements GameEngine {
    private Board board = new Board();
    private int currentPlayerIndice = 0;
    public int passCounter = 0;

    public int getPassCounter() {
        return passCounter;
    }

    public void PassCounterAdd () {
        passCounter ++;
    }

    public int getCurrentPlayerIndice() {
        return currentPlayerIndice;
    }

    public void setCurrentPlayerIndice(int currentPlayerIndice) {
        this.currentPlayerIndice = currentPlayerIndice;
    }

    public Board getBoard() {
        return board;
    }

    private int[][] directions = { { 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 }, { 1, 1 }, { -1, 1 }, { 1, -1 },
            { -1, -1 } };
    public int GameID;

    public Reversi(Player blackPlayer, Player whitePlayer, Scanner scanner) {
        this.board = new Board();
        this.currentPlayerIndice = 0;
    }

    public boolean isGameOver() {
        if (canPlacePiece(Piece.WHITE) || canPlacePiece(Piece.BLACK)) {
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
        // clear the CANPLACE pieces
        for (int i = 0; i < Board.SIZE; i++) {
            for (int j = 0; j < Board.SIZE; j++) {
                if (board.getWhatPiece(i, j) == Piece.CANPLACE) {
                    board.setPiece(i, j, Piece.EMPTY);
                }
            }
        }

        boolean canPlace = false;
        for (int i = 0; i < Board.SIZE; i++) {
            for (int j = 0; j < Board.SIZE; j++) {
                // check if the position is empty
                if (board.getWhatPiece(i, j) != Piece.EMPTY) {
                    continue;
                }

                boolean isValidMove = false;

                for (int k = 0; k < directions.length; k++) {
                    int x = directions[k][0];
                    int y = directions[k][1];
                    int steps = 0;

                    // go in the direction of x and y
                    while (i + x >= 0 && i + x < Board.SIZE && j + y >= 0 && j + y < Board.SIZE) {
                        // if the position is empty or CANPLACE, break, the direction is not valid
                        if (board.getWhatPiece(i + x, j + y) == Piece.EMPTY ||
                                board.getWhatPiece(i + x, j + y) == Piece.CANPLACE) {
                            break;
                        }

                        // if the position is the same as the piece, check if there are steps
                        if (board.getWhatPiece(i + x, j + y) == piece) {
                            if (steps > 0) { // make sure there is at least one piece in between
                                isValidMove = true;
                            }
                            break;
                        }

                        // continue if the direction valid
                        steps++;
                        x += directions[k][0];
                        y += directions[k][1];
                    }
                }

                // if the move is valid, set the position to CANPLACE
                if (isValidMove) {
                    board.setPiece(i, j, Piece.CANPLACE);
                    canPlace = true;
                }
            }
        }

        return canPlace;
    }

    public void placePiece(int col, int row, Piece piece) {
        passCounter = 0;
        // clear the CANPLACE pieces
        if (board.getWhatPiece(col, row) == Piece.CANPLACE) {
            board.setPiece(col, row, piece);

            for (int k = 0; k < directions.length; k++) {
                int dx = directions[k][0];
                int dy = directions[k][1];
                int x = dx;
                int y = dy;
                // List to store the positions to flip
                java.util.ArrayList<int[]> toFlip = new java.util.ArrayList<>();
                boolean validDirection = false;
                while (col + x >= 0 && col + x < Board.SIZE && row + y >= 0 && row + y < Board.SIZE) {
                    Piece currentPiece = board.getWhatPiece(col + x, row + y);

                    if (currentPiece == Piece.EMPTY || currentPiece == Piece.CANPLACE) {
                        break;
                    } else if (currentPiece == piece) {
                        validDirection = true;
                        break;
                    } else {
                        toFlip.add(new int[] { col + x, row + y });
                    }

                    x += dx;
                    y += dy;
                }
                if (validDirection) {
                    for (int[] pos : toFlip) {
                        board.setPiece(pos[0], pos[1], piece);
                    }
                }
            }
        }
    }

    public int getHowManyPieces(Piece piece) {
        int count = 0;
        for (int i = 0; i < Board.SIZE; i++) {
            for (int j = 0; j < Board.SIZE; j++) {
                if (board.getWhatPiece(i, j) == piece) {
                    count++;
                }
            }
        }
        return count;
    }

	@Override
	public void isLine(int col, int row, Piece piece) {
	}

}
