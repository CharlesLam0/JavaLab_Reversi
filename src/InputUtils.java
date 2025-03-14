import java.util.regex.*;

import model.Board;
import model.Piece;

import java.util.Scanner;

public class InputUtils{
    private static final Pattern INPUT_PATTERN = Pattern.compile("^([1-8])([a-hA-H])$");

    public static int[] parseInput(String input) throws IllegalArgumentException {
        // Remove non-printable characters (including hidden control chars)
        input = input.replaceAll("[^\\p{Print}]", "").trim();

        // Check if the input is a single digit, indicating a board index
        if (input.length() == 1 && Character.isDigit(input.charAt(0))) {
            int boardIndex = Integer.parseInt(input) - 1;
            if (boardIndex < 0 || boardIndex >= Board.NUM_BOARDS) {
                throw new IllegalArgumentException("Invalid board index");
            }
            return new int[]{boardIndex, -1}; // Return board index
        }

        // Otherwise, treat it as a move
        Matcher matcher = INPUT_PATTERN.matcher(input);
        if (!matcher.find()) {
            throw new IllegalArgumentException("Invalid input format");
        }

        int row = Integer.parseInt(matcher.group(1)) - 1;
        int col = Character.toLowerCase(matcher.group(2).charAt(0)) - 'a';

        return new int[]{row, col};
    }

    public static int[] readValidInput(Scanner scanner, Board board, Piece piece) {
        while (true) {
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) continue; // Ignore empty lines.
            try {
                int[] move = parseInput(input);
                if (move[1] == -1) {
                    // Input is a board index
                    return move;
                }
                int row = move[0];
                int col = move[1];
                // Check if the move can be placed.
                if (!Board.canPlacePiece(row, col, piece)) {
                    System.out.println("This position cannot be placed. Please try again.");
                    continue;
                }
                return move;
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid input format. Please enter a number (1-8) followed by a letter (A-H), or a board number (1-3).");
            }
        }
    }
}
