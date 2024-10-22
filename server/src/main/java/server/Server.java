package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.NotFoundException;
import handler.*;
import spark.*;

import java.util.Map;

public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        this.RegisterErrorHandler();
        this.RegisterHandlers();

        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }


    private void RegisterHandlers() {

        // Session Handlers
        Spark.post("/session", (req, res) -> (new SessionHandler()).LoginHandler(req, res));
        Spark.delete("/session", (req, res) -> (new SessionHandler()).LogoutHandler(req, res));

        // Game Handler
        Spark.post("/game", (req, res) -> (new GameHandler()).CreateGameRequest(req, res));
        Spark.get("/game", (req, res) -> (new GameHandler()).ListGameRequest(req, res));
        Spark.put("/game", (req, res) -> (new GameHandler()).JoinGameRequest(req, res));

        // User Handlers
        Spark.post("/game", (req, res) -> (new UserHandler()).RegisterHandler(req, res));

        // DatabaseHandlers
        Spark.delete("/session", (req, res) -> (new DatabaseHandler()).ClearRequest(req, res));

    }

    private void RegisterErrorHandler() {
        Spark.exception(DataAccessException.class, this::ErrorHandler);
        Spark.notFound((req, res) -> {
            var body = this.ErrorHandler(new NotFoundException(), req, res);
            return body;
        });

    }


    private Object ErrorHandler(DataAccessException e, Request req, Response res) {
        var body = new Gson().toJson(Map.of("message", String.format("Error: %s", e.getMessage())));
        res.type("application/json");
        res.status(e.getCode());
        res.body(body);
        return body;
    }
}
