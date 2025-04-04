package websocket.commands;

import chess.ChessMove;

public class MakeMoveCommand extends UserGameCommand {
    public final String user;
    public final ChessMove move;

    public MakeMoveCommand(String authToken, int gameID, String user, ChessMove move) {
        super(CommandType.MAKE_MOVE, authToken, gameID);
        this.user = user;
        this.move = move;
    }
}
