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

    public Object CreateGameRequest(Request req, Response res) throws DataAccessException {
        String authToken = GetAuth(req);
        CreateGameRequest createGameRequest = DeserializeJson(req.body(), CreateGameRequest.class);
        if (createGameRequest.gameName() == null) {
            throw new BadRequestException();
        }
        CreateGameResponse createGameResponse = gameService.CreateGame(authToken, createGameRequest);
        var body = SerializeJson(createGameResponse, CreateGameResponse.class);
        res.body(body);
        res.status(200);
        return body;
    }

    public Object ListGameRequest(Request req, Response res) throws DataAccessException {
        String authToken = GetAuth(req);
        ListGameResponse listGameResponse = gameService.ListGames(authToken);
        var body = SerializeJson(listGameResponse, ListGameResponse.class);
        res.body(body);
        res.status(200);
        return body;
    }

    public Object JoinGameRequest(Request req, Response res) throws DataAccessException {
        String authToken = GetAuth(req);
        JoinGameRequest joinGameRequest = DeserializeJson(req.body(), JoinGameRequest.class);
        gameService.JoinGame(authToken, joinGameRequest);
        res.status(200);
        return "{}";
    }
}
