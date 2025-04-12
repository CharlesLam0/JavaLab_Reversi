import model.Player;

import java.util.ArrayList;
import java.util.Scanner;

public class GameManager {
    static int currentGame;
    public static ArrayList<GameEngine> games = new ArrayList<>();

    public static void start(Scanner scanner) {
        Player[] players = GameSetup.initializePlayers(scanner);

        games.add(new Peace(players[0], players[1], scanner));
        games.get(0).setGameID(0);
        games.get(0).getBoard().initializeCenter();
        games.add(new Reversi(players[0], players[1], scanner));
        games.get(1).setGameID(1);
        games.get(1).getBoard().initializeCenter();
        games.get(1).canPlacePiece(players[0].pieceType);
        games.add(new Gomoku(players[0], players[1], scanner));
        games.get(2).setGameID(2);

        currentGame = 0;
        GameView.printBoard(games.get(0), games.get(0).getBoard(), players[0], players[0],
                players[1]);

        // System.out.println(games.get(0).getClass().getSimpleName());

        while (true) {
            GameEngine engine = games.get(currentGame);
            int[] input = InputUtils.readValidInput(scanner, engine,
                    players[engine.getCurrentPlayerIndice()].pieceType);
            if (input[1] == -2) {
                System.out.println("Exiting the game.");
                break;
            } else if (input[1] == -4) {
                games.add(new Peace(players[0], players[1], scanner));
                games.get(games.size() - 1).setGameID(games.size() - 1);
                games.get(games.size() - 1).getBoard().initializeCenter();
                GameView.printBoard(engine, engine.getBoard(), players[engine.getCurrentPlayerIndice()],
                        players[0], players[1]);
            } else if (input[1] == -5) {
                games.add(new Reversi(players[0], players[1], scanner));
                games.get(games.size() - 1).setGameID(games.size() - 1);
                games.get(games.size() - 1).getBoard().initializeCenter();
                games.get(games.size() - 1).canPlacePiece(players[0].pieceType);
                GameView.printBoard(engine, engine.getBoard(), players[engine.getCurrentPlayerIndice()],
                        players[0], players[1]);
            } else if (input[1] == -6) {
                games.add(new Gomoku(players[0], players[1], scanner));
                games.get(games.size() - 1).setGameID(games.size() - 1);
                GameView.printBoard(engine, engine.getBoard(), players[engine.getCurrentPlayerIndice()],
                        players[0], players[1]);
            } else if (input[1] == -3) {
                if (engine instanceof Reversi) {
                    if (engine.canPlacePiece(players[engine.getCurrentPlayerIndice()].pieceType)) {
                        System.out.println();
                        System.out.println("You can place a piece. Please try again.");
                        continue;
                    }
                    engine.setCurrentPlayerIndice((engine.getCurrentPlayerIndice() + 1) % 2);
                    engine.PassCounterAdd();
                    engine.canPlacePiece(players[engine.getCurrentPlayerIndice()].pieceType);
                } else {
                    System.out.println();
                    System.out.print("You cannot pass in Peace mode. Please try again.");
                    continue;
                }
                GameView.printBoard(engine, engine.getBoard(), players[engine.getCurrentPlayerIndice()],
                        players[0], players[1]);

            } else if (input[1] == -1) {
                // Input is a board index
                if (input[0] >= 0 && input[0] < games.size()) {
                    currentGame = input[0];
                } else {
                    System.out.println("Invalid game number. Please try again.");
                    continue;
                }
                engine = games.get(currentGame);
                engine.canPlacePiece(players[engine.getCurrentPlayerIndice()].pieceType);
                GameView.printBoard(engine, engine.getBoard(), players[engine.getCurrentPlayerIndice()],
                        players[0], players[1]);
            } else {
                // Input is a move
                int col = input[0];
                int row = input[1];
                // Place the piece since validation is already done in InputUtils
                if (engine instanceof Gomoku) {
                    engine.placePiece(col, row, players[engine.getCurrentPlayerIndice()].pieceType);
                    engine.isLine(col, row, players[engine.getCurrentPlayerIndice()].pieceType);
                    engine.setCurrentPlayerIndice((engine.getCurrentPlayerIndice() + 1) % 2);
                    GameView.printBoard(engine, engine.getBoard(), players[engine.getCurrentPlayerIndice()],
                            players[0], players[1]);

                } else {
                    engine.placePiece(col, row, players[engine.getCurrentPlayerIndice()].pieceType);
                    engine.setCurrentPlayerIndice((engine.getCurrentPlayerIndice() + 1) % 2);
                    engine.canPlacePiece(players[engine.getCurrentPlayerIndice()].pieceType);
                    GameView.printBoard(engine, engine.getBoard(), players[engine.getCurrentPlayerIndice()],
                            players[0], players[1]);
                }
            }
        }

    }

    // Add a method to get the number of games
    public static int getNumberOfGames() {
        return games.size();
    }

    public static GameEngine getCurrentGame() {
        return games.get(currentGame);
    }

}
