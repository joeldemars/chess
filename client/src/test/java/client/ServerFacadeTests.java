package client;

import api.LoginRequest;
import api.LoginResult;
import api.RegisterRequest;
import api.RegisterResult;
import api.exception.ForbiddenException;
import api.exception.HttpErrorException;
import api.exception.UnauthorizedException;
import jdk.jfr.Description;
import org.junit.jupiter.api.*;
import server.Server;
import serverfacade.ServerFacade;

public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade("localhost:0");
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @Test
    @Description("Successfully clear databases")
    public void clearDatabases() {
        facade.register(new RegisterRequest("user", "secret", "mail@email.com"));
        Assertions.assertDoesNotThrow(() -> facade.clear(),
                "Failed to clear database");
        Assertions.assertDoesNotThrow(
                () -> facade.register(new RegisterRequest("user", "secret", "mail@email.com")),
                "Failed to create duplicate user after clearing database");
    }

    @Test
    @Description("Successfully clear empty databases")
    public void clearEmptyDatabases() {
        Assertions.assertDoesNotThrow(() -> facade.clear(),
                "Failed to clear database");
        Assertions.assertDoesNotThrow(() -> facade.clear(),
                "Failed to clear empty database");
    }

    @Test
    @Description("Successfully register new user")
    public void registerUser() {
        try {
            RegisterResult result = facade.register(
                    new RegisterRequest("user", "secret", "mail@email.com"));
            Assertions.assertEquals("user", result.username(), "Username does not match");
        } catch (HttpErrorException e) {
            Assertions.fail("Failed to register new user");
        }
    }

    @Test
    @Description("Fail to register duplicate user")
    public void registerDuplicateUser() {
        facade.register(new RegisterRequest("user", "secret", "mail@email.com"));
        Assertions.assertThrows(ForbiddenException.class,
                () -> facade.register(new RegisterRequest("user", "secret", "mail@email.com")),
                "Registered user with duplicate username");
    }

    @Test
    @Description("Successfully log in as existing user")
    public void logIn() {
        facade.register(new RegisterRequest("user", "secret", "mail@email.com"));
        try {
            LoginResult result = facade.login(new LoginRequest("user", "secret"));
            Assertions.assertEquals("user", result.username(), "Username does not match");
            Assertions.assertNotNull(result.authToken(), "No auth token returned");
        } catch (HttpErrorException e) {
            Assertions.fail("Failed to log in user");
        }
    }

    @Test
    @Description("Fail to log in with incorrect credentials")
    public void logInWithIncorrectCredentials() {
        facade.register(new RegisterRequest("user", "secret", "mail@email.com"));
        Assertions.assertThrows(ForbiddenException.class,
                () -> facade.login(new LoginRequest("user", "notsecret")),
                "User logged in with incorrect credentials");
    }

    @Test
    @Description("Successfully log out")
    public void logOut() {
        RegisterResult result = facade.register(
                new RegisterRequest("user", "secret", "mail@email.com"));
        Assertions.assertDoesNotThrow(() -> facade.logout(result.authToken()),
                "Failed to log out");
    }

    @Test
    @Description("Fail to log out before logging in")
    public void logOutWithoutLoggingIn() {
        Assertions.assertThrows(UnauthorizedException.class,
                () -> facade.logout("00000000-0000-0000-0000-000000000000"),
                "Logged user out with invalid authToken");
    }

    @Test
    @Description("Successfully list one game")
    public void listOneGame() {
        Assertions.fail("Not implemented");
    }

    @Test
    @Description("Fail to list games without logging in")
    public void listGamesWithoutLoggingIn() {
        Assertions.fail("Not implemented");
    }

    @Test
    @Description("Successfully create new game")
    public void createNewGame() {
        Assertions.fail("Not implemented");
    }

    @Test
    @Description("Fail to create duplicate game")
    public void createDuplicateGame() {
        Assertions.fail("Not implemented");
    }

    @Test
    @Description("Successfully join game")
    public void joinGame() {
        Assertions.fail("Not implemented");
    }

    @Test
    @Description("Fail to join nonexistent game")
    public void joinNonexistentGame() {
        Assertions.fail("Not implemented");
    }
}
