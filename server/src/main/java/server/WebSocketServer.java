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
import websocket.commands.*;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;

import javax.xml.crypto.Data;
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
        if (!authenticate(session, command.getAuthToken())) {
            return;
        }
        if (command.getCommandType() == UserGameCommand.CommandType.CONNECT) {
            handleConnect(session, gson.fromJson(message, ConnectCommand.class));
        } else if (command.getCommandType() == UserGameCommand.CommandType.LEAVE) {
            handleLeave(session, gson.fromJson(message, LeaveCommand.class));
        } else if (command.getCommandType() == UserGameCommand.CommandType.MAKE_MOVE) {
            handleMakeMove(session, gson.fromJson(message, MakeMoveCommand.class));
        } else if (command.getCommandType() == UserGameCommand.CommandType.RESIGN) {
            handleResign(session, gson.fromJson(message, ResignCommand.class));
        }
    }

    private void handleConnect(Session session, ConnectCommand command) {
        if (!authenticate(session, command.getAuthToken())) {
            return;
        }

        GameData gameData;
        try {
            gameData = games.getGame(command.getGameID());
        } catch (DataAccessException e) {
            sendError(session, "Error: game not found");
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
            sendNotification(player, notification);
        }
        room.add(session);
        sendLoadGame(session, gameData.game());
    }

    private void handleLeave(Session session, LeaveCommand command) {
        HashSet<Session> room = rooms.get(command.getGameID());
        room.remove(session);
        try {
            GameData game = games.getGame(command.getGameID());
            if (command.team == ChessGame.TeamColor.WHITE) {
                games.updateGame(command.getGameID(),
                        new GameData(game.gameID(), null, game.blackUsername(), game.gameName(), game.game()));
            } else if (command.team == ChessGame.TeamColor.BLACK) {
                games.updateGame(command.getGameID(),
                        new GameData(game.gameID(), game.whiteUsername(), null, game.gameName(), game.game()));
            }
        } catch (DataAccessException e) {
            sendError(session, "Error: unable to find game");
        }
        String notification = command.user + " left the game.";
        for (Session player : room) {
            sendNotification(player, notification);
        }
    }

    private void handleMakeMove(Session session, MakeMoveCommand command) {
        try {
            GameData gameData = games.getGame(command.getGameID());
            ChessGame game = gameData.game();
            ChessPiece piece = game.getBoard().getPiece(command.move.getStartPosition());
            ChessGame.TeamColor turn = game.getTeamTurn();
            if (game.getIsOver()) {
                sendError(session, "Error: The game has ended");
                return;
            } else if ((turn == ChessGame.TeamColor.WHITE && !command.user.equals(gameData.whiteUsername()))
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
                sendLoadGame(player, game);
                if (player != session) {
                    sendNotification(player, notification);
                }
            }
            notifyStatus(gameData);
        } catch (DataAccessException e) {
            sendError(session, "Error: Game not found");
        }
    }

    private void handleResign(Session session, ResignCommand command) {
        try {
            GameData gameData = games.getGame(command.getGameID());
            ChessGame game = gameData.game();
            if (game.getIsOver()) {
                sendError(session, "Error: The game is already over");
                return;
            }
            game.setIsOver(true);
            games.updateGame(command.getGameID(), new GameData(
                    gameData.gameID(), gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), game
            ));
            String notification = command.user + " has resigned.";
            for (Session player : rooms.get(command.getGameID())) {
                sendNotification(player, notification);
            }
        } catch (DataAccessException e) {
            sendError(session, "Error: Unable to access game");
        }
    }

    private void notifyStatus(GameData gameData) {
        ChessGame game = gameData.game();
        ChessGame.TeamColor turn = game.getTeamTurn();
        String player = turn == ChessGame.TeamColor.WHITE ? gameData.whiteUsername() : gameData.blackUsername();
        String notification;
        if (game.isInCheckmate(turn)) {
            notification = player + " is in checkmate!";
            // Set game over
        } else if (game.isInCheck(turn)) {
            notification = player + " is in check!";
        } else if (game.isInStalemate(turn)) {
            notification = player + " is in stalemate!";
            // set game over
        } else {
            return;
        }
        for (Session session : rooms.get(gameData.gameID())) {
            sendNotification(session, notification);
        }
    }

    private boolean authenticate(Session session, String authToken) {
        try {
            auths.getAuth(authToken);
            return true;
        } catch (DataAccessException e) {
            sendError(session, "Error: invalid credentials");
            return false;
        }
    }

    private void sendNotification(Session session, String notification) {
        try {
            session.getRemote().sendString(gson.toJson(new NotificationMessage(notification)));
        } catch (IOException e) {
            System.out.println("Failed to send websocket message: " + e.getMessage());
        }

    }

    private void sendError(Session session, String errorMessage) {
        try {
            session.getRemote().sendString(gson.toJson(new ErrorMessage(errorMessage)));
        } catch (IOException e) {
            System.out.println("Failed to send websocket message: " + e.getMessage());
        }
    }

    private void sendLoadGame(Session session, ChessGame game) {
        try {
            session.getRemote().sendString(gson.toJson(new LoadGameMessage(game)));
        } catch (IOException e) {
            System.out.println("Failed to send websocket message: " + e.getMessage());
        }
    }
}
