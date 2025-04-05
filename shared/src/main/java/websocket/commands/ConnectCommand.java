package websocket.commands;

import chess.ChessGame;

public class ConnectCommand extends UserGameCommand {
    public final ChessGame.TeamColor team;

    public ConnectCommand(String authToken, Integer gameID, ChessGame.TeamColor team) {
        super(CommandType.CONNECT, authToken, gameID);
        this.team = team;
    }
}
