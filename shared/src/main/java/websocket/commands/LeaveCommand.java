package websocket.commands;

import chess.ChessGame;

public class LeaveCommand extends UserGameCommand {

    public LeaveCommand(String authToken, int gameID) {
        super(CommandType.LEAVE, authToken, gameID);
    }
}
