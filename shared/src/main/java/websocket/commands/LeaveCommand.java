package websocket.commands;

import chess.ChessGame;

public class LeaveCommand extends UserGameCommand {
    public ChessGame.TeamColor team;

    public LeaveCommand(String authToken, int gameID, ChessGame.TeamColor team) {
        super(CommandType.LEAVE, authToken, gameID);
        this.team = team;
    }
}
