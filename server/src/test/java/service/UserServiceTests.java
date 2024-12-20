package service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import dataaccess.*;
import model.request.*;
import model.response.*;

public class UserServiceTests {
    private static DatabaseService databaseService = new DatabaseService(new MemoryAuthDataDAO(),
            new MemoryUserDataDAO(),
            new MemoryGameDataDAO());
    private static GameService gameService = new GameService(new MemoryAuthDataDAO(), new MemoryGameDataDAO());
    ;
    private static UserService userService = new UserService(new MemoryUserDataDAO(), new MemoryAuthDataDAO());
    ;

    @BeforeAll
    public static void createDatabaseUser() {
        databaseService = new DatabaseService(new MemoryAuthDataDAO(), new MemoryUserDataDAO(), new MemoryGameDataDAO());
        gameService = new GameService(new MemoryAuthDataDAO(), new MemoryGameDataDAO());
        userService = new UserService(new MemoryUserDataDAO(), new MemoryAuthDataDAO());
    }

    @BeforeEach
    public void clearDatabase() throws DataAccessException {
        databaseService.clear();
    }

    @Test
    public void testSuccessRegisterUser() throws DataAccessException {
        // Register User
        RegisterRequest registerRequest = new RegisterRequest("username", "password", "email");
        RegisterResponse registerResponse = userService.register(registerRequest);

        // Assert Login
        LoginRequest loginRequest = new LoginRequest("username", "password");
        LoginResponse loginResponse = userService.login(loginRequest);
    }

    @Test
    public void testSameUsernameRegisterUser() throws DataAccessException {
        // Register User
        RegisterRequest registerRequest = new RegisterRequest("username", "password", "email");
        RegisterResponse registerResponse = userService.register(registerRequest);

        // ReRegister User
        Assertions.assertThrows(AlreadyTakenException.class, () -> {
            RegisterResponse reregisterResponse = userService.register(registerRequest);
        });
    }

    @Test
    public void testSuccessLogin() throws DataAccessException {
        // Register User
        RegisterRequest registerRequest = new RegisterRequest("username", "password", "email");
        RegisterResponse registerResponse = userService.register(registerRequest);

        // Assert Login
        LoginRequest loginRequest = new LoginRequest("username", "password");
        LoginResponse loginResponse = userService.login(loginRequest);

        // Assert Valid Authtoken
        ListGameResponse listGameResponse = gameService.listGames(registerResponse.authToken());
    }


    @Test
    public void testBadLoginFail() throws DataAccessException {
        // Register User
        RegisterRequest registerRequest = new RegisterRequest("username", "password", "email");
        RegisterResponse registerResponse = userService.register(registerRequest);

        // Bad username fail login
        LoginRequest loginRequest = new LoginRequest("name", "password");
        Assertions.assertThrows(UnauthorizedException.class, () -> {
            LoginResponse loginResponse = userService.login(loginRequest);
        });

        // Bad password fail login
        LoginRequest badPassword = new LoginRequest("username", "pass");
        Assertions.assertThrows(UnauthorizedException.class, () -> {
            LoginResponse loginResponse = userService.login(badPassword);
        });
    }

    @Test
    public void testLogout() throws DataAccessException {
        // Register User
        RegisterRequest registerRequest = new RegisterRequest("username", "password", "email");
        RegisterResponse registerResponse = userService.register(registerRequest);

        // Assert Valid Authtoken
        ListGameResponse listGameResponse = gameService.listGames(registerResponse.authToken());

        // Assert Logout Success
        userService.logout(registerResponse.authToken());

        Assertions.assertThrows(UnauthorizedException.class, () -> {
            ListGameResponse listGameResponseFail = gameService.listGames(registerResponse.authToken());
        });
    }

    @Test
    public void testLogoutBadAuth() throws DataAccessException {
        // Assert Logout Fail

        Assertions.assertThrows(UnauthorizedException.class, () -> {
            userService.logout("not real");
        });
    }
}
