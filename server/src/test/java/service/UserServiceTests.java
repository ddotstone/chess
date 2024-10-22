package service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import dataaccess.*;
import request.*;
import response.*;

public class UserServiceTests {
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
        ListGameResponse listGameResponse = gameService.ListGames(registerResponse.authToken());
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

    public void testLogout() throws DataAccessException {
        // Register User
        RegisterRequest registerRequest = new RegisterRequest("username", "password", "email");
        RegisterResponse registerResponse = userService.register(registerRequest);

        // Assert Valid Authtoken
        ListGameResponse listGameResponse = gameService.ListGames(registerResponse.authToken());

        // Assert Logout Success
        userService.logout(registerResponse.authToken());

        Assertions.assertThrows(UnauthorizedException.class, () -> {
            ListGameResponse listGameResponseFail = gameService.ListGames(registerResponse.authToken());
        });
    }

    public void testLogoutBadAuth() throws DataAccessException {
        // Assert Logout Fail

        Assertions.assertThrows(UnauthorizedException.class, () -> {
            userService.logout("not real");
        });
    }
}
