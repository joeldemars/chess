package dataaccess;

import model.UserData;
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
        Assertions.fail("Not implemented");
//        Assertions.assertDoesNotThrow(
//                () -> auths.createAuth(new UserData("user", "secret", "email@mail.com")),
//                "Failed to create new user"
//        );
    }

    @Test
    @DisplayName("Fail to create duplicate auth")
    void createDuplicateAuth() {
        Assertions.fail("Not implemented");
//        Assertions.assertDoesNotThrow(
//                () -> users.createUser(new UserData("user", "secret1", "email1@mail.com")),
//                "Failed to create new user"
//        );
//        Assertions.assertThrows(
//                DataAccessException.class,
//                () -> users.createUser(new UserData("user", "secret2", "email2@mail.com")),
//                "Created user with duplicate username"
//        );
    }

    @Test
    @DisplayName("Successfully get created auth")
    void getCreatedAuth() {
//        Assertions.assertDoesNotThrow(
//                () -> users.createUser(new UserData("user", "secret", "email@mail.com")),
//                "Failed to create new user"
//        );
//        try {
//            UserData user = users.getUser("user");
//            Assertions.assertEquals("user", user.username());
//            Assertions.assertEquals("email@mail.com", user.email());
//        } catch (DataAccessException e) {
//            Assertions.fail("Failed to get user");
//        }
    }

    @Test
    @DisplayName("Fail to get nonexistent auth")
    void getNonexistentAuth() {
        Assertions.fail("Not implemented");
//        Assertions.assertThrows(DataAccessException.class,
//                () -> users.getUser("user"),
//                "Returned nonexistent user"
//        );
    }

    @Test
    @DisplayName("Successfully delete created auth")
    void deleteCreatedAuth() {
        Assertions.fail("Not implemented");
    }

    @Test
    @DisplayName("Fail to delete nonexistent auth")
    void deleteNonexistentAuth() {
        Assertions.fail("Not implemented");
    }

    @Test
    @DisplayName("Successfully clear all auths")
    void clearAllAuths() {
        Assertions.fail("Not implemented");
//        Assertions.assertDoesNotThrow(
//                () -> users.createUser(new UserData("user", "secret1", "email1@mail.com")),
//                "Failed to create new user"
//        );
//        Assertions.assertDoesNotThrow(
//                () -> users.clearAll(),
//                "Failed to clear database"
//        );
//        Assertions.assertThrows(DataAccessException.class,
//                () -> users.getUser("user"),
//                "Returned deleted user"
//        );
    }

    @Test
    @DisplayName("Successfully clear empty database")
    void clearEmptyDatabase() {
        Assertions.fail("Not implemented");
//        Assertions.assertDoesNotThrow(() -> {
//                    users.clearAll();
//                    users.clearAll();
//                },
//                "Failed to clear empty database");
    }
}
