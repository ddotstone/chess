package ui.client;

import java.util.Arrays;

import chess.ChessGame;
import websocketClient.NotificationHandler;
import exception.ResponseException;
import model.AuthData;
import connection.ServerFacade;

public class SignedOutChessClient implements ChessClient {
    String authToken;
    String url;
    ServerFacade serverFacade;
    ChessGame.TeamColor teamColor;
    Class transferClass;
    NotificationHandler notificationHandler;

    public SignedOutChessClient(String url, NotificationHandler notificationHandler) {
        serverFacade = new ServerFacade(url);
        authToken = null;
        this.url = url;
        this.notificationHandler = notificationHandler;
    }

    public SignedOutChessClient(SignedInChessClient copy) {
        this.authToken = copy.authToken;
        this.serverFacade = copy.serverFacade;
        this.notificationHandler = copy.notificationHandler;
        this.url = copy.url;
    }


    public SignedOutChessClient(InGameChessClient copy) {
        this.authToken = copy.authToken;
        this.serverFacade = copy.serverFacade;
        this.notificationHandler = copy.notificationHandler;
        this.url = copy.url;
    }

    public String getState() {
        return "<signed in>";
    }

    public String eval(String input) throws ResponseException {
        var tokens = input.toLowerCase().split(" ");
        var cmd = (tokens.length > 0) ? tokens[0] : "help";
        var params = Arrays.copyOfRange(tokens, 1, tokens.length);
        return switch (cmd) {
            case "login" -> signIn(params);
            case "register" -> register(params);
            case "clear" -> clear(params);
            case "help" -> help();
            case "quit" -> "quit";
            default -> throw new ResponseException(400, "Invalid Command");
        };
    }

    private String signIn(String... params) throws ResponseException {
        if (params.length == 2) {
            String username = params[0];
            String password = params[1];
            AuthData authData = serverFacade.signIn(username, password);
            this.authToken = authData.authToken();
            transferClass = SignedInChessClient.class;

            return String.format("signed in as: %s", username);
        }
        throw new ResponseException(400, "Expected: login <username> <password>");
    }

    private String register(String... params) throws ResponseException {
        if (params.length == 3) {
            String username = params[0];
            String password = params[1];
            String email = params[2];
            AuthData authData = serverFacade.register(username, password, email);
            this.authToken = authData.authToken();
            transferClass = SignedInChessClient.class;
            return String.format("registered as: %s", username);
        }
        throw new ResponseException(400, "Expected: register <username> <password> <email>");
    }

    private String clear(String... params) throws ResponseException {
        serverFacade.clear();
        return "cleared database";
    }

    public String help() {
        return """
                Options:
                Login: "login" <USERNAME> <PASSWORD>
                Register a new user: "register" <USERNAME> <PASSWORD> <EMAIL>
                Quit: "quit"
                """;
    }

    public Class transferStates() {
        return transferClass;
    }
}
