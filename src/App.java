import model.Player;
import java.util.Scanner;

public class App {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Initialize players once for all boards using GameSetup
        Player[] players = GameSetup.initializePlayers(scanner);

        // Start the game with the first board by default
        GameEngine game = new GameEngine(players[0], players[1], scanner);
        game.startGame();

        scanner.close();
    }
}
