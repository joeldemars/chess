package dataaccess;

import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import spark.utils.Assert;

public class MySqlUserDAOTests {
    private MySqlUserDAO users = new MySqlUserDAO();

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
        Assertions.fail("Not implemented");
    }

    @Test
    @DisplayName("Fail to create user with duplicate name")
    void createUserWithDuplicateName() {
        Assertions.fail("Not implemented");
    }

    @Test
    @DisplayName("Successfully get created user")
    void getCreatedUser() {
        Assertions.fail("Not implemented");
    }

    @Test
    @DisplayName("Fail to get nonexistent user")
    void getNonexistentUser() {
        Assertions.fail("Not implemented");
    }

    @Test
    @DisplayName("Successfully clear all users")
    void clearAllUsers() {
        Assertions.fail("Not implemented");
    }

    @Test
    @DisplayName("Successfully clear empty database")
    void clearEmptyDatabase() {
        Assertions.fail("Not implemented");
    }
}
