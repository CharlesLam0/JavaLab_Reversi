import java.util.Scanner;

import model.Board;
import model.Piece;
import model.Player;

public class GameEngine {
    private static Board board = new Board();
    private final Player[] players;
    private final Scanner scanner;
    private int[] currentPlayerIndices; // Array to track current player for each board

    public GameEngine(Player blackPlayer, Player whitePlayer, Scanner scanner) {
        this.players = new Player[]{blackPlayer, whitePlayer};
        this.scanner = scanner;
        this.currentPlayerIndices = new int[Board.NUM_BOARDS]; // Initialize to 0 for all boards
    }

    public void startGame() {
        while (!isGameOver()) {
            handleTurn(players[currentPlayerIndices[Board.currentBoardIndex]]);
        }
        GameView.printBoard(board, players[currentPlayerIndices[Board.currentBoardIndex]], players[0], players[1]);
        System.out.println("Game over! The board is full.");
    }

    private void handleTurn(Player player) {
        GameView.printBoard(board, players[currentPlayerIndices[Board.currentBoardIndex]], players[0], players[1]);

        int[] input = InputUtils.readValidInput(scanner, board, players[currentPlayerIndices[Board.currentBoardIndex]].pieceType);
        if (input[1] == -1) {
            // Input is a board index
            Board.switchBoard(input[0]);
            System.out.println("Switched to board " + (input[0] + 1));
        } else {
            // Input is a move
            int row = input[0];
            int col = input[1];

            // Place the piece since validation is already done in InputUtils
            Board.boards[Board.currentBoardIndex][row][col] = players[currentPlayerIndices[Board.currentBoardIndex]].pieceType;
            currentPlayerIndices[Board.currentBoardIndex] = (currentPlayerIndices[Board.currentBoardIndex] + 1) % 2;
        }
    }

    static boolean isGameOver() {
        for (int i = 0; i < Board.SIZE; i++) {
            for (int j = 0; j < Board.SIZE; j++) {
                if (board.getWhatPiece(i, j) == Piece.EMPTY) {
                    return false;
                }
            }
        }
        return true;
    }
}