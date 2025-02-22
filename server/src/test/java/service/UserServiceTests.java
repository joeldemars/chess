package service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class UserServiceTests {
    private UserService userService;

    @BeforeEach
    public void resetService() {
        userService = new UserService();
    }

    @Test
    @DisplayName("Successfully register new user")
    public void registerNewUser() {

    }

    @Test
    @DisplayName("Fail to register two users with the same username")
    public void registerWithDuplicateUsername() {

    }

    @Test
    @DisplayName("Fail to register with empty password")
    public void registerWithEmptyPassword() {

    }

    @Test
    @DisplayName("Successful login after user created")
    public void createAndLoginUser() {

    }

    @Test
    @DisplayName("Fail to login user without registering first")
    public void loginBeforeRegister() {

    }

    @Test
    @DisplayName("Fail to login with wrong password")
    public void loginWithWrongPassword() {

    }

    @Test
    @DisplayName("Successfully log out after logging in")
    public void logOutAfterLogin() {

    }

    @Test
    @DisplayName("Fail to log out with invalid authorization")
    public void logoutWithInvalidAuthorization() {
        
    }
}
