package server;

import dataaccess.*;
import service.GameService;
import service.InternalServerErrorException;
import service.ResetService;
import service.UserService;
import spark.*;

public class Server {
    private UserService userService;
    private GameService gameService;
    private ResetService resetService;

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");
        initializeServices();

        // Register your endpoints and handle exceptions here.
        Spark.delete("/db", this::handleClear);

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private void initializeServices() {
        UserDAO users = new MemoryUserDAO();
        AuthDAO auths = new MemoryAuthDAO();
        GameDAO games = new MemoryGameDAO();

        userService = new UserService(users, auths);
        gameService = new GameService(games, auths);
        resetService = new ResetService(users, auths, games);
    }

    private Object handleClear(Request request, Response response) {
        response.type("application/json");
        try {
            resetService.clearDatabase();
            return "{}";
        } catch (InternalServerErrorException e) {
            return "{\"message\": \"" + e.getMessage() + "\"}";
        }
    }
}
