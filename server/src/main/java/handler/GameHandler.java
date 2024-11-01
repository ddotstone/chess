package handler;

import dataaccess.*;
import request.*;
import response.*;
import service.GameService;
import spark.Request;
import spark.Response;

import static handler.HandlerUnitFunctions.*;

public class GameHandler {
    private final GameService gameService;

    public GameHandler() throws DataAccessException {
        this.gameService = new GameService(new SQLAuthDataDAO(), new MemoryGameDataDAO());
    }

    public Object createGameRequest(Request req, Response res) throws DataAccessException {
        String authToken = getAuth(req);
        CreateGameRequest createGameRequest = deserializeJson(req.body(), CreateGameRequest.class);
        if (createGameRequest.gameName() == null) {
            throw new BadRequestException();
        }
        CreateGameResponse createGameResponse = gameService.createGame(authToken, createGameRequest);
        var body = serializeJson(createGameResponse, CreateGameResponse.class);
        res.body(body);
        res.status(200);
        return body;
    }

    public Object listGameRequest(Request req, Response res) throws DataAccessException {
        String authToken = getAuth(req);
        ListGameResponse listGameResponse = gameService.listGames(authToken);
        var body = serializeJson(listGameResponse, ListGameResponse.class);
        res.body(body);
        res.status(200);
        return body;
    }

    public Object joinGameRequest(Request req, Response res) throws DataAccessException {
        String authToken = getAuth(req);
        JoinGameRequest joinGameRequest = deserializeJson(req.body(), JoinGameRequest.class);
        if (joinGameRequest.playerColor() == null || joinGameRequest.gameID() == 0) {
            throw new BadRequestException();
        }
        gameService.joinGame(authToken, joinGameRequest);
        res.status(200);
        return "{}";
    }
}
