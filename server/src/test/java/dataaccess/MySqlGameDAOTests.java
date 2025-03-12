package dataaccess;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import model.GameData;
import org.junit.jupiter.api.*;
import spark.utils.Assert;

import java.util.Collection;

public class MySqlGameDAOTests {
    private MySqlGameDAO games;

    MySqlGameDAOTests() throws DataAccessException {
        games = new MySqlGameDAO();
    }

    @BeforeEach
    void resetDB() {
        try {
            games.clearAll();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("Successfully create new game")
    void createNewGame() {
        Assertions.assertDoesNotThrow(
                () -> games.createGame(new GameData(
                        1,
                        "user1",
                        "user2",
                        "Game",
                        new ChessGame())),
                "Failed to create new game"
        );
    }

    @Test
    @DisplayName("Fail to create game with duplicate id")
    void createDuplicateGame() {
        createNewGame();
        Assertions.assertThrows(
                DataAccessException.class,
                () -> games.createGame(new GameData(
                        1,
                        "user1",
                        "user2",
                        "Game 2",
                        new ChessGame())),
                "Created game with duplicate id"
        );
    }

    @Test
    @DisplayName("Successfully get created game")
    void getCreatedGame() {
        createNewGame();
        try {
            GameData game = games.getGame(1);
            Assertions.assertEquals(1, game.gameID(), "Game ID does not match");
            Assertions.assertEquals("user1", game.whiteUsername(), "White username does not match");
            Assertions.assertEquals("user2", game.blackUsername(), "Black username does not match");
            Assertions.assertEquals("Game", game.gameName(), "Game name does not match");
        } catch (DataAccessException e) {
            Assertions.fail("Failed to get game");
        }
    }

    @Test
    @DisplayName("Fail to get nonexistent game")
    void getNonexistentGame() {
        Assertions.assertThrows(DataAccessException.class,
                () -> games.getGame(1),
                "Returned nonexistent game"
        );
    }

    @Test
    @DisplayName("Successfully list no games")
    void listNoGames() {
        Collection<GameData> gameList;
        try {
            gameList = games.listGames();
            Assertions.assertTrue(
                    gameList.isEmpty(),
                    "Listed game when none created"
            );
        } catch (DataAccessException e) {
            Assertions.fail("Failed to list games");
        }
    }

    @Test
    @DisplayName("Successfully list two games")
    void listTwoGames() {
        createNewGame();
        try {
            games.createGame(new GameData(
                    2,
                    "user1",
                    "user2",
                    "Game 2",
                    new ChessGame()));
            try {
                Collection<GameData> gameList = games.listGames();
                Assertions.assertEquals(2, gameList.size(), "Returned wrong number of games");
            } catch (DataAccessException e) {
                Assertions.fail("Failed to list games");
            }
        } catch (DataAccessException e) {
            Assertions.fail("Failed to create game");
        }
    }

    @Test
    @DisplayName("Successfully update game")
    void updateGame() {
        createNewGame();
        try {
            ChessGame newGame = new ChessGame();
            newGame.makeMove(new ChessMove(
                    new ChessPosition(2, 1),
                    new ChessPosition(3, 1),
                    null));
            games.updateGame(1, new GameData(
                    1,
                    "user1",
                    "user2",
                    "Game",
                    newGame
            ));
        } catch (Exception e) {
            Assertions.fail("Failed to update game");
        }
    }

    @Test
    @DisplayName("Fail to update nonexistent game")
    void updateNonexistentGame() {
        Assertions.assertThrows(DataAccessException.class, () -> games.updateGame(
                        1,
                        new GameData(
                                1,
                                "user1",
                                "user2",
                                "Game",
                                new ChessGame())
                ),
                "Updated nonexistent game");
    }

    @Test
    @DisplayName("Successfully clear all games")
    void clearAllGames() {
        createNewGame();
        Assertions.assertDoesNotThrow(
                () -> games.clearAll(),
                "Failed to clear games"
        );
        Assertions.assertThrows(DataAccessException.class,
                () -> games.getGame(1),
                "Got game after clearing database");
    }

    @Test
    @DisplayName("Successfully clear empty database")
    void clearEmptyDatabase() {
        Assertions.assertDoesNotThrow(
                () -> games.clearAll(),
                "Failed to clear empty database"
        );
    }
}
