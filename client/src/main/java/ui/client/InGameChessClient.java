package ui.client;

import chess.*;
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
        this.teamColor = copy.teamColor;

    }

    public InGameChessClient(SignedInChessClient copy) {
        this.authToken = copy.authToken;
        this.serverFacade = copy.serverFacade;
        this.url = copy.url;
        this.notificationHandler = copy.notificationHandler;
        this.gameID = copy.gameID;
        this.teamColor = copy.teamColor;
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
        webSocketFacade.draw(authToken, gameID);
        return "";
    }

    private String move(String... params) throws ResponseException {
        if (params.length == 2 || params.length == 3) {
            String start = params[0];
            String end = params[1];
            ChessPosition startPosition = convertStringToPosition(start);
            ChessPosition endPosition = convertStringToPosition(end);
            ChessPiece.PieceType promotionPiece = null;
            if (params.length == 3) {
                switch (params[2]) {
                    case "pawn" -> promotionPiece = ChessPiece.PieceType.PAWN;
                    case "rook" -> promotionPiece = ChessPiece.PieceType.ROOK;
                    case "king" -> promotionPiece = ChessPiece.PieceType.KING;
                    case "queen" -> promotionPiece = ChessPiece.PieceType.QUEEN;
                    case "knight" -> promotionPiece = ChessPiece.PieceType.KNIGHT;
                    case "bishop" -> promotionPiece = ChessPiece.PieceType.BISHOP;
                    default -> throw new ResponseException(400, "Expected: move <start> <end> optional <promotion>");
                }
            }
            ChessMove chessMove = new ChessMove(startPosition, endPosition, promotionPiece);

        } else {
            throw new ResponseException(400, "Expected: move <start> <end> optional <promotion>");
        }
        return "";
    }

    public String resign(String... params) throws ResponseException {
        WebSocketFacade webSocketFacade = new WebSocketFacade(url, notificationHandler);
        webSocketFacade.resign(authToken, gameID);
        return "";
    }

    public String leave(String... params) throws ResponseException {
        WebSocketFacade webSocketFacade = new WebSocketFacade(url, notificationHandler);
        webSocketFacade.leave(authToken, gameID);
        return "";

    }

    public void printBoard(ChessBoard board) {
    }

    @Override
    public String help() {
        return """
                Options:
                Redraw Board: "draw"
                Make Move: "move" <start> <end> optional <promotion>
                Resign: "resign"
                Leave Game: "leave"
                Quit: "quit"
                """;
    }

    public Class transferStates() {
        return transferClass;
    }

    public ChessPosition convertStringToPosition(String position) throws ResponseException {
        if (position.length() != 2) {
            throw new ResponseException(400, "Expected: move <start> <end> optional <promotion>");
        }
        int row = position.charAt(0) - 'a' + 1;
        int col = Character.getNumericValue(position.charAt(1));
        if (row > 8 || col > 8) {
            throw new ResponseException(400, "Expected: move <start> <end> optional <promotion>");
        }
        return new ChessPosition(row, col);
    }

}
