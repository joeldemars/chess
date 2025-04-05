package ui;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import com.google.gson.Gson;
import websocket.commands.*;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import javax.websocket.*;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.NoSuchElementException;
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
        session.getBasicRemote().sendText(gson.toJson(new ConnectCommand(authToken, gameID)));
        printHelp();
        while (true) {
            System.out.print(">>> ");
            Scanner input = new Scanner(System.in);
            String command = input.next().trim().toLowerCase();
            if (command.equals("help")) {
                printHelp();
            } else if (command.equals("board")) {
                printBoard(null);
            } else if (command.equals("leave")) {
                session.getBasicRemote().sendText(gson.toJson(new LeaveCommand(authToken, gameID)));
                break;
            } else if (command.equals("move") && team != null) {
                handleMove(input);
            } else if (command.equals("resign") && team != null) {
                handleResign();
            } else if (command.equals("highlight")) {
                handleHighlight(input);
            } else {
                System.out.println("Command not recognized.");
                printHelp();
            }
        }
    }

    private void printHelp() {
        System.out.println("Available commands:\n"
                + "help: Print available options\n"
                + "board: Print the board\n"
                + "leave: Leave game and return to game selection");
        if (team != null) {
            System.out.println("move " + EscapeSequences.SET_TEXT_ITALIC + "<start> <end>"
                    + EscapeSequences.RESET_TEXT_ITALIC + ": Move the piece from the start position "
                    + "to the end position\n"
                    + "resign: Resign from the game");
        }
        System.out.println("highlight " + EscapeSequences.SET_TEXT_ITALIC + "<start>"
                + EscapeSequences.RESET_TEXT_ITALIC + ": Highlight all legal moves "
                + "starting at the selected square");
    }

    private void handleMove(Scanner input) {
        try {
            String start = input.next();
            String end = input.next();

            ChessPosition startPosition = parsePosition(start);
            ChessPosition endPosition = parsePosition(end);
            ChessPiece.PieceType promotionPiece = null;

            boolean promotionMove = game.getBoard().getPiece(startPosition) != null
                    && game.getBoard().getPiece(startPosition).getPieceType() == ChessPiece.PieceType.PAWN
                    && (endPosition.getRow() == 1 || endPosition.getRow() == 8);
            if (promotionMove) {
                promotionPiece = getPromotionPiece();
            }

            ChessMove move = new ChessMove(startPosition, endPosition, promotionPiece);

            session.getBasicRemote().sendText(gson.toJson(new MakeMoveCommand(authToken, gameID, move)));
        } catch (Exception e) {
            System.out.println("Invalid usage.");
            System.out.println("Usage: move " + EscapeSequences.SET_TEXT_ITALIC + "<start>"
                    + EscapeSequences.RESET_TEXT_ITALIC);
        }
    }

    private void handleResign() {
        System.out.print("Are you sure you want to resign? [y/N]: ");

        try {
            String input = new Scanner(System.in).next();
            if (input.toLowerCase().charAt(0) == 'y') {
                session.getBasicRemote().sendText(gson.toJson(new ResignCommand(authToken, gameID)));
            }
        } catch (Exception e) {
        }
    }

    private void handleHighlight(Scanner input) {
        try {
            String start = input.next();
            ChessPosition startPosition = parsePosition(start);
            if (game.getBoard().getPiece(startPosition) == null) {
                System.out.println("Error: No piece at indicated position");
            } else {
                printBoard(startPosition);
            }
        } catch (Exception e) {
            System.out.println("Invalid usage");
            System.out.println("Usage: highlight " + EscapeSequences.SET_TEXT_ITALIC + "<start>"
                    + EscapeSequences.RESET_TEXT_ITALIC);
        }
    }

    private void printBoard(ChessPosition highlightPiece) {
        Collection<ChessMove> validMoves;
        if (highlightPiece == null) {
            validMoves = new ArrayList<>();
        } else {
            validMoves = game.validMoves(highlightPiece);
            if (validMoves == null) {
                validMoves = new ArrayList<>();
            }
        }
        Collection<ChessPosition> endPositions = validMoves.stream()
                .map(ChessMove::getEndPosition).toList();
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
                ChessPosition position = new ChessPosition(i + 1, j + 1);
                if (position.equals(highlightPiece)) {
                    System.out.print(EscapeSequences.SET_BG_COLOR_YELLOW);
                } else if ((i + j) % 2 == 0) {
                    if (endPositions.contains(position)) {
                        System.out.print(EscapeSequences.SET_BG_COLOR_DARK_GREEN);
                    } else {
                        System.out.print(EscapeSequences.SET_BG_COLOR_BLACK);
                    }
                } else {
                    if (endPositions.contains(position)) {
                        System.out.print(EscapeSequences.SET_BG_COLOR_GREEN);
                    } else {
                        System.out.print(EscapeSequences.SET_BG_COLOR_WHITE);
                    }
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
            printBoard(null);
            System.out.print(">>> ");
        } else if (m.getServerMessageType() == ServerMessage.ServerMessageType.ERROR) {
            String error = gson.fromJson(message, ErrorMessage.class).getErrorMessage();
            System.out.println("\r" + EscapeSequences.ERASE_LINE + error);
            System.out.print(">>> ");
        }
    }

    private ChessPosition parsePosition(String string) throws RuntimeException {
        try {
            ChessPosition position = new ChessPosition(
                    string.charAt(1) - '0',
                    string.toLowerCase().charAt(0) - 'a' + 1
            );
            if (!position.isValid()) {
                throw new RuntimeException("Invalid position");
            }
            return position;
        } catch (IndexOutOfBoundsException e) {
            throw new RuntimeException("Invalid syntax");
        }
    }

    private ChessPiece.PieceType getPromotionPiece() throws RuntimeException {
        System.out.print("Enter promotion piece [Q/N/R/B]: ");
        Scanner input = new Scanner(System.in);

        char piece = input.next().toLowerCase().charAt(0);

        if (piece == 'q') {
            return ChessPiece.PieceType.QUEEN;
        } else if (piece == 'n') {
            return ChessPiece.PieceType.KNIGHT;
        } else if (piece == 'r') {
            return ChessPiece.PieceType.ROOK;
        } else if (piece == 'b') {
            return ChessPiece.PieceType.BISHOP;
        } else {
            throw new RuntimeException("Invalid piece");
        }
    }
}
