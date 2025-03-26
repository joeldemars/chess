package ui;

import api.LoginRequest;
import api.RegisterRequest;
import api.exception.HttpErrorException;
import serverfacade.ServerFacade;

import java.util.NoSuchElementException;
import java.util.Scanner;

public class Prelogin {
    private final ServerFacade facade;

    public Prelogin(ServerFacade facade) {
        this.facade = facade;
    }

    public void start() {
        printHelp();
        while (true) {
            System.out.print("Not logged in >>> ");
            Scanner input = new Scanner(System.in);
            String command = input.next().trim().toLowerCase();
            if (command.equals("help")) {
                printHelp();
            } else if (command.equals("quit")) {
                break;
            } else if (command.equals("login")) {
                handleLogin(input);
            } else if (command.equals("register")) {
                handleRegister(input);
            } else {
                System.out.println("Command not recognized.");
                printHelp();
            }
        }
    }

    private Scanner getInput() {
        return new Scanner(System.in);
    }

    private static void printHelp() {
        System.out.print("Available commands:\n"
                + "help: Print available options\n"
                + "quit: Exit chess\n"
                + "login " + EscapeSequences.SET_TEXT_ITALIC + "<username> <password>"
                + EscapeSequences.RESET_TEXT_ITALIC + ": Log in\n"
                + "register " + EscapeSequences.SET_TEXT_ITALIC + "<username> <password> <email>"
                + EscapeSequences.RESET_TEXT_ITALIC + ": Create a new account\n");
    }

    private void handleLogin(Scanner input) {
        try {
            String username = input.next();
            String password = input.next();
            facade.login(new LoginRequest(username, password));
            new Postlogin(facade, username).start();
            printHelp();
        } catch (NoSuchElementException e) {
            System.out.println("Invalid usage.");
            System.out.println("Usage: login " + EscapeSequences.SET_TEXT_ITALIC + "<username> <password>"
                    + EscapeSequences.RESET_TEXT_ITALIC);
        } catch (HttpErrorException e) {
            if (e.status == 401) {
                System.out.println("Error: Invalid credentials.");
            } else {
                System.out.println("Error: Could not log in.");
            }
        }
    }

    private void handleRegister(Scanner input) {
        try {
            String username = input.next();
            String password = input.next();
            String email = input.next();
            facade.register(new RegisterRequest(username, password, email));
            System.out.println("Successfully created new user " + username + ".");
            new Postlogin(facade, username).start();
        } catch (NoSuchElementException e) {
            System.out.println("Invalid usage.");
            System.out.println("Usage: register " + EscapeSequences.SET_TEXT_ITALIC + "<username> <password> <email>"
                    + EscapeSequences.RESET_TEXT_ITALIC);
        } catch (HttpErrorException e) {
            if (e.status == 403) {
                System.out.println("Error: Username already taken.");
            } else {
                System.out.println("Error: Could not register.");
            }
        }
    }
}
