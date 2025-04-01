import java.util.Scanner;

import model.Board;
import model.Piece;
import model.Player;

public interface GameEngine {
    Board board = new Board();
    public Player[] players = new Player[2];
    public Scanner scanner = new Scanner(System.in);
    public int currentPlayerIndice = 0;
    public int passCounter = 0;

    public int getPassCounter();

    public void PassCounterAdd();

    public static int GameID = 0;

    public int getCurrentPlayerIndice();

    public void setCurrentPlayerIndice(int currentPlayerIndice);

    public boolean isGameOver();

    public int getGameID();

    public void setGameID(int id);

    public boolean canPlacePiece(Piece piece);

    public void placePiece(int col, int row, Piece piece);

    public Board getBoard();

    public int getHowManyPieces(Piece piece);
}
