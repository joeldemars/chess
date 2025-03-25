package ui;

import serverfacade.ServerFacade;

import java.util.Scanner;

public class Prelogin {
    private final ServerFacade facade;

    public Prelogin(ServerFacade facade) {
        this.facade = facade;
    }

    public static void start(ServerFacade facade) {
        Prelogin prelogin = new Prelogin(facade);
        while (true) {
            Scanner input = getInput();
            String command = input.next().trim();
            if (command.equals("quit")) {
                System.out.println("Exiting...");
                break;
            } else {
                System.out.println("Valid commands: quit");
            }
        }
    }

    private static Scanner getInput() {
        return new Scanner(System.in);
    }

}
