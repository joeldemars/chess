package ui;

import api.CreateGameRequest;
import api.JoinGameRequest;
import api.exception.HttpErrorException;
import chess.ChessGame;
import model.GameData;
import serverfacade.ServerFacade;

import java.util.NoSuchElementException;
import java.util.Scanner;

public class Postlogin {
    private final ServerFacade facade;
    private final String user;
    private final String authToken;
    private GameData[] games;

    public Postlogin(ServerFacade facade, String user, String authToken) {
        this.facade = facade;
        this.user = user;
        this.authToken = authToken;
    }

    public void start() {
        printHelp();
        while (true) {
            System.out.print(user + " >>> ");
            Scanner input = new Scanner(System.in);
            String command = input.next().trim().toLowerCase();
            if (command.equals("help")) {
                printHelp();
            } else if (command.equals("logout")) {
                try {
                    facade.logout();
                    break;
                } catch (HttpErrorException e) {
                    System.out.println("Error: Failed to log out.");
                }
            } else if (command.equals("create")) {
                handleCreate(input);
            } else if (command.equals("list")) {
                handleList();
            } else if (command.equals("join")) {
                handleJoin(input);
            } else if (command.equals("observe")) {
                handleObserve(input);
            } else {
                System.out.println("Command not recognized.");
                printHelp();
            }
        }
    }

    private void printHelp() {
        System.out.print("Available commands:\n"
                + "help: Print available options\n"
                + "logout: Log out\n"
                + "create " + EscapeSequences.SET_TEXT_ITALIC + "<name>" + EscapeSequences.RESET_TEXT_ITALIC
                + ": Create a new game with the specified name\n"
                + "list: List all games\n"
                + "join " + EscapeSequences.SET_TEXT_ITALIC + "<id> <color>" + EscapeSequences.RESET_TEXT_ITALIC
                + ": Join the game with the given ID as the given color\n"
                + "observe " + EscapeSequences.SET_TEXT_ITALIC + "<id>" + EscapeSequences.RESET_TEXT_ITALIC
                + ": Watch the game with the given id\n");
    }

    private void handleCreate(Scanner input) {
        try {
            String name = input.next();
            facade.createGame(new CreateGameRequest(name));
            System.out.println("Created new game " + name + ".");
        } catch (NoSuchElementException e) {
            System.out.println("Invalid usage.");
            System.out.println("Usage: join " + EscapeSequences.SET_TEXT_ITALIC + "<id> <color>"
                    + EscapeSequences.RESET_TEXT_ITALIC);
        } catch (HttpErrorException e) {
            if (e.status == 400) {
                System.out.println("Error: Game name already taken.");
            } else {
                System.out.println("Error: Failed to create game.");
            }
        }
    }

    private void handleList() {
        try {
            games = facade.listGames().games();
            for (int i = 0; i < games.length; i++) {
                String white = games[i].whiteUsername() != null ? games[i].whiteUsername() : "none";
                String black = games[i].blackUsername() != null ? games[i].blackUsername() : "none";
                System.out.println((i + 1) + ": " + games[i].gameName());
                System.out.println("\tWhite player: " + white);
                System.out.println("\tBlack player: " + black);
            }
        } catch (HttpErrorException e) {
            System.out.println("Error: Failed to list games.");
        }
    }

    private void handleJoin(Scanner input) {
        try {
            games = facade.listGames().games();
            int id = input.nextInt();
            String colorString = input.next().toUpperCase();
            if (id < 1 || id > games.length) {
                System.out.println("Error: game not found.");
                return;
            }
            ChessGame.TeamColor color;
            if (colorString.equals("WHITE")) {
                color = ChessGame.TeamColor.WHITE;
            } else if (colorString.equals("BLACK")) {
                color = ChessGame.TeamColor.BLACK;
            } else {
                System.out.println("Error: Color must be either white or black.");
                return;
            }
            facade.joinGame(new JoinGameRequest(color, games[id - 1].gameID()));
            System.out.println("Joined game " + games[id - 1].gameName() + ".");
            new Gameplay(games[id - 1].game(), color, user, authToken, games[id - 1].gameID()).start();
        } catch (HttpErrorException e) {
            if (e.status == 403) {
                System.out.println("Error: Already taken.");
            } else {
                System.out.println("Error: Failed to join game.");
            }
        } catch (Exception e) {
            System.out.println("Error: Failed to join game.");
        }
    }

    private void handleObserve(Scanner input) {
        try {
            games = facade.listGames().games();
            int id = input.nextInt();
            if (id < 1 || id > games.length) {
                System.out.println("Error: game not found.");
                return;
            }
            System.out.println("Observing game " + games[id - 1].gameName() + ".");
            new Gameplay(games[id - 1].game(), null, user, authToken, games[id - 1].gameID()).start();
        } catch (Exception e) {
            System.out.println("Error: Failed to observe game.");
        }
    }
}
