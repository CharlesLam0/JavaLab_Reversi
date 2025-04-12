import java.io.IOException;
import java.util.stream.IntStream;

import model.Board;
import model.Piece;
import model.Player;

public class GameView {

    public static void printBoard(GameEngine engine, Board board, Player currentPlayer, Player blackPlayer,
            Player whitePlayer) {
        clearConsole();

        String[] playername = { blackPlayer.getName(), whitePlayer.getName() };
        String blackplayerStr = null;
        String whiteplayerStr = null;
        String blackPlayerScore = null;
        String whitePlayerScore = null;

        if (engine instanceof Reversi) {
            whitePlayerScore = Integer.toString(GameManager.getCurrentGame().getHowManyPieces(whitePlayer.pieceType));
            blackPlayerScore = Integer.toString(GameManager.getCurrentGame().getHowManyPieces(blackPlayer.pieceType));
        }

        whiteplayerStr = String.format("Player [%s] ", whitePlayer.getName());
        blackplayerStr = String.format("Player [%s] ", blackPlayer.getName());

        int nameLengthDiff = Math.max(blackplayerStr.length(), whiteplayerStr.length())
                - Math.min(blackplayerStr.length(),
                        whiteplayerStr.length());
        if (nameLengthDiff > 0) {
            if (blackplayerStr.length() > whiteplayerStr.length()) {
                whiteplayerStr = String.format("%s%s", whiteplayerStr, " ".repeat(nameLengthDiff));
            } else {
                blackplayerStr = String.format("%s%s", blackplayerStr, " ".repeat(nameLengthDiff));
            }
        }

        if (currentPlayer == whitePlayer) {
            whiteplayerStr = String.format("%s" + whitePlayer.getpieceType(), whiteplayerStr);
        } else if (currentPlayer == blackPlayer) {
            blackplayerStr = String.format("%s" + blackPlayer.getpieceType(), blackplayerStr);
        }

        System.out.println("  A B C D E F G H");
        for (int i = 0; i < Board.SIZE; i++) {
            String left = buildLeftSection(engine, board, i);
            String middle = buildMiddleSection(engine, blackPlayerScore, whitePlayerScore, blackplayerStr,
                    whiteplayerStr, i);
            String right = buildRightSection(i);

            System.out.printf("%-25s %-30s %-50s%n", left, middle, right);

        }

        boolean allGamesOver = true;
        for (GameEngine game : GameManager.games) {
            if (!game.isGameOver()) {
                allGamesOver = false;
                break;
            }
        }

        if (engine instanceof Reversi) {
            if (engine.getBoard().isBoardFull() || (engine.isGameOver() && engine.getPassCounter() == 2)) {
                System.out.println("Game " + (engine.getGameID() + 1) + " is over now.");
                whiteplayerStr = String.format("Player [%s] ", whitePlayer.getName());
                blackplayerStr = String.format("Player [%s] ", blackPlayer.getName());
                if (GameManager.getCurrentGame() instanceof Reversi) {
                    engine = GameManager.getCurrentGame();
                    System.out.printf("%-13s%-5s%s", blackplayerStr, Piece.BLACK.getSymbol(), blackPlayerScore);
                    System.out.println();
                    System.out.printf("%-13s%-5s%s", whiteplayerStr, Piece.WHITE.getSymbol(), whitePlayerScore);
                    System.out.println();
                    if (engine.getHowManyPieces(Piece.BLACK) > engine.getHowManyPieces(Piece.WHITE)) {
                        System.out.println("Player [" + blackPlayer.getName() + "]  wins!");
                    } else if (engine.getHowManyPieces(Piece.BLACK) < engine.getHowManyPieces(Piece.WHITE)) {
                        System.out.println("Player [" + whitePlayer.getName() + "]  wins!");
                    } else {
                        System.out.println("It's a tie!");
                    }
                }
                if (allGamesOver) {
                    System.out.println("All Games over! All the boards are full.");
                    System.out.print("Please enter a new game (peace / reversi / gomoku) / quit the game (quit) : ");
                } else {
                    System.out.println(
                            "Please enter another board number to continue / new game (peace / reversi / gomoku) / quit the game (quit) : ");
                }

            } else {
                if (!engine.canPlacePiece(currentPlayer.pieceType) && engine.getPassCounter() < 2) {
                    System.out.printf("Player " + currentPlayer.getName() +
                            ", you do not have place for your piece, pleace enter pass / game number (1-%d) / new game (peace / reversi / gomoku) / quit : ",
                            GameManager.getNumberOfGames());
                    return;
                } else if (engine.getPassCounter() < 2) {
                    System.out.printf("Player " + currentPlayer.getName() +
                            ", please enter your move (1-8,a-h) / game number (1-%d) / new game (peace / reversi / gomoku) / quit the game (quit) : ",
                            GameManager.getNumberOfGames());
                }
            }
        } else {
            if (engine.isGameOver()) {
                System.out.print("Game " + (engine.getGameID() + 1) + " is over now.");
                if (engine instanceof Gomoku) {
                    if (engine.getBoard().isBoardFull()) {
                        System.out.println(" It's a tie!");
                    } else {
                        System.out.println(
                                " Player [" + playername[(engine.getCurrentPlayerIndice() + 1) % 2] + "] wins!");
                    }
                } else {
                    System.out.println();
                }
                if (allGamesOver) {
                    System.out.println("All Games over! All the boards are full.");
                    System.out.print("Please enter a new game (peace / reversi / gomoku) / quit the game (quit) : ");
                } else {
                    System.out.println(
                            "Please enter another board number to continue / new game (peace / reversi / gomoku) / quit the game (quit) : ");
                }
            } else {
                System.out.printf("Player " + currentPlayer.getName() +
                        ", please enter your move (1-8,a-h) / game number (1-%d) / new game (peace / reversi / gomoku) / quit the game (quit) : ",
                        GameManager.getNumberOfGames());
            }
        }
    }

    private static String buildLeftSection(GameEngine engine, Board board, int row) {
        StringBuilder left = new StringBuilder();
        left.append((row + 1) + " ");
        for (int j = 0; j < Board.SIZE; j++) {
            if (engine instanceof Peace && board.getWhatPiece(row, j) == Piece.CANPLACE) {
                left.append(Piece.EMPTY.getSymbol() + " ");
            } else {
                left.append(board.getWhatPiece(row, j).getSymbol() + " ");
            }
        }
        return left.toString();
    }

    private static String buildMiddleSection(GameEngine engine, String blackPlayerScore, String whitePlayerScore,
            String blackplayerStr, String whiteplayerStr, int row) {
        StringBuilder middle = new StringBuilder();
        if (row == 2) {
            middle.append("Current Game: ").append(engine.getGameID() + 1);
        } else if (row == 3) {
            if (blackPlayerScore != null) {
                middle.append(String.format("%-17s %s", blackplayerStr, blackPlayerScore));
            } else {
                middle.append(String.format("%-17s", blackplayerStr));
            }
        } else if (row == 4) {
            if (whitePlayerScore != null) {
                middle.append(String.format("%-17s %s", whiteplayerStr, whitePlayerScore));
            } else {
                middle.append(String.format("%-17s", whiteplayerStr));
            }
        } else if (row == 5) {
            if (engine instanceof Gomoku) {
                middle.append(String.format("Current Round : %d", engine.getHowManyPieces(null) / 2 + 1));
            }
        }
        return middle.toString();
    }

    private static String buildRightSection(int row) {
        StringBuilder right = new StringBuilder();
        if (row == 1) {
            right.append("Game List");
        }
        int gameIndex = row - 1;
        for (int i = 0; i < GameManager.getNumberOfGames() / 6 + 1; i++) {
            if (row > 1 && row - 1 <= GameManager.games.size()) {
                int index = i * 6 + gameIndex - 1;
                if (index >= GameManager.games.size() || GameManager.games.get(index) == null) {
                    break;
                }
                String temp = Integer.toString(i * 6 + gameIndex);
                temp = temp + ". ";
                right.append(String.format("%-4s%-10s", temp,
                        GameManager.games.get(index).getClass().getSimpleName()));
            }
        }
        return right.toString();

    }

    public static void clearConsole() {
        try {
            final String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("win")) {
                // Windows: Use "cls" command
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                // macOS/Linux: Use ANSI escape code
                System.out.print("\033[H\033[2J");
                System.out.flush();
                IntStream.range(0, 50).forEach(i -> System.out.println());
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("Clear console failed: " + e.getMessage());
        }
    }
}
