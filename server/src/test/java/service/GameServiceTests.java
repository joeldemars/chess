package service;

import model.GameData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class GameServiceTests {
    private GameService gameService;

    @BeforeEach
    public void resetService() {
        gameService = new GameService();
    }

    @Test
    @DisplayName("Successfully list multiple games")
    public void listMultipleGames() {
        gameService.createGame("Game 1");
        gameService.createGame("Game 2");
        ListGamesResult result = gameService.listGames();
        Assertions.assertEquals(2, result.games().length, "Returned incorrect number of games");
    }

    @Test
    @DisplayName("Successfully list zero games")
    public void listZeroGames() {
        ListGamesResult result = gameService.listGames();
        Assertions.assertEquals(0, result.games().length);
    }

    @Test
    @DisplayName("Successfully list one game")
    public void listOneGame() {
        gameService.createGame("Game");
        ListGamesResult result = gameService.listGames();
        Assertions.assertEquals(1, result.games().length, "Returned more or less than one game");
        Assertions.assertEquals("Game", result.games()[0].gameName(), "Game name does not match");
    }

    @Test
    @DisplayName("Successfully create two games")
    public void createGame() {
        int game1 = gameService.createGame("Game 1");
        int game2 = gameService.createGame("Game 2");
        Assertions.assertNotEquals(game1, game2, "Duplicate game ID returned");
    }

    @Test
    @DisplayName("Fail to create game with duplicate name")
    public void createGameWithDuplicateName() {
        gameService.createGame("Game");
        Assertions.assertThrows(BadRequestException.class, () -> {
            gameService.createGame("Game");
        }, "Created game with duplicate name");
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
