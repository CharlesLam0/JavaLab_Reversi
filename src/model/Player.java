package model;

public class Player {
    public final String name;
    public final Piece pieceType;
    private int bombCount = 0; // Default value, will be set appropriately for Gomoku

    public Player(String name, Piece type) {
        this.name = name;
        this.pieceType = type;
    }

    // Getters
    public String getName() {
        return name;
    }

    public String getpieceType() {
        return pieceType.getSymbol();
    }

    public int getBombCount() {
        return bombCount;
    }

    public void setBombCount(int count) {
        this.bombCount = count;
    }

    public void decrementBombCount() {
        if (bombCount > 0) {
            bombCount--;
        }
    }

    public static String PlayerInfo(Player player) {
        return String.format(player.getName(), player.getpieceType());
    }
}