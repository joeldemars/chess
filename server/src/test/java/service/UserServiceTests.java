package service;

import org.junit.jupiter.api.Assertions;
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
        RegisterResult result =
                userService.register(new RegisterRequest("user", "secret", "email@mail.com"));
        Assertions.assertEquals("user", result.username(), "Returned username does not match");
        Assertions.assertNotNull(result.authToken(), "Returned authToken empty");
    }

    @Test
    @DisplayName("Fail to register two users with the same username")
    public void registerWithDuplicateUsername() {
        userService.register(new RegisterRequest("user", "secret1", "email1@mail.com"));
        Assertions.assertThrows(ForbiddenException.class, () -> {
            userService.register(new RegisterRequest("user", "secret2", "email2@mail.com"));
        }, "User registered with duplicated username");
    }

    @Test
    @DisplayName("Fail to register with empty email")
    public void registerWithEmptyPassword() {
        Assertions.assertThrows(BadRequestException.class, () -> {
            userService.register(new RegisterRequest("user", "secret", ""));
        }, "User registered with empty email");
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
