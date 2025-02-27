package service;

import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class GameServiceTests {
    private GameService gameService;
    private UserService userService;
    private String authToken;

    @BeforeEach
    public void resetService() {
        MemoryAuthDAO auths = new MemoryAuthDAO();
        gameService = new GameService(new MemoryGameDAO(), auths);
        userService = new UserService(new MemoryUserDAO(), auths);
        authToken = userService
                .register(new RegisterRequest("user", "secret", "email@mail.com"))
                .authToken();
    }

    @Test
    @DisplayName("Successfully list multiple games")
    public void listMultipleGames() {
        gameService.createGame("Game 1", authToken);
        gameService.createGame("Game 2", authToken);
        ListGamesResult result = gameService.listGames(authToken);
        Assertions.assertEquals(2, result.games().length, "Returned incorrect number of games");
    }

    @Test
    @DisplayName("Successfully list zero games")
    public void listZeroGames() {
        ListGamesResult result = gameService.listGames(authToken);
        Assertions.assertEquals(0, result.games().length);
    }

    @Test
    @DisplayName("Successfully list one game")
    public void listOneGame() {
        gameService.createGame("Game", authToken);
        ListGamesResult result = gameService.listGames(authToken);
        Assertions.assertEquals(1, result.games().length, "Returned more or less than one game");
        Assertions.assertEquals("Game", result.games()[0].gameName(), "Game name does not match");
    }

    @Test
    @DisplayName("Fail to list games if not logged in")
    public void listGamesWithoutLogin() {
        Assertions.assertThrows(UnauthorizedException.class, () -> {
            gameService.listGames("00000000-0000-0000-0000-000000000000");
        }, "Listed games without login");
    }

    @Test
    @DisplayName("Successfully create two games")
    public void createGame() {
        int game1 = gameService.createGame("Game 1", authToken);
        int game2 = gameService.createGame("Game 2", authToken);
        Assertions.assertNotEquals(game1, game2, "Duplicate game ID returned");
    }

    @Test
    @DisplayName("Fail to create game with duplicate name")
    public void createGameWithDuplicateName() {
        gameService.createGame("Game", authToken);
        Assertions.assertThrows(BadRequestException.class, () -> {
            gameService.createGame("Game", authToken);
        }, "Created game with duplicate name");
    }

    @Test
    @DisplayName("Fail to create game when not logged in")
    public void createGameWithoutLogin() {
        gameService.createGame("Game", authToken);
        Assertions.assertThrows(BadRequestException.class, () -> {
            gameService.createGame("Game", "00000000-0000-0000-0000-000000000000");
        }, "Created game without login");
    }

    @Test
    @DisplayName("Successfully join game with no players")
    public void joinGameWithNoPlayers() {
        Assertions.fail("Not implemented");
    }

    @Test
    @DisplayName("Successfully join game with one player")
    public void joinGameWithOnePlayers() {
        Assertions.fail("Not implemented");
    }

    @Test
    @DisplayName("Fail to join game with two players")
    public void joinGameWithTwoPlayers() {
        Assertions.fail("Not implemented");
    }

    @Test
    @DisplayName("Fail to join game as second white player")
    public void joinGameAsSecondWhitePlayer() {
        Assertions.fail("Not implemented");
    }

    @Test
    @DisplayName("Fail to join nonexistent game")
    public void joinNonexistentGame() {
        Assertions.fail("Not implemented");
    }
}
