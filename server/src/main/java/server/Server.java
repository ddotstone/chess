package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.NotFoundException;
import handler.*;
import spark.*;
import websocketserver.WebSocketHandler;

import java.util.Map;

public class Server {
    private WebSocketHandler webSocketHandler;

    public Server() {
        webSocketHandler = new WebSocketHandler();
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        this.registerErrorHandler();
        this.registerHandlers();

        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }


    private void registerHandlers() {

        // Session Handlers
        Spark.webSocket("/ws", new WebSocketHandler());
        Spark.post("/session", (req, res) -> (new SessionHandler()).loginHandler(req, res));
        Spark.delete("/session", (req, res) -> (new SessionHandler()).logoutHandler(req, res));

        // Game Handler
        Spark.post("/game", (req, res) -> (new GameHandler()).createGameRequest(req, res));
        Spark.get("/game", (req, res) -> (new GameHandler()).listGameRequest(req, res));
        Spark.put("/game", (req, res) -> (new GameHandler()).joinGameRequest(req, res));

        // User Handlers
        Spark.post("/user", (req, res) -> (new UserHandler()).registerHandler(req, res));

        // DatabaseHandlers
        Spark.delete("/db", (req, res) -> (new DatabaseHandler()).clearRequest(req, res));

    }

    private void registerErrorHandler() {
        Spark.exception(DataAccessException.class, this::errorHandler);
        Spark.notFound((req, res) -> {
            var body = this.errorHandler(new NotFoundException(), req, res);
            return body;
        });

    }


    private Object errorHandler(DataAccessException e, Request req, Response res) {
        var body = new Gson().toJson(Map.of("message", String.format("Error: %s", e.getMessage())));
        res.type("application/json");
        res.status(e.getCode());
        res.body(body);
        return body;
    }
}
