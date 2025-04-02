package websocket.commands;

import chess.ChessGame;

public class LeaveCommand extends UserGameCommand {
    public String user;
    public ChessGame.TeamColor team;

    public LeaveCommand(String authToken, int gameID, String user, ChessGame.TeamColor team) {
        super(CommandType.LEAVE, authToken, gameID);
        this.user = user;
        this.team = team;
    }
}
