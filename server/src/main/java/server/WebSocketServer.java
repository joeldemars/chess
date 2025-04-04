package server;

import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.ConnectCommand;
import websocket.commands.LeaveCommand;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

@WebSocket
public class WebSocketServer {
    private final Gson gson = new Gson();
    private final AuthDAO auths;
    private final GameDAO games;
    private final HashMap<Integer, HashSet<Session>> rooms = new HashMap<>();

    public WebSocketServer(AuthDAO auths, GameDAO games) {
        this.auths = auths;
        this.games = games;
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) {
        UserGameCommand command = gson.fromJson(message, UserGameCommand.class);
        try {
            if (command.getCommandType() == UserGameCommand.CommandType.CONNECT) {
                handleConnect(session, gson.fromJson(message, ConnectCommand.class));
            } else if (command.getCommandType() == UserGameCommand.CommandType.LEAVE) {
                handleLeave(session, gson.fromJson(message, LeaveCommand.class));
            } else if (command.getCommandType() == UserGameCommand.CommandType.MAKE_MOVE) {
                handleMakeMove(session, gson.fromJson(message, MakeMoveCommand.class));
            }
        } catch (Exception e) {
            System.out.println("Error when handling message: " + e.getMessage());
        }
    }

    private void handleConnect(Session session, ConnectCommand command) throws Exception {
        try {
            auths.getAuth(command.getAuthToken());
        } catch (DataAccessException e) {
            sendError(session, "Error: invalid credentials");
            return;
        }

        if (!rooms.containsKey(command.getGameID())) {
            rooms.put(command.getGameID(), new HashSet<>());
        }
        HashSet<Session> room = rooms.get(command.getGameID());
        String role;
        if (command.team == ChessGame.TeamColor.WHITE) {
            role = "the white player.";
        } else if (command.team == ChessGame.TeamColor.BLACK) {
            role = "the black player.";
        } else {
            role = "an observer.";
        }
        String notification = command.user + " joined the game as " + role;
        for (Session player : room) {
            try {
                sendNotification(player, notification);
            } catch (IOException e) {
            }
        }
        room.add(session);
        sendLoadGame(session, games.getGame(command.getGameID()).game());
    }

    private void handleLeave(Session session, LeaveCommand command) throws Exception {
        HashSet<Session> room = rooms.get(command.getGameID());
        room.remove(session);
        GameData game = games.getGame(command.getGameID());
        if (command.team == ChessGame.TeamColor.WHITE) {
            games.updateGame(command.getGameID(),
                    new GameData(game.gameID(), null, game.blackUsername(), game.gameName(), game.game()));
        } else if (command.team == ChessGame.TeamColor.BLACK) {
            games.updateGame(command.getGameID(),
                    new GameData(game.gameID(), game.whiteUsername(), null, game.gameName(), game.game()));
        }
        String notification = command.user + " left the game.";
        for (Session player : room) {
            try {
                sendNotification(player, notification);
            } catch (IOException e) {
            }
        }
    }

    private void handleMakeMove(Session session, MakeMoveCommand command) throws Exception {
        try {
            GameData gameData = games.getGame(command.getGameID());
            ChessGame game = gameData.game();
            ChessPiece piece = game.getBoard().getPiece(command.move.getStartPosition());
            ChessGame.TeamColor turn = game.getTeamTurn();
            if ((turn == ChessGame.TeamColor.WHITE && !command.user.equals(gameData.whiteUsername()))
                    || (turn == ChessGame.TeamColor.BLACK && !command.user.equals(gameData.blackUsername()))) {
                sendError(session, "Error: It is not your turn!");
                return;
            }
            try {
                game.makeMove(command.move);
            } catch (InvalidMoveException e) {
                sendError(session, "Error: invalid move");
                return;
            }
            games.updateGame(command.getGameID(), new GameData(
                    command.getGameID(), gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), game
            ));
            String notification = command.user + " moved " + piece.getPieceType().toString().toLowerCase()
                    + " from " + command.move.getStartPosition().toAlgebraicNotation() + " to "
                    + command.move.getEndPosition().toAlgebraicNotation();
            for (Session player : rooms.get(command.getGameID())) {
                try {
                    sendLoadGame(player, game);
                    if (player != session) {
                        sendNotification(player, notification);
                    }
                } catch (IOException e) {
                }
            }
            notifyStatus(gameData);
        } catch (DataAccessException e) {
            sendError(session, "Error: Game not found");
        }
    }

    private void notifyStatus(GameData gameData) {
        ChessGame game = gameData.game();
        ChessGame.TeamColor turn = game.getTeamTurn();
        String player = turn == ChessGame.TeamColor.WHITE ? gameData.whiteUsername() : gameData.blackUsername();
        String notification;
        if (game.isInCheckmate(turn)) {
            notification = player + " is in checkmate!";
        } else if (game.isInCheck(turn)) {
            notification = player + " is in check!";
        } else if (game.isInStalemate(turn)) {
            notification = player + " is in stalemate!";
        } else {
            return;
        }
        for (Session session : rooms.get(gameData.gameID())) {
            try {
                sendNotification(session, notification);
            } catch (IOException e) {
            }
        }
    }

    private void sendNotification(Session session, String notification) throws IOException {
        session.getRemote().sendString(gson.toJson(new NotificationMessage(notification)));
    }

    private void sendError(Session session, String errorMessage) throws IOException {
        session.getRemote().sendString(gson.toJson(new ErrorMessage(errorMessage)));
    }

    private void sendLoadGame(Session session, ChessGame game) throws IOException {
        session.getRemote().sendString(gson.toJson(new LoadGameMessage(game)));
    }
}
