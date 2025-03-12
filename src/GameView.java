import java.io.IOException;
import java.util.stream.IntStream;

import model.Board;
import model.Player;

public class GameView {
    public static void printBoard(Board board, Player currentPlayer, Player blackPlayer, Player whitePlayer){
        clearConsole();

        System.out.println("  A B C D E F G H");
        for (int i = 0; i < Board.SIZE ; i++){
            System.out.print((i+1) + " ");
            for (int j = 0 ; j < Board.SIZE; j++){
                System.out.print(board.getwhatPiece(i, j).getSymbol() + " ");
            }
            if(i==3){
                System.out.print("    Player [" + blackPlayer.getName() + "]");
                if(currentPlayer == blackPlayer) {
                    System.out.print(blackPlayer.getpieceType());
                }
            }
            if(i==4){
                System.out.print("    Player [" + whitePlayer.getName() + "]" );
                if(currentPlayer == whitePlayer) {
                    System.out.print(whitePlayer.getpieceType());
                }
            }
            System.out.println();
        }
        if (!GameEngine.isGameOver())
        System.out.print("Player " + currentPlayer.getName() + " please enter your move:");


    }

    public static void clearConsole(){
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
        } 
        catch (IOException | InterruptedException e) {
            System.err.println("Clear console failed: " + e.getMessage());
        }
    }
}
