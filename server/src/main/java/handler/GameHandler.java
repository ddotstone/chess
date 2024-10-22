package handler;

import com.google.gson.Gson;

import java.util.Map;

import dataaccess.*;
import request.*;
import response.*;
import service.GameService;
import spark.Request;
import spark.Response;


import static handler.HandlerUnitFunctions.*;

public class GameHandler {
    private final GameService gameService;

    public GameHandler() {
        this.gameService = new GameService(new MemoryAuthDataDAO(), new MemoryGameDataDAO());
    }

    public void CreateGameRequest(Request req, Response res) throws DataAccessException {
        String authToken = GetAuth(req);
        CreateGameRequest createGameRequest = DeserializeJson(req.body(), CreateGameRequest.class);
        if (createGameRequest.gameName() == null) {
            throw new BadRequestException("bad request");
        }
        CreateGameResponse createGameResponse = gameService.CreateGame(authToken, createGameRequest);
        res.body(SerializeJson(createGameResponse, CreateGameResponse.class));
        return;
    }

    public void ListGameRequest(Request req, Response res) throws DataAccessException {
        String authToken = GetAuth(req);
        ListGameResponse createGameResponse = gameService.ListGames(authToken);
        res.body(SerializeJson(createGameResponse, CreateGameResponse.class));
        return;
    }
}
