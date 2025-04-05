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
        String user;
        try {
            gameData = games.getGame(command.getGameID());
            user = auths.getAuth(command.getAuthToken()).username();
        } catch (DataAccessException e) {
            sendError(session, "Error: game not found");
            return;
        }

        if (!rooms.containsKey(command.getGameID())) {
            rooms.put(command.getGameID(), new HashSet<>());
        }
        HashSet<Session> room = rooms.get(command.getGameID());
        String role;
        if (user.equals(gameData.whiteUsername())) {
            role = "the white player.";
        } else if (user.equals(gameData.blackUsername())) {
            role = "the black player.";
        } else {
            role = "an observer.";
        }
        String notification = user + " joined the game as " + role;
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
            String user = auths.getAuth(command.getAuthToken()).username();
            GameData game = games.getGame(command.getGameID());
            if (user.equals(game.whiteUsername())) {
                games.updateGame(command.getGameID(),
                        new GameData(game.gameID(), null, game.blackUsername(), game.gameName(), game.game()));
            } else if (user.equals(game.blackUsername())) {
                games.updateGame(command.getGameID(),
                        new GameData(game.gameID(), game.whiteUsername(), null, game.gameName(), game.game()));
            }
            String notification = user + " left the game.";
            for (Session player : room) {
                sendNotification(player, notification);
            }
        } catch (DataAccessException e) {
            sendError(session, "Error: unable to find game");
        }
    }

    private void handleMakeMove(Session session, MakeMoveCommand command) {
        try {
            GameData gameData = games.getGame(command.getGameID());
            ChessGame game = gameData.game();
            ChessPiece piece = game.getBoard().getPiece(command.move.getStartPosition());
            ChessGame.TeamColor turn = game.getTeamTurn();
            String user = auths.getAuth(command.getAuthToken()).username();
            if (game.getIsOver()) {
                sendError(session, "Error: The game has ended");
                return;
            } else if ((turn == ChessGame.TeamColor.WHITE && !user.equals(gameData.whiteUsername()))
                    || (turn == ChessGame.TeamColor.BLACK && !user.equals(gameData.blackUsername()))) {
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
            String notification = user + " moved " + piece.getPieceType().toString().toLowerCase()
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
            String user = auths.getAuth(command.getAuthToken()).username();
            if (!user.equals(gameData.whiteUsername()) && !user.equals(gameData.blackUsername())) {
                sendError(session, "Error: Only the players can resign");
                return;
            }
            if (game.getIsOver()) {
                sendError(session, "Error: The game is already over");
                return;
            }
            game.setIsOver(true);
            games.updateGame(command.getGameID(), new GameData(
                    gameData.gameID(), gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), game
            ));
            String notification = user + " has resigned.";
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
        game.setIsOver(true);
        GameData gameOver = new GameData(
                gameData.gameID(), gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), game
        );
        try {
            if (game.isInCheckmate(turn)) {
                notification = player + " is in checkmate!";
                games.updateGame(gameData.gameID(), gameOver);
            } else if (game.isInCheck(turn)) {
                notification = player + " is in check!";
            } else if (game.isInStalemate(turn)) {
                notification = player + " is in stalemate!";
                games.updateGame(gameData.gameID(), gameOver);
            } else {
                return;
            }

            for (Session session : rooms.get(gameData.gameID())) {
                sendNotification(session, notification);
            }
        } catch (DataAccessException e) {
            System.out.println("Error: Unable to update game");
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
