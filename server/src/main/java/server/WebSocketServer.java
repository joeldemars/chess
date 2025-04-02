package server;

import chess.ChessGame;
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
