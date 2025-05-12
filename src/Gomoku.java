import java.util.Scanner;

import model.Board;
import model.Piece;
import model.Player;

public class Gomoku implements GameEngine {
    private static final int BOARD_SIZE = 15; // Standard Gomoku board size
    private Board board;
    private int currentPlayerIndice;
    public int GameID;
    public boolean haveLine = false;
    public int pieceCount = 0;
    private Player blackPlayer;
    private Player whitePlayer;
    private static final int BLACK_INITIAL_BOMBS = 2;
    private static final int WHITE_INITIAL_BOMBS = 3;

    private int[][] directions = { { 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 }, { 1, 1 }, { -1, 1 }, { 1, -1 },
            { -1, -1 } };

    public Gomoku(Player player, Player player2, Scanner scanner) {
        this.board = new Board(BOARD_SIZE);
        this.currentPlayerIndice = 0;
        this.blackPlayer = player;
        this.whitePlayer = player2;

        // Initialize bomb counts
        if (player.pieceType == Piece.BLACK) {
            player.setBombCount(BLACK_INITIAL_BOMBS);
            player2.setBombCount(WHITE_INITIAL_BOMBS);
        } else {
            player.setBombCount(WHITE_INITIAL_BOMBS);
            player2.setBombCount(BLACK_INITIAL_BOMBS);
        }

        // Place fixed barriers at specified positions: 3F, 8G, 9F, CK
        placeBarrierAtPosition("3F");
        placeBarrierAtPosition("8G");
        placeBarrierAtPosition("9F");
        placeBarrierAtPosition("CK");
    }

    // Use bomb to remove opponent's piece and place a crater

    public boolean useBomb(int row, int col) {
        Player currentPlayer = getCurrentPlayer();

        // Check if the player has bombs left
        if (currentPlayer.getBombCount() <= 0) {
            return false;
        }

        // Check if position is valid and has an opponent's piece
        Piece targetPiece = board.getWhatPiece(row, col);
        Piece currentPieceType = currentPlayer.pieceType;
        Piece opponentPieceType = (currentPieceType == Piece.BLACK) ? Piece.WHITE : Piece.BLACK;

        if (targetPiece != opponentPieceType) {
            return false;
        }

        // Remove the opponent's piece and place a crater
        board.setPiece(row, col, Piece.CRATER);

        // Decrement the bomb count
        currentPlayer.decrementBombCount();

        return true;
    }

    public Player getCurrentPlayer() {
        return (currentPlayerIndice == 0) ? blackPlayer : whitePlayer;
    }

    private void placeBarrierAtPosition(String position) {
        if (position == null || position.length() < 2) {
            return;
        }

        // Extract the row (can be 1-9 or A-F in hex) and column (A-O)
        String rowString = position.substring(0, 1);
        String colString = position.substring(1, 2);

        int row;
        // Parse row from hex
        try {
            row = Integer.parseInt(rowString, 16) - 1; // Convert to 0-based index
        } catch (NumberFormatException e) {
            // Handle A-F row notation if needed
            char rowChar = rowString.toUpperCase().charAt(0);
            if (rowChar >= 'A' && rowChar <= 'F') {
                row = 10 + (rowChar - 'A') - 1; // 'A' becomes 10 (0-based would be 9)
            } else {
                return; // Invalid row
            }
        }

        // Parse column from A-O
        char colChar = colString.toUpperCase().charAt(0);
        int col = colChar - 'A';

        // Place the barrier
        placeBarrier(row, col);
    }

    private void placeBarrier(int row, int col) {
        // Check if position is within board boundaries
        if (row >= 0 && row < board.getSize() && col >= 0 && col < board.getSize()) {
            board.setPiece(row, col, Piece.BARRIER);
        }
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

                if (newCol < 0 || newCol >= getBoard().getSize() || newRow < 0 || newRow >= getBoard().getSize()) {
                    break;
                }

                Piece currentPiece = board.getWhatPiece(newCol, newRow);
                if (currentPiece == piece) {
                    count++;
                } else {
                    break;
                }
            }

            // another direction
            for (int i = 1; i < 5; i++) {
                int newCol = col - dx * i;
                int newRow = row - dy * i;

                if (newCol < 0 || newCol >= getBoard().getSize() || newRow < 0 || newRow >= getBoard().getSize()) {
                    break;
                }

                Piece currentPiece = board.getWhatPiece(newCol, newRow);
                if (currentPiece == piece) {
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

    public Player[] getPlayers() {
        return new Player[] { blackPlayer, whitePlayer };
    }
}