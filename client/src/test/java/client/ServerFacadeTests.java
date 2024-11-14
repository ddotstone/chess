package client;

import chess.ChessGame;
import org.junit.jupiter.api.*;
import server.Server;
import server.ServerFacade;
import exception.ResponseException;

import static org.junit.jupiter.api.Assertions.assertThrows;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade serverFacade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        serverFacade = new ServerFacade("http://localhost:" + port);
        System.out.println("Started test HTTP server on " + port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeEach
    public void clearDatabase() throws ResponseException {
        serverFacade.clear();
    }

    @Test
    public void signInSuccessful() throws ResponseException {
        String authToken;
        authToken = serverFacade.register("this", "is", "test").authToken();
        serverFacade.signIn("this", "is");
    }

    @Test
    public void signInFail() throws ResponseException {
        serverFacade.register("this", "is", "test").authToken();
        assertThrows(ResponseException.class, () -> {
            serverFacade.signIn("is", "wrong");
        });
    }

    @Test
    public void signOutSuccess() throws ResponseException {
        String authToken;
        authToken = serverFacade.register("this", "is", "test").authToken();
        serverFacade.signOut(authToken);
    }

    @Test
    public void signOutFail() throws ResponseException {
        String authToken;
        authToken = serverFacade.register("this", "is", "test").authToken();
        assertThrows(ResponseException.class, () -> {
            serverFacade.signOut("lolnotatoken");
        });
    }

    @Test
    public void clearSuccess() throws ResponseException {
        serverFacade.clear();
    }

    @Test
    public void createGameSuccess() throws ResponseException {
        String authToken;
        authToken = serverFacade.register("this", "is", "test").authToken();
        serverFacade.createGame(authToken, "newGame");
    }

    @Test
    public void createGameFail() throws ResponseException {
        String authToken;
        authToken = serverFacade.register("this", "is", "test").authToken();
        assertThrows(ResponseException.class, () -> {
            serverFacade.createGame(authToken, null);
        });
    }

    @Test
    public void joinGameSuccess() throws ResponseException {
        String authToken;
        authToken = serverFacade.register("this", "is", "test").authToken();
        int id = serverFacade.createGame(authToken, "newGame");
        serverFacade.joinGame(authToken, id, ChessGame.TeamColor.BLACK);

    }

    @Test
    public void joinGameFail() throws ResponseException {
        String authToken;
        authToken = serverFacade.register("this", "is", "test").authToken();
        int id = serverFacade.createGame(authToken, "newGame");
        serverFacade.joinGame(authToken, id, ChessGame.TeamColor.BLACK);
        assertThrows(ResponseException.class, () -> {
            serverFacade.joinGame(authToken, id, ChessGame.TeamColor.BLACK);
        });
    }

    @Test
    public void listGameSuccess() throws ResponseException {
        String authToken;
        authToken = serverFacade.register("this", "is", "test").authToken();
        int id = serverFacade.createGame(authToken, "newGame");
        serverFacade.listGames(authToken);
    }

    @Test
    public void listGameFail() throws ResponseException {
        String authToken;
        authToken = serverFacade.register("this", "is", "test").authToken();
        int id = serverFacade.createGame(authToken, "newGame");
        assertThrows(ResponseException.class, () -> {
            serverFacade.listGames("badAuth");
        });
    }

    @Test
    public void registerSuccess() throws ResponseException {
        serverFacade.register("this", "is", "test").authToken();
    }

    @Test
    public void registerFail() {
        assertThrows(ResponseException.class, () -> {
            serverFacade.register("null", "null", "test").authToken();
        });
    }
}
