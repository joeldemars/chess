import chess.*;
import serverfacade.ServerFacade;
import ui.Prelogin;

public class Main {
    public static void main(String[] args) {
//        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
//        System.out.println("♕ 240 Chess Client: " + piece);
        ServerFacade facade = new ServerFacade("http://localhost:8080");
        new Prelogin(facade).start();
    }
}