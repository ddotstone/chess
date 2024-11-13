package ui.client;

import java.util.Arrays;

import exception.ResponseException;
import server.ServerFacade;
import model.AuthData;

public class SignedInChessClient {
    String authToken;
    ServerFacade serverFacade;

    public SignedInChessClient(String url) {
        serverFacade = new ServerFacade(url);
        authToken = null;
    }

    public SignedInChessClient(SignedOutChessClient copy) {
        this.authToken = copy.authToken;
        this.serverFacade = copy.serverFacade;
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "signOut" -> signIn(params);
                case "clear" -> register(params);
                case "createGame" -> register(params);
                case "joinGame" -> register(params);
                case "quit" -> "quit";
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    private String signIn(String... params) throws ResponseException {
        if (params.length >= 2) {
            String username = params[0];
            String password = params[1];
            AuthData authData = serverFacade.signIn(username, password);
            this.authToken = authData.authToken();
            return String.format("signed in as: %s", username);
        }
        throw new ResponseException(400, "Expected: signIn <username> <password>");
    }

    private String register(String... params) throws ResponseException {
        if (params.length >= 3) {
            String username = params[0];
            String password = params[1];
            String email = params[2];
            AuthData authData = serverFacade.register(username, password, email);
            this.authToken = authData.authToken();
            return String.format("registered as: %s", username);
        }
        throw new ResponseException(400, "Expected: register <username> <password> <email>");
    }

    public String help() {
        return """
                - signIn <username> <password>
                - register <username> <password> <email>
                - quit
                """;
    }
}
