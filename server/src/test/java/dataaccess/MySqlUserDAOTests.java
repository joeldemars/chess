package dataaccess;

import model.UserData;
import org.junit.jupiter.api.*;

public class MySqlUserDAOTests {
    private MySqlUserDAO users;

    MySqlUserDAOTests() throws DataAccessException {
        users = new MySqlUserDAO();
    }

    @BeforeEach
    void resetDB() {
        try {
            users.clearAll();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("Successfully create new user")
    void createNewUser() {
        Assertions.assertDoesNotThrow(
                () -> users.createUser(new UserData("user", "secret", "email@mail.com")),
                "Failed to create new user"
        );
    }

    @Test
    @DisplayName("Fail to create user with duplicate name")
    void createUserWithDuplicateName() {
        Assertions.assertDoesNotThrow(
                () -> users.createUser(new UserData("user", "secret1", "email1@mail.com")),
                "Failed to create new user"
        );
        Assertions.assertThrows(
                DataAccessException.class,
                () -> users.createUser(new UserData("user", "secret2", "email2@mail.com")),
                "Created user with duplicate username"
        );
    }

    @Test
    @DisplayName("Successfully get created user")
    void getCreatedUser() {
        Assertions.assertDoesNotThrow(
                () -> users.createUser(new UserData("user", "secret", "email@mail.com")),
                "Failed to create new user"
        );
        try {
            UserData user = users.getUser("user");
            Assertions.assertEquals("user", user.username());
            Assertions.assertEquals("email@mail.com", user.email());
        } catch (DataAccessException e) {
            Assertions.fail("Failed to get user " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Fail to get nonexistent user")
    void getNonexistentUser() {
        Assertions.assertThrows(DataAccessException.class,
                () -> users.getUser("user"),
                "Returned nonexistent user"
        );
    }

    @Test
    @DisplayName("Successfully clear all users")
    void clearAllUsers() {
        Assertions.assertDoesNotThrow(
                () -> users.createUser(new UserData("user", "secret1", "email1@mail.com")),
                "Failed to create new user"
        );
        Assertions.assertDoesNotThrow(
                () -> users.clearAll(),
                "Failed to clear database"
        );
        Assertions.assertThrows(DataAccessException.class,
                () -> users.getUser("user"),
                "Returned deleted user"
        );
    }

    @Test
    @DisplayName("Successfully clear empty database")
    void clearEmptyDatabase() {
        Assertions.assertDoesNotThrow(() -> {
                    users.clearAll();
                    users.clearAll();
                },
                "Failed to clear empty database");
    }
}
