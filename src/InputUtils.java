import java.util.regex.*;

import model.Piece;

import java.util.Scanner;

public class InputUtils {
    private static final Pattern INPUT_PATTERN = Pattern.compile("^([1-8])([a-hA-H])$");

    public static int[] parseInput(String input) throws IllegalArgumentException {
        // Remove non-printable characters (including hidden control chars)
        input = input.replaceAll("[^\\p{Print}]", "").trim();

        if (input.equalsIgnoreCase("quit")) {
            return new int[] { 0, -2 };
        }

        if (input.equalsIgnoreCase("pass")) {
            return new int[] { 0, -3 };
        }

        if (input.equalsIgnoreCase("peace")) {
            return new int[] { 0, -4 };
        }

        if (input.equalsIgnoreCase("reversi")) {
            return new int[] { 0, -5 };
        }

        if (input.equalsIgnoreCase("gomoku")) {
            return new int[] { 0, -6 };
        }

        // Check if the input is a single digit, indicating a board index
        if (input.matches("-?\\d+")) {
            int boardIndex = Integer.parseInt(input) - 1;
            if (boardIndex < 0 || boardIndex >= GameManager.getNumberOfGames()) {
                throw new IllegalArgumentException("Invalid board index");
            }
            return new int[] { boardIndex, -1 }; // Return board index
        }

        // Otherwise, treat it as a move
        Matcher matcher = INPUT_PATTERN.matcher(input);
        if (!matcher.find()) {
            throw new IllegalArgumentException("Invalid input format");
        }

        int row = Integer.parseInt(matcher.group(1)) - 1;
        int col = Character.toLowerCase(matcher.group(2).charAt(0)) - 'a';

        return new int[] { row, col };
    }

    public static int[] readValidInput(Scanner scanner, GameEngine engine, Piece piece) {
        while (true) {
            String input = scanner.nextLine().trim();
            if (input.isEmpty())
                continue; // Ignore empty lines.
            try {
                int[] move = parseInput(input);
                if (move[1] <= -1) {
                    return move;
                }
                // Check if the move can be placed.
                engine.canPlacePiece(piece);

                if (engine.getClass().getSimpleName().equals("Reversi")) {
                    if (engine.getBoard().getWhatPiece(move[0], move[1]) == Piece.CANPLACE) {
                        return move;
                    } else {
                        System.out.println();
                        System.out.println("This position cannot be placed. Please try again.");
                        continue;
                    }

                } else if (engine.getClass().getSimpleName().equals("Peace")) {
                    if (engine.getBoard().getWhatPiece(move[0], move[1]) == Piece.EMPTY) {
                        return move;
                    } else {
                        System.out.println();
                        System.out.println("This position cannot be placed. Please try again.");
                        continue;
                    }
                } else if (engine.getClass().getSimpleName().equals("Gomoku")) {
                    if (engine.getBoard().getWhatPiece(move[0], move[1]) == Piece.EMPTY &&
                            engine.canPlacePiece(piece)) {
                        return move;
                    } else {
                        System.out.println();
                        System.out.println("This position cannot be placed. Please try again.");
                        continue;
                    }
                }
            } catch (IllegalArgumentException e) {
                System.out.printf(
                        "Invalid input format. Please enter a number (1-8) followed by a letter (A-H) / a board number (1-%d) / new game (peace / reversi / gomoku) / quit the game (quit) : ",
                        GameManager.getNumberOfGames());
            }
        }
    }
}
