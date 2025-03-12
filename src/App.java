import model.Piece;
import model.Player;

import java.util.Scanner;

public class App {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Please enter the first player name（Using the black piece ●）：");
        String Player1 = scanner.nextLine().trim();
                
        System.out.print("Please enter the second player name（Using the white piece ○）：");
        String Player2 = scanner.nextLine().trim();

        Player blackPlayer = new Player(Player1, Piece.BLACK);
        Player whitePlayer = new Player(Player2, Piece.WHITE);

        GameEngine game = new GameEngine(blackPlayer, whitePlayer, scanner);
        game.startGame();

        scanner.close();

    }
}
