import chess.*;
import ui.Repl;

public class Main {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);
        try {
            new Repl("http://localhost:8080").run();
        } catch (Exception ex) {
            System.out.println("Failed to run client");
        }
    }
}