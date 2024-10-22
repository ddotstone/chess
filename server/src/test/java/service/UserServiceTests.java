package service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import dataaccess.*;
import request.LoginRequest;
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

}
