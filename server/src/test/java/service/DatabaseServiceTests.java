package service;

import dataaccess.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import request.*;
import response.*;

public class DatabaseServiceTests {
    private static DatabaseService databaseService;
    private static GameService gameService;
    private static UserService userService;

    @BeforeAll
    public static void createDatabase() {
        databaseService = new DatabaseService(new MemoryAuthDataDAO(), new MemoryUserDataDAO(), new MemoryGameDataDAO());
        gameService = new GameService(new MemoryAuthDataDAO(), new MemoryGameDataDAO());
        userService = new UserService(new MemoryUserDataDAO(), new MemoryAuthDataDAO());
    }

    @Test
    public void testLoadThenClear() throws DataAccessException {
        // Register User
        RegisterRequest registerRequest = new RegisterRequest("username", "password", "email");
        RegisterResponse registerResponse = userService.register(registerRequest);

        // Create Game
        CreateGameRequest createGameRequest = new CreateGameRequest("game");
        CreateGameResponse createGameResponse = gameService.createGame(registerResponse.authToken(), createGameRequest);

        // Clear Database
        databaseService.clear();

        // Assert User was cleared
        LoginRequest loginRequest = new LoginRequest("username", "password");
        Assertions.assertThrows(UnauthorizedException.class, () -> {
            userService.login(loginRequest);
        });

        // Assert Authtoken was cleared

        RegisterResponse finalRegisterResponse = registerResponse;
        Assertions.assertThrows(UnauthorizedException.class, () -> {
            ListGameResponse listGameResponse = gameService.listGames(finalRegisterResponse.authToken());
        });

        // Assert Game was cleared
        registerResponse = userService.register(registerRequest);
        ListGameResponse listGameResponse = gameService.listGames(registerResponse.authToken());
        Assertions.assertEquals(0, listGameResponse.games().size());
    }


}
