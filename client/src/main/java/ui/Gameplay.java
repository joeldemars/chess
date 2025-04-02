package ui;

import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import com.google.gson.Gson;
import websocket.commands.ConnectCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import javax.websocket.*;
import java.net.URI;
import java.util.Scanner;

public class Gameplay extends Endpoint {
    private ChessGame game;
    private final ChessGame.TeamColor team;
    private final String user;
    private final String authToken;
    private final int gameID;
    private Session session;
    private final Gson gson = new Gson();

    public Gameplay(ChessGame game, ChessGame.TeamColor team, String user, String authToken, int gameID) throws Exception {
        this.game = game;
        this.team = team;
        this.user = user;
        this.authToken = authToken;
        this.gameID = gameID;
        session = ContainerProvider.getWebSocketContainer().connectToServer(
                this, new URI("ws://localhost:8080/ws")
        );
        session.addMessageHandler(new MessageHandler.Whole<String>() {
            public void onMessage(String message) {
                handleMessage(message);
            }
        });
    }

    public void onOpen(Session session, EndpointConfig config) {
    }

    public void start() throws Exception {
        session.getBasicRemote().sendText(gson.toJson(new ConnectCommand(authToken, gameID, user, team)));
        printHelp();
        while (true) {
            System.out.print(">>> ");
            Scanner input = new Scanner(System.in);
            String command = input.next().trim().toLowerCase();
            if (command.equals("help")) {
                printHelp();
            } else if (command.equals("board")) {
                printBoard();
            } else if (command.equals("leave")) {
                // TODO: Remove player from game if not observer (i.e. team != null)
                break;
            } else if (command.equals("move")) {
                System.out.println("Unimplemented");
            } else if (command.equals("resign")) {
                System.out.println("Unimplemented");
            } else if (command.equals("highlight")) {
                System.out.println("Unimplemented");
            } else {
                System.out.println("Command not recognized.");
                printHelp();
            }
        }
    }

    private void printHelp() {
        System.out.print("Available commands:\n"
                + "help: Print available options\n"
                + "board: Print the board\n"
                + "leave: Leave game and return to game selection\n"
                + "move " + EscapeSequences.SET_TEXT_ITALIC + "<start> <end>"
                + EscapeSequences.RESET_TEXT_ITALIC + ": Move the piece from the start position "
                + "to the end position\n"
                + "resign: Resign from the game\n"
                + "highlight " + EscapeSequences.SET_TEXT_ITALIC + "<start>"
                + EscapeSequences.RESET_TEXT_ITALIC + ": Highlight all legal moves "
                + "starting at the selected square\n");
    }

    private void printBoard() {
        boolean flipped = team == ChessGame.TeamColor.BLACK;
        printEdge(flipped);
        int start = flipped ? 0 : 7;
        int end = flipped ? 8 : -1;
        for (int i = start; i != end; i += flipped ? 1 : -1) {
            System.out.print(EscapeSequences.SET_BG_COLOR_LIGHT_GREY
                    + EscapeSequences.SET_TEXT_COLOR_BLACK + " " + (i + 1) + " ");
            int startColumn = flipped ? 7 : 0;
            int endColumn = flipped ? -1 : 8;
            for (int j = startColumn; j != endColumn; j += flipped ? -1 : 1) {
                if ((i + j) % 2 == 0) {
                    setBackgroundBlack();
                } else {
                    setBackgroundWhite();
                }
                ChessPiece piece = game.getBoard().getPiece(new ChessPosition(i + 1, j + 1));
                printPiece(piece);
            }
            System.out.print(EscapeSequences.SET_BG_COLOR_LIGHT_GREY
                    + EscapeSequences.SET_TEXT_COLOR_BLACK + " " + (i + 1) + " ");
            System.out.println(EscapeSequences.RESET_BG_COLOR);
        }

        printEdge(flipped);
    }

    private void printEdge(boolean flipped) {
        String columns = flipped ? "hgfedcba" : "abcdefgh";
        System.out.print(EscapeSequences.SET_BG_COLOR_LIGHT_GREY
                + EscapeSequences.SET_TEXT_COLOR_BLACK + "   ");
        for (int i = 0; i < 8; i++) {
            System.out.print(" " + columns.charAt(i) + " ");
        }
        System.out.println("   " + EscapeSequences.RESET_BG_COLOR
                + EscapeSequences.RESET_TEXT_COLOR);
    }

    private void setBackgroundBlack() {
        System.out.print(EscapeSequences.SET_BG_COLOR_BLACK);
    }

    private void setBackgroundWhite() {
        System.out.print(EscapeSequences.SET_BG_COLOR_WHITE);
    }

    private void printPiece(ChessPiece piece) {
        if (piece == null) {
            System.out.print("   ");
        } else {
            System.out.print(EscapeSequences.SET_TEXT_BOLD);
            if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                System.out.print(EscapeSequences.SET_TEXT_COLOR_BLUE);
            } else {
                System.out.print(EscapeSequences.SET_TEXT_COLOR_RED);
            }
            switch (piece.getPieceType()) {
                case KING:
                    System.out.print(" K ");
                    break;
                case QUEEN:
                    System.out.print(" Q ");
                    break;
                case BISHOP:
                    System.out.print(" B ");
                    break;
                case KNIGHT:
                    System.out.print(" N ");
                    break;
                case ROOK:
                    System.out.print(" R ");
                    break;
                case PAWN:
                    System.out.print(" P ");
            }
            System.out.print(EscapeSequences.RESET_TEXT_BOLD_FAINT);
        }
    }

    private void handleMessage(String message) {
        ServerMessage m = gson.fromJson(message, ServerMessage.class);
        if (m.getServerMessageType() == ServerMessage.ServerMessageType.NOTIFICATION) {
            String notification = gson.fromJson(message, NotificationMessage.class).getMessage();
            System.out.println("\r" + EscapeSequences.ERASE_LINE + notification);
            System.out.print(">>> ");
        } else if (m.getServerMessageType() == ServerMessage.ServerMessageType.LOAD_GAME) {
            game = gson.fromJson(message, LoadGameMessage.class).getGame();
            System.out.print("\r" + EscapeSequences.ERASE_LINE);
            printBoard();
            System.out.print(">>> ");
        } else {
            System.out.println("Received message: " + message);
        }
    }
}
