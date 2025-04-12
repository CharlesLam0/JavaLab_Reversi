import model.Player;
import model.Piece;
import java.util.Scanner;

public class GameSetup {
    public static Player[] initializePlayers(Scanner scanner) {
        Player[] players = new Player[2]; // Two players for all boards

        System.out.print("Please enter the first player name (Using the black piece ○): ");
        String player1Name = scanner.nextLine().trim();
        players[0] = new Player(player1Name, Piece.BLACK);

        System.out.print("Please enter the second player name (Using the white piece ●): ");
        String player2Name = scanner.nextLine().trim();
        players[1] = new Player(player2Name, Piece.WHITE);

        return players;
    }
}
