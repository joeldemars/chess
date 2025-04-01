package server;

import chess.ChessGame;
import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.ConnectCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.NotificationMessage;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

@WebSocket
public class WebSocketServer {
    private final Gson gson = new Gson();
    private HashMap<Integer, HashSet<Session>> games = new HashMap<>();

    @OnWebSocketMessage
    public void onMessage(Session session, String message) {
        UserGameCommand command = gson.fromJson(message, UserGameCommand.class);
        try {
            if (command.getCommandType() == UserGameCommand.CommandType.CONNECT) {
                handleConnect(session, gson.fromJson(message, ConnectCommand.class));
            }
        } catch (IOException e) {
            System.out.println("Error when handling message: " + e.getMessage());
        }
    }

    private void handleConnect(Session session, ConnectCommand command) throws IOException {
        if (!games.containsKey(command.getGameID())) {
            games.put(command.getGameID(), new HashSet<>());
        }
        HashSet<Session> game = games.get(command.getGameID());
        System.out.println(game.size());
        String role;
        if (command.team == ChessGame.TeamColor.WHITE) {
            role = "white player.";
        } else if (command.team == ChessGame.TeamColor.BLACK) {
            role = "black player.";
        } else {
            role = "observer.";
        }
        String notification = gson.toJson(new NotificationMessage(command.user + " joined game as " + role));
        for (Session player : game) {
            player.getRemote().sendString(notification);
        }
        game.add(session);
    }
}
