package ui;

import api.CreateGameRequest;
import api.exception.HttpErrorException;
import serverfacade.ServerFacade;

import java.util.NoSuchElementException;
import java.util.Scanner;

public class Postlogin {
    private final ServerFacade facade;
    private final String user;

    public Postlogin(ServerFacade facade, String user) {
        this.facade = facade;
        this.user = user;
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
//            } else if (command.equals("register")) {
//                handleRegister(input);
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
}
