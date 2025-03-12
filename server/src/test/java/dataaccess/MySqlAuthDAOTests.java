package dataaccess;

import model.AuthData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class MySqlAuthDAOTests {
    private MySqlAuthDAO auths;

    MySqlAuthDAOTests() throws DataAccessException {
        auths = new MySqlAuthDAO();
    }

    @BeforeEach
    void resetDB() {
        try {
            auths.clearAll();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("Successfully create new auth")
    void createNewAuth() {
        Assertions.assertDoesNotThrow(
                () -> auths.createAuth(new AuthData("FFFFFFFF-FFFF-FFFF-FFFF-FFFFFFFFFFFF", "user")),
                "Failed to create new auth"
        );
    }

    @Test
    @DisplayName("Fail to create duplicate auth")
    void createDuplicateAuth() {
        Assertions.assertDoesNotThrow(
                () -> auths.createAuth(new AuthData("FFFFFFFF-FFFF-FFFF-FFFF-FFFFFFFFFFFF", "user1")),
                "Failed to create new auth"
        );
        Assertions.assertThrows(
                DataAccessException.class,
                () -> auths.createAuth(new AuthData("FFFFFFFF-FFFF-FFFF-FFFF-FFFFFFFFFFFF", "user2")),
                "Created duplicate auth"
        );
    }

    @Test
    @DisplayName("Successfully get created auth")
    void getCreatedAuth() {
        Assertions.assertDoesNotThrow(
                () -> auths.createAuth(new AuthData("FFFFFFFF-FFFF-FFFF-FFFF-FFFFFFFFFFFF", "user")),
                "Failed to create new auth"
        );
        try {
            AuthData auth = auths.getAuth("FFFFFFFF-FFFF-FFFF-FFFF-FFFFFFFFFFFF");
            Assertions.assertEquals("FFFFFFFF-FFFF-FFFF-FFFF-FFFFFFFFFFFF", auth.authToken());
            Assertions.assertEquals("user", auth.username());
        } catch (DataAccessException e) {
            Assertions.fail("Failed to get auth");
        }
    }

    @Test
    @DisplayName("Fail to get nonexistent auth")
    void getNonexistentAuth() {
        Assertions.assertThrows(DataAccessException.class,
                () -> auths.getAuth("FFFFFFFF-FFFF-FFFF-FFFF-FFFFFFFFFFFF"),
                "Returned nonexistent auth"
        );
    }

    @Test
    @DisplayName("Successfully delete created auth")
    void deleteCreatedAuth() {
        Assertions.assertDoesNotThrow(
                () -> auths.createAuth(new AuthData("FFFFFFFF-FFFF-FFFF-FFFF-FFFFFFFFFFFF", "user")),
                "Failed to create new auth"
        );
        Assertions.assertDoesNotThrow(
                () -> auths.deleteAuth(new AuthData("FFFFFFFF-FFFF-FFFF-FFFF-FFFFFFFFFFFF", "user")),
                "Failed to delete auth");
        Assertions.assertThrows(
                DataAccessException.class,
                () -> auths.deleteAuth(new AuthData("FFFFFFFF-FFFF-FFFF-FFFF-FFFFFFFFFFFF", "user")),
                "Failed to delete auth");
    }

    @Test
    @DisplayName("Fail to delete nonexistent auth")
    void deleteNonexistentAuth() {
        Assertions.assertThrows(
                DataAccessException.class,
                () -> auths.deleteAuth(new AuthData("FFFFFFFF-FFFF-FFFF-FFFF-FFFFFFFFFFFF", "user")),
                "Deleted nonexistent auth");
    }

    @Test
    @DisplayName("Successfully clear all auths")
    void clearAllAuths() {
        Assertions.assertDoesNotThrow(
                () -> auths.createAuth(new AuthData("FFFFFFFF-FFFF-FFFF-FFFF-FFFFFFFFFFFF", "user")),
                "Failed to create new auth"
        );
        Assertions.assertDoesNotThrow(
                () -> auths.clearAll(),
                "Failed to clear auths"
        );
        Assertions.assertThrows(DataAccessException.class,
                () -> auths.getAuth("FFFFFFFF-FFFF-FFFF-FFFF-FFFFFFFFFFFF"),
                "Got auth after clearing database");
    }

    @Test
    @DisplayName("Successfully clear empty database")
    void clearEmptyDatabase() {
        Assertions.assertDoesNotThrow(
                () -> auths.clearAll(),
                "Failed to clear empty database"
        );
    }
}
