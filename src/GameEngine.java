import java.util.Scanner;

import model.Board;
import model.Piece;
import model.Player;

public class GameEngine {
    private static Board board = new Board();
            private final Player[] players;
            private final Scanner scanner;
            private int currentPlayerIndex;
        
            public GameEngine(Player blackPlayer, Player whitePlayer, Scanner scanner) {
                GameEngine.board = new Board();
            this.players = new Player[]{blackPlayer, whitePlayer};
            this.scanner = scanner;
            this.currentPlayerIndex = 0;
        }
    
        public void startGame() {
            while (!isGameOver()) {
                handleTurn(players[currentPlayerIndex]);
                currentPlayerIndex = (currentPlayerIndex + 1) % 2;
      
            }
            GameView.printBoard(board, players[currentPlayerIndex], players[0], players[1]);
            System.out.println("Game over! The board is full.");
        }
        
        private void handleTurn(Player player) {
            GameView.printBoard(board, players[currentPlayerIndex], players[0], players[1]);
                
            int[] position = InputUtils.readValidInput(scanner, board , players[currentPlayerIndex].pieceType);
            int row = position[0];
            int col = position[1];
                        
            //执行落子
            Board.grid[row][col] = players[currentPlayerIndex].pieceType;
            
        }
    
        static boolean isGameOver() {
            for(int i = 0; i < Board.SIZE ; i++){
                for(int j = 0; j < Board.SIZE ; j++){
                    if (board.getwhatPiece(i, j) == Piece.EMPTY){
                    return false;
                }
            }
        }
        return true;
    }
}