package service;

import dataaccess.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import api.CreateGameRequest;
import api.LoginRequest;
import api.RegisterRequest;

public class ResetServiceTests {
    private UserService userService;
    private GameService gameService;
    private ResetService resetService;

    @BeforeEach
    public void resetService() {
        AuthDAO auths = new MemoryAuthDAO();
        GameDAO games = new MemoryGameDAO();
        UserDAO users = new MemoryUserDAO();

        resetService = new ResetService(users, auths, games);
        userService = new UserService(users, auths);
        gameService = new GameService(games, auths);
    }

    @Test
    @DisplayName("Successfully clear databases")
    public void clearDatabases() {
        RegisterRequest registerRequest =
                new RegisterRequest("user", "secret", "email@mail.com");
        String authToken = userService.register(registerRequest).authToken();
        gameService.createGame(new CreateGameRequest("Game"), authToken);
        resetService.clearDatabase();
        Assertions.assertDoesNotThrow(() -> {
            userService.register(registerRequest);
        }, "Failed to register duplicate user after clearing databases");
        authToken = userService.login(new LoginRequest("user", "secret")).authToken();
        Assertions.assertEquals(0, gameService.listGames(authToken).games().length,
                "Games found after clearing databases");
    }

    @Test
    @DisplayName("Successfully clear empty databases")
    public void clearEmptyDatabases() {
        Assertions.assertDoesNotThrow(() -> {
            resetService.clearDatabase();
            resetService.clearDatabase();
        }, "Failed to clear empty databases");
    }
}
