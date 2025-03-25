package client;

import api.*;
import api.exception.ForbiddenException;
import api.exception.HttpErrorException;
import api.exception.UnauthorizedException;
import chess.ChessGame;
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
        var port = server.run(8000);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade("http://localhost:" + port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeEach
    void resetDatabases() {
        facade.clear();
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
        Assertions.assertThrows(HttpErrorException.class,
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
        Assertions.assertThrows(HttpErrorException.class,
                () -> facade.login(new LoginRequest("user", "notsecret")),
                "User logged in with incorrect credentials");
    }

    @Test
    @Description("Successfully log out")
    public void logOut() {
        facade.register(new RegisterRequest("user", "secret", "mail@email.com"));
        Assertions.assertDoesNotThrow(() -> facade.logout(),
                "Failed to log out");
    }

    @Test
    @Description("Fail to log out before logging in")
    public void logOutWithoutLoggingIn() {
        Assertions.assertThrows(HttpErrorException.class,
                () -> facade.logout(),
                "Logged user out without authorization");
    }

    @Test
    @Description("Successfully list one game")
    public void listOneGame() {
        facade.register(new RegisterRequest("user", "secret", "mail@email.com"));
        facade.createGame(new CreateGameRequest("Game"));
        try {
            ListGamesResult result = facade.listGames();
            Assertions.assertEquals(1, result.games().length, "Found incorrect number of games");
        } catch (HttpErrorException e) {
            Assertions.fail("Failed to list games");
        }
    }

    @Test
    @Description("Fail to list games without logging in")
    public void listGamesWithoutLoggingIn() {
        Assertions.assertThrows(HttpErrorException.class, () -> facade.listGames(),
                "Listed games without logging in");
    }

    @Test
    @Description("Successfully create new game")
    public void createNewGame() {
        facade.register(new RegisterRequest("user", "secret", "mail@email.com"));
        try {
            facade.createGame(new CreateGameRequest("Game"));
        } catch (HttpErrorException e) {
            Assertions.fail("Failed to create game");
        }
    }

    @Test
    @Description("Fail to create duplicate game")
    public void createDuplicateGame() {
        try {
            facade.register(new RegisterRequest("user", "secret", "mail@email.com"));
            facade.createGame(new CreateGameRequest("Game"));
            Assertions.assertThrows(HttpErrorException.class,
                    () -> facade.createGame(new CreateGameRequest("Game")),
                    "Created game with duplicate name");
        } catch (HttpErrorException e) {
            Assertions.fail("Failed to create game");
        }

    }

    @Test
    @Description("Successfully join game")
    public void joinGame() {
        facade.register(new RegisterRequest("user", "secret", "mail@email.com"));
        facade.createGame(new CreateGameRequest("Game"));
        Assertions.assertDoesNotThrow(() -> facade.joinGame(new JoinGameRequest(ChessGame.TeamColor.WHITE, 1)),
                "Failed to join game");
    }

    @Test
    @Description("Fail to join nonexistent game")
    public void joinNonexistentGame() {
        facade.register(new RegisterRequest("user", "secret", "mail@email.com"));
        Assertions.assertThrows(HttpErrorException.class,
                () -> facade.joinGame(new JoinGameRequest(ChessGame.TeamColor.WHITE, 1)),
                "Joined nonexistent game");
    }
}
