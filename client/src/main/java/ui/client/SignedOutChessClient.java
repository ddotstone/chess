package ui.client;

import java.util.Arrays;

import com.google.gson.Gson;
import exception.ResponseException;
import server.ServerFacade;

public class SignedOutChessClient {
    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "signin" -> signIn(params);
                case "rescue" -> register(params);
                case "quit" -> "quit";
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    private String signIn(String... params) throws ResponseException {

    }

    private String signIn(String... params) throws ResponseException {

    }

    public String help() {
        return """
                - signIn <username> <password>
                - register <username> <password> <email>
                - quit
                """;
    }
}
