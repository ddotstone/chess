package ui.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import chess.ChessBoard;
import chess.ChessGame;
import exception.ResponseException;
import connection.ServerFacade;
import model.GameData;

import static ui.DisplayFunctions.*;
import static ui.EscapeSequences.SET_BG_COLOR_LIGHT_GREY;

public class SignedInChessClient implements ChessClient {
    String authToken;
    ServerFacade serverFacade;
    Class transferClass;
    ArrayList<GameData> lastList;

    public SignedInChessClient(String url) {
        serverFacade = new ServerFacade(url);
        authToken = null;
    }

    public SignedInChessClient(SignedOutChessClient copy) {
        this.authToken = copy.authToken;
        this.serverFacade = copy.serverFacade;
    }

    public SignedInChessClient(InGameChessClient copy) {
        this.authToken = copy.authToken;
        this.serverFacade = copy.serverFacade;
    }


    public String eval(String input) throws ResponseException {
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
    }

    private String signOut(String... params) throws ResponseException {
        serverFacade.signOut(authToken);
        transferClass = SignedOutChessClient.class;
        return "signed out";
    }

    private String clear(String... params) throws ResponseException {
        serverFacade.clear();
        return "cleared database";
    }

    private String createGame(String... params) throws ResponseException {
        if (params.length == 1) {
            String gameName = params[0];
            int gameID = serverFacade.createGame(authToken, gameName);
            return String.format("created game %s", gameName);
        }
        throw new ResponseException(400, "Expected: create <GAME ID>");
    }

    private String joinGame(String... params) throws ResponseException {
        if (params.length == 2) {
            String gameID = params[0];
            String teamColor = params[1];
            int gameIDInt;
            ChessGame.TeamColor teamColorEnum;
            try {
                gameIDInt = Integer.parseInt(gameID) - 1;
            } catch (Exception ex) {
                throw new ResponseException(400, "Invalid ID");
            }

            try {
                teamColorEnum = ChessGame.TeamColor.valueOf(teamColor.toUpperCase());
            } catch (Exception ex) {
                throw new ResponseException(400, "Invalid Team Color");
            }
            if (gameIDInt < 0 || gameIDInt >= lastList.size()) {
                throw new ResponseException(400, "Invalid Game ID");
            }
            gameIDInt = lastList.get(gameIDInt).gameID();
            serverFacade.joinGame(authToken, gameIDInt, teamColorEnum);
            transferClass = InGameChessClient.class;
            ChessBoard board = new ChessBoard();
            board.resetBoard();
            System.out.println(boardStringBlack(board) + "\n" + SET_BG_COLOR_LIGHT_GREY + "\n" + boardStringWhite(board));
            return String.format("joined game %s as %s", gameID, teamColor);
        }
        throw new ResponseException(400, "Expected: join <GAME ID> <TEAM COLOR>");
    }

    private String listGames(String... params) throws ResponseException {
        Collection<GameData> games = serverFacade.listGames(authToken);
        ArrayList<GameData> orderedGames = new ArrayList<>();
        StringBuilder result = new StringBuilder("Games:");
        int i = 1;
        for (GameData game : games) {
            result.append("\n");
            result.append("\n");
            orderedGames.add(game);
            result.append("\tGame: ");
            result.append(game.gameName());
            result.append("\n");
            result.append("\tID: ");
            result.append(String.format("%d", i));
            result.append("\n");
            result.append("\tWhite: ");
            result.append(game.whiteUsername());
            result.append("\n");
            result.append("\tBlack: ");
            result.append(game.blackUsername());
            i++;
        }
        lastList = orderedGames;
        return result.toString();
    }

    private String watchGame(String... params) throws ResponseException {
        if (params.length == 1) {
            String gameID = params[0];
            int gameIDInt;
            try {
                gameIDInt = Integer.parseInt(gameID) - 1;
            } catch (Exception ex) {
                throw new ResponseException(400, "Invalid Game ID");
            }
            if (gameIDInt < 0 || gameIDInt >= lastList.size()) {
                throw new ResponseException(400, "Invalid Game ID");
            }
            transferClass = InGameChessClient.class;
            ChessBoard board = new ChessBoard();
            board.resetBoard();
            System.out.println(boardStringBlack(board) + "\n" + SET_BG_COLOR_LIGHT_GREY + "\n" + boardStringWhite(board));
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
