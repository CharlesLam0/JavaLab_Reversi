package model;

public enum Piece {
    EMPTY("·"),
    BLACK("○"),
    WHITE("●"),
    CANPLACE("+"),
    BARRIER("#"),
    CRATER("@");

    private final String symbol;

    private Piece(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }
}