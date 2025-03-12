package model;

public class Player {
    public final String name;
    public final Piece pieceType;

    public Player(String name, Piece type){
        this.name = name;
        this.pieceType = type;
    }

    //Getters
    public String getName(){
        return name;
    }

    public String getpieceType(){
        return pieceType.getSymbol();
    }

    public static String PlayerInfo(Player player)  {
        return String.format(player.getName() , player.getpieceType());
    }
}