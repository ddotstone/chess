package service;

import chess.ChessGame;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import dataaccess.*;
import model.request.*;
import model.response.*;

public class GameServiceTests {

    private static DatabaseService databaseService = new DatabaseService(new MemoryAuthDataDAO(),
            new MemoryUserDataDAO(),
            new MemoryGameDataDAO());
    private static GameService gameService = new GameService(new MemoryAuthDataDAO(), new MemoryGameDataDAO());
    ;
    private static UserService userService = new UserService(new MemoryUserDataDAO(), new MemoryAuthDataDAO());
    ;

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
        CreateGameResponse createGameResponse = gameService.createGame(registerResponse.authToken(), createGameRequest);
    }

    @Test
    public void testBadAuthCreateGame() throws DataAccessException {

        // Create Game
        CreateGameRequest createGameRequest = new CreateGameRequest("game");
        Assertions.assertThrows(UnauthorizedException.class, () -> {
            CreateGameResponse createGameResponse = gameService.createGame("not real", createGameRequest);
        });
    }

    @Test
    public void testListGame() throws DataAccessException {

        // Register User
        RegisterRequest registerRequest = new RegisterRequest("username", "password", "email");
        RegisterResponse registerResponse = userService.register(registerRequest);

        // Create Game
        CreateGameRequest createGameRequest = new CreateGameRequest("game");
        CreateGameResponse createGameResponse = gameService.createGame(registerResponse.authToken(), createGameRequest);

        // List Game
        ListGameResponse listGameResponse = gameService.listGames(registerResponse.authToken());

        // Assert Game Name Correct
        Assertions.assertEquals(listGameResponse.games().size(), 1);
        Assertions.assertEquals(listGameResponse.games().iterator().next().gameName(), "game");
    }

    @Test
    public void testBadAuthListGame() throws DataAccessException {

        // List Game
        Assertions.assertThrows(UnauthorizedException.class, () -> {
            ListGameResponse listGameResponse = gameService.listGames("not real");
        });
    }

    @Test
    public void testUpdateGame() throws DataAccessException {

        // Register User
        RegisterRequest registerRequest = new RegisterRequest("username", "password", "email");
        RegisterResponse registerResponse = userService.register(registerRequest);

        // Create Game
        CreateGameRequest createGameRequest = new CreateGameRequest("game");
        CreateGameResponse createGameResponse = gameService.createGame(registerResponse.authToken(), createGameRequest);

        // List Game
        ListGameResponse listGameResponse = gameService.listGames(registerResponse.authToken());

        // Assert Game Name Correct
        Assertions.assertEquals(listGameResponse.games().size(), 1);
        Assertions.assertEquals(listGameResponse.games().iterator().next().gameName(), "game");

        // Join Game
        JoinGameRequest joinGameRequest = new JoinGameRequest(createGameResponse.gameID(), ChessGame.TeamColor.BLACK);
        gameService.joinGame(registerResponse.authToken(), joinGameRequest);

        // List Game
        listGameResponse = gameService.listGames(registerResponse.authToken());

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
        CreateGameResponse createGameResponse = gameService.createGame(registerResponse.authToken(), createGameRequest);

        // List Game
        ListGameResponse listGameResponse = gameService.listGames(registerResponse.authToken());

        // Assert Game Name Correct
        Assertions.assertEquals(listGameResponse.games().size(), 1);
        Assertions.assertEquals(listGameResponse.games().iterator().next().gameName(), "game");

        // Join Game
        JoinGameRequest joinGameRequest = new JoinGameRequest(1, ChessGame.TeamColor.BLACK);
        gameService.joinGame(registerResponse.authToken(), joinGameRequest);

        // List Game
        listGameResponse = gameService.listGames(registerResponse.authToken());

        // Assert Black Player Name Correct
        Assertions.assertEquals(listGameResponse.games().size(), 1);
        Assertions.assertEquals(listGameResponse.games().iterator().next().blackUsername(), "username");

        // Attempt Rejoin Game
        joinGameRequest = new JoinGameRequest(1, ChessGame.TeamColor.BLACK);
        JoinGameRequest finalJoinGameRequest = joinGameRequest;
        Assertions.assertThrows(AlreadyTakenException.class, () -> {
            gameService.joinGame(registerResponse.authToken(), finalJoinGameRequest);
        });
    }
}
