import java.util.regex.*;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Queue;
import java.util.LinkedList;

import model.Piece;

public class InputUtils {
    private static final Pattern STANDARD_INPUT_PATTERN = Pattern.compile("^([1-8])([a-hA-H])$");
    private static final Pattern GOMOKU_INPUT_PATTERN = Pattern.compile("^([1-9a-fA-F])([a-oA-O])$");
    private static Queue<String> commandQueue = new LinkedList<>();
    private static boolean playbackMode = false;

    /**
     * Process the playback command to read commands from a file
     * 
     * @param filename The file to read commands from
     * @return true if file was loaded successfully, false otherwise
     */
    public static boolean processPlaybackCommand(String filename) {
        try {
            File file = new File(filename);
            Scanner fileScanner = new Scanner(file);

            // Clear any existing commands in the queue
            commandQueue.clear();

            // Read all commands from the file into the queue
            while (fileScanner.hasNextLine()) {
                String command = fileScanner.nextLine().trim();
                if (!command.isEmpty()) {
                    commandQueue.offer(command);
                }
            }

            fileScanner.close();
            playbackMode = true;
            System.out.println("Playback mode: Loaded " + commandQueue.size() + " commands from " + filename);
            return true;
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + filename);
            return false;
        }
    }

    public static int[] parseInput(String input, GameEngine engine) throws IllegalArgumentException {
        // Process playback command
        if (input.startsWith("playback ")) {
            String filename = input.substring("playback ".length()).trim();
            if (processPlaybackCommand(filename)) {
                return new int[] { 0, -7 }; // Special code for playback command
            } else {
                throw new IllegalArgumentException("Failed to load playback file");
            }
        }

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

        // Check if the input is a bomb command for Gomoku (@1A format where 1 is row, A
        // is column)
        if (engine instanceof Gomoku && input.startsWith("@")) {
            // Extract row and column parts manually for better control
            if (input.length() < 3) {
                throw new IllegalArgumentException(
                        "Invalid bomb format. Use @1A where 1 is row (1-F) and A is column (A-O)");
            }

            char rowChar = Character.toUpperCase(input.charAt(1));
            char colChar = Character.toUpperCase(input.charAt(2));

            // Validate column (A-O)
            if (colChar < 'A' || colChar > 'O') {
                throw new IllegalArgumentException("Invalid column in bomb command. Must be A-O");
            }

            int col = colChar - 'A';
            int row;

            // Parse row - can be 1-9 or A-F (for 10-15)
            if (Character.isDigit(rowChar)) {
                row = Character.getNumericValue(rowChar) - 1;
            } else if (rowChar >= 'A' && rowChar <= 'F') {
                row = 10 + (rowChar - 'A') - 1;
            } else {
                throw new IllegalArgumentException("Invalid row in bomb command. Must be 1-9 or A-F");
            }

            if (row < 0 || row >= engine.getBoard().getSize() || col < 0 || col >= engine.getBoard().getSize()) {
                throw new IllegalArgumentException("Position out of bounds");
            }

            // Return bomb command
            return new int[] { row, col, 1 }; // Third parameter 1 indicates bomb usage
        }

        // Check if the input is a single digit, indicating a board index
        if (input.matches("-?\\d+")) {
            int boardIndex = Integer.parseInt(input) - 1;
            if (boardIndex < 0 || boardIndex >= GameManager.getNumberOfGames()) {
                throw new IllegalArgumentException("Invalid board index");
            }
            return new int[] { boardIndex, -1 }; // Return board index
        }

        // For Gomoku, use hexadecimal pattern for rows
        if (engine instanceof Gomoku) {
            Matcher matcher = GOMOKU_INPUT_PATTERN.matcher(input);
            if (!matcher.find()) {
                throw new IllegalArgumentException("Invalid input format for Gomoku");
            }

            // Parse row as hexadecimal
            int row = Integer.parseInt(matcher.group(1), 16) - 1;
            int col = Character.toLowerCase(matcher.group(2).charAt(0)) - 'a';

            if (row < 0 || row >= engine.getBoard().getSize() || col < 0 || col >= engine.getBoard().getSize()) {
                throw new IllegalArgumentException("Position out of bounds");
            }

            return new int[] { row, col };
        } else {
            // Otherwise, treat it as a standard move
            Matcher matcher = STANDARD_INPUT_PATTERN.matcher(input);
            if (!matcher.find()) {
                throw new IllegalArgumentException("Invalid input format");
            }

            int row = Integer.parseInt(matcher.group(1)) - 1;
            int col = Character.toLowerCase(matcher.group(2).charAt(0)) - 'a';

            return new int[] { row, col };
        }
    }

    public static int[] readValidInput(Scanner scanner, GameEngine engine, Piece piece) {
        while (true) {
            String input;

            // Check if we have commands in the queue (playback mode)
            if (playbackMode && !commandQueue.isEmpty()) {
                input = commandQueue.poll();
                System.out.println("> " + input); // Echo the command being executed

                // After processing the command, sleep for 1 second
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    // Ignore interruption
                }

                // If queue is now empty, switch back to keyboard input
                if (commandQueue.isEmpty()) {
                    playbackMode = false;
                    System.out.println("Playback finished. Switching back to keyboard input.");
                }
            } else {
                // Regular keyboard input
                input = scanner.nextLine().trim();
            }

            if (input.isEmpty())
                continue; // Ignore empty lines.
            try {
                int[] move = parseInput(input, engine);

                // Special case for playback command
                if (move.length == 2 && move[1] == -7) {
                    // Continue to the next command
                    continue;
                }

                // Handle bomb usage (move has length 3 with the third element = 1)
                if (move.length > 2 && move[2] == 1 && engine instanceof Gomoku) {
                    Gomoku gomoku = (Gomoku) engine;
                    if (gomoku.getCurrentPlayer().getBombCount() <= 0) {
                        System.out.println();
                        System.out.println("You don't have any bombs left. Please try again.");
                        continue;
                    }

                    // Check if the target is an opponent's piece
                    Piece targetPiece = engine.getBoard().getWhatPiece(move[0], move[1]);
                    Piece currentPieceType = gomoku.getCurrentPlayer().pieceType;
                    Piece opponentPieceType = (currentPieceType == Piece.BLACK) ? Piece.WHITE : Piece.BLACK;

                    if (targetPiece != opponentPieceType) {
                        System.out.println();
                        System.out.println("You can only use bombs on opponent's pieces. Please try again.");
                        continue;
                    }

                    return move; // Valid bomb usage
                }

                if (move.length <= 2 && move[1] <= -1) {
                    return move;
                }

                // Check if the move can be placed.
                engine.canPlacePiece(piece);

                if (engine instanceof Reversi) {
                    if (engine.getBoard().getWhatPiece(move[0], move[1]) == Piece.CANPLACE) {
                        return move;
                    } else {
                        System.out.println();
                        System.out.println("This position cannot be placed. Please try again.");
                        continue;
                    }
                } else if (engine instanceof Peace) {
                    if (engine.getBoard().getWhatPiece(move[0], move[1]) == Piece.EMPTY) {
                        return move;
                    } else {
                        System.out.println();
                        System.out.println("This position cannot be placed. Please try again.");
                        continue;
                    }
                } else if (engine instanceof Gomoku) {
                    if (engine.getBoard().getWhatPiece(move[0], move[1]) == Piece.EMPTY &&
                            engine.canPlacePiece(piece)) {
                        return move;
                    } else if (engine.getBoard().getWhatPiece(move[0], move[1]) == Piece.BARRIER) {
                        System.out.println();
                        System.out.println(
                                "This position has a barrier. You cannot place a piece here. Please try again.");
                        continue;
                    } else if (engine.getBoard().getWhatPiece(move[0], move[1]) == Piece.CRATER) {
                        System.out.println();
                        System.out.println(
                                "This position has a bomb crater. You cannot place a piece here. Please try again.");
                        continue;
                    } else {
                        System.out.println();
                        System.out.println("This position cannot be placed. Please try again.");
                        continue;
                    }
                }
            } catch (IllegalArgumentException e) {
                if (engine instanceof Gomoku) {
                    System.out.printf(
                            "Invalid input format. Please enter a hexadecimal number (1-F) followed by a letter (A-O), bomb command (@1A where 1 is row and A is column), a board number (1-%d), new game (peace / reversi / gomoku), or quit the game (quit) : ",
                            GameManager.getNumberOfGames());
                } else {
                    System.out.printf(
                            "Invalid input format. Please enter a number (1-8) followed by a letter (A-H) / a board number (1-%d) / new game (peace / reversi / gomoku) / quit the game (quit) : ",
                            GameManager.getNumberOfGames());
                }
            }
        }
    }
}
