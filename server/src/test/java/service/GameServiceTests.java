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
    private String authToken1;
    private String authToken2;
    private String authToken3;

    @BeforeEach
    public void resetService() {
        MemoryAuthDAO auths = new MemoryAuthDAO();
        gameService = new GameService(new MemoryGameDAO(), auths);
        userService = new UserService(new MemoryUserDAO(), auths);
        authToken1 = userService
                .register(new RegisterRequest("user1", "secret1", "email1@mail.com"))
                .authToken();
        authToken2 = userService
                .register(new RegisterRequest("user2", "secret2", "email2@mail.com"))
                .authToken();
        authToken3 = userService
                .register(new RegisterRequest("user3", "secret3", "email3@mail.com"))
                .authToken();
    }

    @Test
    @DisplayName("Successfully list multiple games")
    public void listMultipleGames() {
        gameService.createGame("Game 1", authToken1);
        gameService.createGame("Game 2", authToken1);
        ListGamesResult result = gameService.listGames(authToken1);
        Assertions.assertEquals(2, result.games().length, "Returned incorrect number of games");
    }

    @Test
    @DisplayName("Successfully list zero games")
    public void listZeroGames() {
        ListGamesResult result = gameService.listGames(authToken1);
        Assertions.assertEquals(0, result.games().length);
    }

    @Test
    @DisplayName("Successfully list one game")
    public void listOneGame() {
        gameService.createGame("Game", authToken1);
        ListGamesResult result = gameService.listGames(authToken1);
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
        int game1 = gameService.createGame("Game 1", authToken1);
        int game2 = gameService.createGame("Game 2", authToken1);
        Assertions.assertNotEquals(game1, game2, "Duplicate game ID returned");
    }

    @Test
    @DisplayName("Fail to create game with duplicate name")
    public void createGameWithDuplicateName() {
        gameService.createGame("Game", authToken1);
        Assertions.assertThrows(BadRequestException.class, () -> {
            gameService.createGame("Game", authToken1);
        }, "Created game with duplicate name");
    }

    @Test
    @DisplayName("Fail to create game when not logged in")
    public void createGameWithoutLogin() {
        gameService.createGame("Game", authToken1);
        Assertions.assertThrows(UnauthorizedException.class, () -> {
            gameService.createGame("Game", "00000000-0000-0000-0000-000000000000");
        }, "Created game without login");
    }

    @Test
    @DisplayName("Successfully join game with no players")
    public void joinGameWithNoPlayers() {
        int gameID = gameService.createGame("Game", authToken1);
        Assertions.assertDoesNotThrow(() -> {
            gameService.joinGame(new JoinGameRequest("WHITE", gameID), authToken1);
        }, "Failed to join game with no players");
    }

    @Test
    @DisplayName("Successfully join game with one player")
    public void joinGameWithOnePlayer() {
        int gameID = gameService.createGame("Game", authToken1);
        gameService.joinGame(new JoinGameRequest("WHITE", gameID), authToken1);
        Assertions.assertDoesNotThrow(() -> {
            gameService.joinGame(new JoinGameRequest("BLACK", gameID), authToken2);
        }, "Failed to join game with one player");
    }

    @Test
    @DisplayName("Fail to join game with two players")
    public void joinGameWithTwoPlayers() {
        int gameID = gameService.createGame("Game", authToken1);
        gameService.joinGame(new JoinGameRequest("WHITE", gameID), authToken1);
        gameService.joinGame(new JoinGameRequest("BLACK", gameID), authToken2);
        Assertions.assertThrows(ForbiddenException.class, () -> {
            gameService.joinGame(new JoinGameRequest("WHITE", gameID), authToken3);
        }, "Three players in game");
    }

    @Test
    @DisplayName("Fail to join game as second white player")
    public void joinGameAsSecondWhitePlayer() {
        int gameID = gameService.createGame("Game", authToken1);
        gameService.joinGame(new JoinGameRequest("WHITE", gameID), authToken1);
        Assertions.assertThrows(ForbiddenException.class, () -> {
            gameService.joinGame(new JoinGameRequest("WHITE", gameID), authToken2);
        }, "Two white players in game");
    }

    @Test
    @DisplayName("Fail to join nonexistent game")
    public void joinNonexistentGame() {
        Assertions.assertThrows(BadRequestException.class, () -> {
            gameService.joinGame(new JoinGameRequest("WHITE", 0), authToken1);
        }, "Joined nonexistent game");
    }

    @Test
    @DisplayName("Fail to join game without login")
    public void joinGameWithoutLogin() {
        int gameID = gameService.createGame("Game", authToken1);
        Assertions.assertThrows(UnauthorizedException.class, () -> {
            gameService.joinGame(new JoinGameRequest("WHITE", gameID),
                    "00000000-0000-0000-0000-000000000000");
        }, "Joined game without login");
    }
}
