package websocket.commands;

import chess.ChessGame;

public class ConnectCommand extends UserGameCommand {
    public final String user;
    public final ChessGame.TeamColor team;

    public ConnectCommand(String authToken, Integer gameID, String user, ChessGame.TeamColor team) {
        super(CommandType.CONNECT, authToken, gameID);
        this.user = user;
        this.team = team;
    }
}
