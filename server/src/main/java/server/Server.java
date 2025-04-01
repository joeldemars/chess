package server;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import dataaccess.*;
import service.*;
import api.exception.BadRequestException;
import api.exception.HttpErrorException;
import api.CreateGameRequest;
import api.JoinGameRequest;
import api.LoginRequest;
import api.RegisterRequest;
import spark.*;

import java.util.function.Supplier;

public class Server {
    private UserService userService;
    private GameService gameService;
    private ResetService resetService;
    private Gson gson = new Gson();

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");
        initializeServices();

        // Register your endpoints and handle exceptions here.
        Spark.webSocket("/ws", WebSocketServer.class);
        Spark.delete("/db", this::handleClear);
        Spark.post("/user", this::handleRegister);
        Spark.post("/session", this::handleLogin);
        Spark.delete("/session", this::handleLogout);
        Spark.get("/game", this::handleListGames);
        Spark.post("/game", this::handleCreateGame);
        Spark.put("/game", this::handleJoinGame);

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private void initializeServices() {
        try {
            UserDAO users = new MySqlUserDAO();
            AuthDAO auths = new MySqlAuthDAO();
            GameDAO games = new MySqlGameDAO();

            userService = new UserService(users, auths);
            gameService = new GameService(games, auths);
            resetService = new ResetService(users, auths, games);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private String handleClear(Request request, Response response) {
        return serializeResponse(response, () -> resetService.clearDatabase());
    }

    private String handleRegister(Request request, Response response) {
        return serializeResponse(response, () -> userService.register(
                serializeRequest(request.body(), RegisterRequest.class)
        ));
    }

    private String handleLogin(Request request, Response response) {
        return serializeResponse(response, () -> userService.login(
                serializeRequest(request.body(), LoginRequest.class)
        ));
    }

    private String handleLogout(Request request, Response response) {
        return serializeResponse(response, () -> userService.logout(request.headers("authorization")));
    }

    private String handleListGames(Request request, Response response) {
        return serializeResponse(response, () -> gameService.listGames(request.headers("authorization")));
    }

    private String handleCreateGame(Request request, Response response) {
        return serializeResponse(response, () -> gameService.createGame(
                serializeRequest(request.body(), CreateGameRequest.class),
                request.headers("authorization"))
        );
    }

    private String handleJoinGame(Request request, Response response) {
        return serializeResponse(response, () -> gameService.joinGame(
                serializeRequest(request.body(), JoinGameRequest.class),
                request.headers("authorization"))
        );
    }

    private <T> T serializeRequest(String body, Class<T> objectClass) {
        try {
            return gson.fromJson(body, objectClass);
        } catch (JsonSyntaxException e) {
            throw new BadRequestException("Error: bad request");
        }
    }

    private String serializeResponse(Response response, Supplier<Object> handler) {
        response.type("application/json");
        try {
            Object result = handler.get();
            response.status(200);
            return new Gson().toJson(result);
        } catch (HttpErrorException e) {
            response.status(e.status);
            return "{\"message\": \"" + e.getMessage() + "\"}";
        }
    }

    private String serializeResponse(Response response, Runnable handler) {
        response.type("application/json");
        try {
            handler.run();
            response.status(200);
            return "{}";
        } catch (HttpErrorException e) {
            response.status(e.status);
            return "{\"message\": \"" + e.getMessage() + "\"}";
        }
    }
}
