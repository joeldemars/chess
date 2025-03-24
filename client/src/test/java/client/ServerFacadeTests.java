package client;

import jdk.jfr.Description;
import org.junit.jupiter.api.*;
import server.Server;


public class ServerFacadeTests {

    private static Server server;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @Test
    @Description("Successfully clear databases")
    public void clearDatabases() {
        Assertions.fail("Not implemented");
    }

    @Test
    @Description("Successfully clear empty databases")
    public void clearEmptyDatabases() {
        Assertions.fail("Not implemented");
    }

    @Test
    @Description("Successfully register new user")
    public void registerUser() {
        Assertions.fail("Not implemented");
    }

    @Test
    @Description("Fail to register duplicate user")
    public void registerDuplicateUser() {
        Assertions.fail("Not implemented");
    }

    @Test
    @Description("Successfully log in as existing user")
    public void logIn() {
        Assertions.fail("Not implemented");
    }

    @Test
    @Description("Fail to log in with incorrect credentials")
    public void logInWithIncorrectCredentials() {
        Assertions.fail("Not implemented");
    }

    @Test
    @Description("Successfully log out")
    public void logOut() {
        Assertions.fail("Not implemented");
    }

    @Test
    @Description("Fail to log out before logging in")
    public void logOutWithoutLoggingIn() {
        Assertions.fail("Not implemented");
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
