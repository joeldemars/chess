package service;

import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryUserDAO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class UserServiceTests {
    private UserService userService;

    @BeforeEach
    public void resetService() {
        userService = new UserService(new MemoryUserDAO(), new MemoryAuthDAO());
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
        userService.register(new RegisterRequest("user", "secret", "email@mail.com"));
        LoginResult result = userService.login(new LoginRequest("user", "secret"));
        Assertions.assertEquals("user", result.username(), "Returned username does not match");
        Assertions.assertNotNull(result.authToken(), "Returned authToken empty");
    }

    @Test
    @DisplayName("Fail to login user without registering first")
    public void loginBeforeRegister() {
        Assertions.assertThrows(UnauthorizedException.class, () -> {
            userService.login(new LoginRequest("user", "secret"));
        }, "User logged in without registering");
    }

    @Test
    @DisplayName("Fail to login with wrong password")
    public void loginWithWrongPassword() {
        userService.register(new RegisterRequest("user", "secret", "email@mail.com"));
        Assertions.assertThrows(UnauthorizedException.class, () -> {
            userService.login(new LoginRequest("user", "notsecret"));
        }, "User logged in with incorrect password");
    }

    @Test
    @DisplayName("Successfully log out after logging in")
    public void logOutAfterLogin() {
        userService.register(new RegisterRequest("user", "secret", "email@mail.com"));
        LoginResult result = userService.login(new LoginRequest("user", "secret"));
        Assertions.assertDoesNotThrow(() -> {
            userService.logout(result.authToken());
        }, "Failed to log user out");
    }

    @Test
    @DisplayName("Fail to log out with invalid authorization")
    public void logoutWithInvalidAuthorization() {
        userService.register(new RegisterRequest("user", "secret", "email@mail.com"));
        userService.login(new LoginRequest("user", "secret"));
        Assertions.assertThrows(UnauthorizedException.class, () -> {
            userService.logout("00000000-0000-0000-0000-000000000000");
        }, "Logged user out with invalid authToken");
    }
}
