package service;

import chess.ChessGame;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import dataaccess.*;
import request.*;
import response.*;

public class GameServiceTests {

    private static DatabaseService databaseService;
    private static GameService gameService;
    private static UserService userService;

    @BeforeAll
    public static void createDatabase() {
        databaseService = new DatabaseService(new MemoryAuthDataDAO(), new MemoryUserDataDAO(), new MemoryGameDataDAO());
        gameService = new GameService(new MemoryAuthDataDAO(), new MemoryGameDataDAO());
        userService = new UserService(new MemoryUserDataDAO(), new MemoryAuthDataDAO());
    }

    @BeforeEach
    public void clearDatabase() throws DataAccessException {
        databaseService.clear();
    }

    @Test
    public void testCreateGame() throws DataAccessException {

        // Register User
        RegisterRequest registerRequest = new RegisterRequest("username", "password", "email");
        RegisterResponse registerResponse = userService.register(registerRequest);

        // Create Game
        CreateGameRequest createGameRequest = new CreateGameRequest("game");
        CreateGameResponse createGameResponse = gameService.CreateGame(registerResponse.authToken(), createGameRequest);

        // List Game
        ListGameResponse listGameResponse = gameService.ListGames(registerResponse.authToken());

        // Assert Game Name Correct
        Assertions.assertEquals(listGameResponse.games().size(), 1);
        Assertions.assertEquals(listGameResponse.games().iterator().next().gameName(), "game");
    }

    @Test
    public void testBadAuthCreateGame() throws DataAccessException {

        // Create Game
        CreateGameRequest createGameRequest = new CreateGameRequest("game");
        Assertions.assertThrows(UnauthorizedException.class, () -> {
            CreateGameResponse createGameResponse = gameService.CreateGame("not real", createGameRequest);
        });
    }

    public void testListGame() throws DataAccessException {

        // Register User
        RegisterRequest registerRequest = new RegisterRequest("username", "password", "email");
        RegisterResponse registerResponse = userService.register(registerRequest);

        // Create Game
        CreateGameRequest createGameRequest = new CreateGameRequest("game");
        CreateGameResponse createGameResponse = gameService.CreateGame(registerResponse.authToken(), createGameRequest);

        // List Game
        ListGameResponse listGameResponse = gameService.ListGames(registerResponse.authToken());

        // Assert Game Name Correct
        Assertions.assertEquals(listGameResponse.games().size(), 1);
        Assertions.assertEquals(listGameResponse.games().iterator().next().gameName(), "game");
    }

    @Test
    public void testBadAuthListGame() throws DataAccessException {

        // List Game
        Assertions.assertThrows(UnauthorizedException.class, () -> {
            ListGameResponse listGameResponse = gameService.ListGames("not real");
        });
    }

    public void testUpdateGame() throws DataAccessException {

        // Register User
        RegisterRequest registerRequest = new RegisterRequest("username", "password", "email");
        RegisterResponse registerResponse = userService.register(registerRequest);

        // Create Game
        CreateGameRequest createGameRequest = new CreateGameRequest("game");
        CreateGameResponse createGameResponse = gameService.CreateGame(registerResponse.authToken(), createGameRequest);

        // List Game
        ListGameResponse listGameResponse = gameService.ListGames(registerResponse.authToken());

        // Assert Game Name Correct
        Assertions.assertEquals(listGameResponse.games().size(), 1);
        Assertions.assertEquals(listGameResponse.games().iterator().next().gameName(), "game");

        // Join Game
        JoinGameRequest joinGameRequest = new JoinGameRequest(1, ChessGame.TeamColor.BLACK);
        gameService.JoinGame(registerResponse.authToken(), joinGameRequest);

        // List Game
        listGameResponse = gameService.ListGames(registerResponse.authToken());

        // Assert Black Player Name Correct
        Assertions.assertEquals(listGameResponse.games().size(), 1);
        Assertions.assertEquals(listGameResponse.games().iterator().next().blackUsername(), "username");
    }

    @Test
    public void testCannotJoinAlreadyJoinedSpot() throws DataAccessException {

        // Register User
        RegisterRequest registerRequest = new RegisterRequest("username", "password", "email");
        RegisterResponse registerResponse = userService.register(registerRequest);

        // Create Game
        CreateGameRequest createGameRequest = new CreateGameRequest("game");
        CreateGameResponse createGameResponse = gameService.CreateGame(registerResponse.authToken(), createGameRequest);

        // List Game
        ListGameResponse listGameResponse = gameService.ListGames(registerResponse.authToken());

        // Assert Game Name Correct
        Assertions.assertEquals(listGameResponse.games().size(), 1);
        Assertions.assertEquals(listGameResponse.games().iterator().next().gameName(), "game");

        // Join Game
        JoinGameRequest joinGameRequest = new JoinGameRequest(1, ChessGame.TeamColor.BLACK);
        gameService.JoinGame(registerResponse.authToken(), joinGameRequest);

        // List Game
        listGameResponse = gameService.ListGames(registerResponse.authToken());

        // Assert Black Player Name Correct
        Assertions.assertEquals(listGameResponse.games().size(), 1);
        Assertions.assertEquals(listGameResponse.games().iterator().next().blackUsername(), "username");

        // Attempt Rejoin Game
        joinGameRequest = new JoinGameRequest(1, ChessGame.TeamColor.BLACK);
        JoinGameRequest finalJoinGameRequest = joinGameRequest;
        Assertions.assertThrows(AlreadyTakenException.class, () -> {
            gameService.JoinGame(registerResponse.authToken(), finalJoinGameRequest);
        });
    }
}
