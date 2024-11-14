package ui.client;

import chess.ChessBoard;
import exception.ResponseException;
import server.ServerFacade;

import static ui.DisplayFunctions.*;

import java.util.Arrays;

public class InGameChessClient implements ChessClient {

    String authToken;
    ServerFacade serverFacade;
    Class transferClass;

    public InGameChessClient(String url) {
        serverFacade = new ServerFacade(url);
        authToken = null;
    }

    public InGameChessClient(SignedOutChessClient copy) {
        this.authToken = copy.authToken;
        this.serverFacade = copy.serverFacade;
    }

    public InGameChessClient(SignedInChessClient copy) {
        this.authToken = copy.authToken;
        this.serverFacade = copy.serverFacade;
    }

    public String eval(String input) {
        var tokens = input.toLowerCase().split(" ");
        var cmd = (tokens.length > 0) ? tokens[0] : "help";
        var params = Arrays.copyOfRange(tokens, 1, tokens.length);
        return switch (cmd) {
            case "quit" -> "quit";
            default -> printBoard();
        };
    }

    @Override
    public String help() {
        return "";
    }

    public Class transferStates() {
        return null;
    }

    public String printBoard(String... params) {
        ChessBoard board = new ChessBoard();
        board.resetBoard();
        return (boardStringBlack(board) + "\n\n" + boardStringWhite(board));
    }
}
