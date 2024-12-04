package ui.client;

import chess.ChessBoard;
import chess.ChessGame;
import connection.ServerFacade;
import exception.ResponseException;
import websocketClient.NotificationHandler;
import websocketClient.WebSocketFacade;

import static ui.DisplayFunctions.*;
import static ui.EscapeSequences.*;

import java.util.Arrays;

public class InGameChessClient implements ChessClient {

    NotificationHandler notificationHandler;
    String url;
    String authToken;
    ChessGame.TeamColor teamColor;
    ServerFacade serverFacade;
    Class transferClass;
    int gameID;

    public InGameChessClient(String url, NotificationHandler notificationHandler) {
        this.url = url;
        this.notificationHandler = notificationHandler;
        serverFacade = new ServerFacade(url);
        authToken = null;
        this.teamColor = ChessGame.TeamColor.GREY;
        gameID = 0;
    }

    public InGameChessClient(SignedOutChessClient copy) {
        this.authToken = copy.authToken;
        this.serverFacade = copy.serverFacade;
        this.url = copy.url;
        this.notificationHandler = copy.notificationHandler;
        this.gameID = 0;

    }

    public InGameChessClient(SignedInChessClient copy) {
        this.authToken = copy.authToken;
        this.serverFacade = copy.serverFacade;
        this.url = copy.url;
        this.notificationHandler = copy.notificationHandler;
        this.gameID = copy.gameID;
    }

    public String getState() {
        return "<in game>";
    }

    public String eval(String input) throws ResponseException {
        var tokens = input.toLowerCase().split(" ");
        var cmd = (tokens.length > 0) ? tokens[0] : "help";
        var params = Arrays.copyOfRange(tokens, 1, tokens.length);
        return switch (cmd) {
            case "clear" -> clear(params);
            case "draw" -> draw(params);
            case "move" -> move(params);
            case "resign" -> resign(params);
            case "leave" -> leave(params);
            case "help" -> help();
            case "quit" -> "quit";
            default -> throw new ResponseException(400, "Invalid Command");
        };
    }

    private String clear(String... params) throws ResponseException {
        serverFacade.clear();
        return "cleared database";
    }

    private String draw(String... params) throws ResponseException {
        WebSocketFacade webSocketFacade = new WebSocketFacade(url, notificationHandler);
        webSocketFacade.draw(authToken, );
        return "";
    }

    private String exitGame(String... params) throws ResponseException {
        transferClass = SignedInChessClient.class;
        return "exited game";
    }

    @Override
    public String help() {
        return """
                Options:
                Redraw Board: "draw"
                Make Move: "move" <START> <END>
                Resign: "resign"
                Leave Game: "leave"
                Quit: "quit"
                """;
    }

    public Class transferStates() {
        return transferClass;
    }

}
