package service;

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

}
