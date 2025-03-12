package dataaccess;

import model.GameData;
import org.junit.jupiter.api.*;

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
        Assertions.fail("Not implemented");
//        Assertions.assertDoesNotThrow(
//                () -> auths.createAuth(new AuthData("FFFFFFFF-FFFF-FFFF-FFFF-FFFFFFFFFFFF", "user")),
//                "Failed to create new auth"
//        );
    }

    @Test
    @DisplayName("Fail to create game with duplicate name")
    void createDuplicateGame() {
        Assertions.fail("Not implemented");
//        Assertions.assertDoesNotThrow(
//                () -> auths.createAuth(new AuthData("FFFFFFFF-FFFF-FFFF-FFFF-FFFFFFFFFFFF", "user1")),
//                "Failed to create new auth"
//        );
//        Assertions.assertThrows(
//                DataAccessException.class,
//                () -> auths.createAuth(new AuthData("FFFFFFFF-FFFF-FFFF-FFFF-FFFFFFFFFFFF", "user2")),
//                "Created duplicate auth"
//        );
    }

    @Test
    @DisplayName("Successfully get created game")
    void getCreatedGame() {
        Assertions.fail("Not implemented");
//        Assertions.assertDoesNotThrow(
//                () -> auths.createAuth(new AuthData("FFFFFFFF-FFFF-FFFF-FFFF-FFFFFFFFFFFF", "user")),
//                "Failed to create new auth"
//        );
//        try {
//            AuthData auth = auths.getAuth("FFFFFFFF-FFFF-FFFF-FFFF-FFFFFFFFFFFF");
//            Assertions.assertEquals("FFFFFFFF-FFFF-FFFF-FFFF-FFFFFFFFFFFF", auth.authToken());
//            Assertions.assertEquals("user", auth.username());
//        } catch (DataAccessException e) {
//            Assertions.fail("Failed to get auth");
//        }
    }

    @Test
    @DisplayName("Fail to get nonexistent game")
    void getNonexistentGame() {
        Assertions.fail("Not implemented");
//        Assertions.assertThrows(DataAccessException.class,
//                () -> auths.getAuth("FFFFFFFF-FFFF-FFFF-FFFF-FFFFFFFFFFFF"),
//                "Returned nonexistent auth"
//        );
    }

    @Test
    @DisplayName("Successfully list no games")
    void listNoGames() {
        Assertions.fail("Not implemented");
    }

    @Test
    @DisplayName("Successfully list three games")
    void listThreeGames() {
        Assertions.fail("Not implemented");
    }

    @Test
    @DisplayName("Successfully update game")
    void updateGame() {
        Assertions.fail("Not implemented");
    }

    @Test
    @DisplayName("Fail to update nonexistent game")
    void updateNonexistentGame() {
        Assertions.fail("Not implemented");
    }

    @Test
    @DisplayName("Successfully clear all games")
    void clearAllGames() {
        Assertions.fail("Not implemented");
//        Assertions.assertDoesNotThrow(
//                () -> auths.createAuth(new AuthData("FFFFFFFF-FFFF-FFFF-FFFF-FFFFFFFFFFFF", "user")),
//                "Failed to create new auth"
//        );
//        Assertions.assertDoesNotThrow(
//                () -> auths.clearAll(),
//                "Failed to clear auths"
//        );
//        Assertions.assertThrows(DataAccessException.class,
//                () -> auths.getAuth("FFFFFFFF-FFFF-FFFF-FFFF-FFFFFFFFFFFF"),
//                "Got auth after clearing database");
    }

    @Test
    @DisplayName("Successfully clear empty database")
    void clearEmptyDatabase() {
        Assertions.fail("Not implemented");
//        Assertions.assertDoesNotThrow(
//                () -> auths.clearAll(),
//                "Failed to clear empty database"
//        );
    }
}
