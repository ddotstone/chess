package ui.client;

import java.util.Arrays;
import java.util.Collection;

import chess.ChessGame;
import exception.ResponseException;
import server.ServerFacade;
import model.GameData;

public class SignedInChessClient {
    String authToken;
    ServerFacade serverFacade;
    Class transferClass;

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
                case "logout" -> signOut(params);
                case "clear" -> clear(params);
                case "list" -> listGames(params);
                case "create" -> createGame(params);
                case "join" -> joinGame(params);
                case "watch" -> watchGame(params);
                case "quit" -> "quit";
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    private String signOut(String... params) throws ResponseException {
        serverFacade.signOut(authToken);
        return "signed out";
    }

    private String clear(String... params) throws ResponseException {
        serverFacade.clear();
        return "cleared database";
    }

    private String createGame(String... params) throws ResponseException {
        if (params.length >= 1) {
            String gameName = params[0];
            int gameID = serverFacade.createGame(authToken, gameName);
            return String.format("created game %s", gameName);
        }
        throw new ResponseException(400, "Expected: create <GAME ID>");
    }

    private String joinGame(String... params) throws ResponseException {
        if (params.length >= 2) {
            String gameID = params[0];
            String teamColor = params[1];
            int gameIDInt;
            ChessGame.TeamColor teamColorEnum;
            try {
                gameIDInt = Integer.parseInt(gameID);
                teamColorEnum = ChessGame.TeamColor.valueOf(teamColor);
            } catch (Exception ex) {
                throw new ResponseException(400, "Expected: join <GAME ID> <TEAM COLOR>");
            }
            serverFacade.joinGame(authToken, gameIDInt, teamColorEnum);
            return String.format("joined game %s as %s", teamColor);
        }
        throw new ResponseException(400, "Expected: join <GAME ID> <TEAM COLOR>");
    }

    private String listGames(String... params) throws ResponseException {
        Collection<GameData> games = serverFacade.listGames(authToken);
        StringBuilder result = new StringBuilder("Games:\n");
        for (GameData game : games) {
            result.append(game.toString());
            result.append("\n");
        }
        return result.toString();
    }

    private String watchGame(String... params) throws ResponseException {
        if (params.length >= 1) {
            String gameID = params[0];
            return String.format("watching game %s", gameID);
        }
        throw new ResponseException(400, "Expected: watch <GAME ID>");
    }

    public Class transferStates() {
        return transferClass;
    }

    public String help() {
        return """
                Options:
                List current games: "list"
                Create a new game: "create" <GAME NAME>
                Join a game: "join" <GAME ID> <COLOR>
                Watch a game: "watch" <GAME ID>
                Logout: "logout"
                Quit: "quit"
                """;
    }
}
