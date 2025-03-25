package ui;

import chess.ChessGame;

import java.util.Scanner;

public class Gameplay {
    private ChessGame game;
    private ChessGame.TeamColor team;

    public Gameplay(ChessGame game, ChessGame.TeamColor team) {
        this.game = game;
        this.team = team;
    }

    public void start() {
        printHelp();
        while (true) {
            System.out.print(">>> ");
            Scanner input = new Scanner(System.in);
            String command = input.next().trim().toLowerCase();
            if (command.equals("help")) {
                printHelp();
            } else {
                System.out.println("Command not recognized.");
                printHelp();
            }
        }
    }

    private void printHelp() {
        System.out.print("Available commands:\n"
                + "help: Print available options\n"
                + "board: Print the board\n");
    }
}
