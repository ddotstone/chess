package websocketserver;

import chess.ChessGame;
import chess.ChessPosition;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import dataaccess.DataAccessException;
import dataaccess.GameDataDAO;
import dataaccess.SQLAuthDataDAO;
import dataaccess.SQLGameDataDAO;
import exception.ResponseException;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.*;
import websocket.messages.ErrorMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.LoadGameMessage;

import java.io.IOException;

@WebSocket
public class WebSocketHandler {
    private final ConnectionManager connections = new ConnectionManager();

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        JsonObject object = JsonParser.parseString(message).getAsJsonObject();
        JsonElement element = object.get("commandType");
        UserGameCommand.CommandType type =
                UserGameCommand.CommandType.valueOf(element.getAsString());
        try {
            switch (type) {
                case CONNECT -> connect(new Gson().fromJson(message,
                        ConnectCommand.class), session);
                case MAKE_MOVE -> makeMove(new Gson().fromJson(message,
                        MakeMoveCommand.class), session);
                case RESIGN -> resign(new Gson().fromJson(message, ResignCommand.class), session);
                case LEAVE -> leave(new Gson().fromJson(message, LeaveCommand.class), session);
                case LOAD_GAME -> loadGame(new Gson().fromJson(message, LoadGameCommand.class), session);
            }
        } catch (Exception e) {
            UserGameCommand userGameCommand = new Gson().fromJson(message, UserGameCommand.class);
            String userName = userGameCommand.getAuthToken();
            int gameID = userGameCommand.getGameID();
            try {
                userName = getUserName(userGameCommand.getAuthToken());
            } catch (DataAccessException dataAccessException) {
                System.out.println("Invalid Auth Token");
            }
            String error = "Error: " + e.getMessage();
            System.out.println(error + "\n");
            ErrorMessage errorMessage = new ErrorMessage(error);
            connections.sendClean(userName, gameID, errorMessage, session);
        }
    }

    private void connect(ConnectCommand connectCommand, Session session) throws DataAccessException, IOException {
        String userName = getUserName(connectCommand.getAuthToken());
        ChessGame.TeamColor color = getTeamColor(userName, connectCommand.getGameID());
        connections.add(userName, connectCommand.getGameID(), session);
        String broadcastMessage = userName + "has joined";
        switch (color) {
            case ChessGame.TeamColor.WHITE -> broadcastMessage = userName + " has joined as white";
            case ChessGame.TeamColor.BLACK -> broadcastMessage = userName + " has joined as black";
            case ChessGame.TeamColor.GREY -> broadcastMessage = userName + " has joined as an observer";
        }
        LoadGameMessage loadGameMessage = new LoadGameMessage(getGameData(connectCommand.getGameID()).game());
        connections.broadcast(userName, connectCommand.getGameID(), new NotificationMessage(broadcastMessage));
        connections.send(userName, connectCommand.getGameID(), loadGameMessage);
    }

    private void makeMove(MakeMoveCommand makeMoveCommand, Session session) throws Exception, DataAccessException, IOException {
        GameData gameData = getGameData(makeMoveCommand.getGameID());
        String userName = getUserName(makeMoveCommand.getAuthToken());
        ChessGame.TeamColor teamColor = getTeamColor(userName, makeMoveCommand.getGameID());
        ChessGame.TeamColor turnColor = gameData.game().getTeamTurn();
        if (!checkUserColor(turnColor, userName, gameData.gameID())) {
            if (teamColor == ChessGame.TeamColor.GREY) {
                throw new Exception("You cannot play while observing");
            } else if (teamColor == ChessGame.TeamColor.NONE) {
                throw new Exception("The Game is over");
            } else {
                throw new Exception("It is currently " + turnColor.toString() + "'s turn");
            }
        }

        gameData.game().makeMove(makeMoveCommand.getMove());
        connections.broadcast(userName, makeMoveCommand.getGameID(), new NotificationMessage(userName + " has moved " +
                getPieceAtPosition(gameData.game(), makeMoveCommand.getMove().getEndPosition()) + " from " +
                convertPositionToString(makeMoveCommand.getMove().getStartPosition()) + " to " +
                convertPositionToString(makeMoveCommand.getMove().getEndPosition())));
        if (gameData.game().isInCheckmate(ChessGame.TeamColor.WHITE)) {
            connections.broadcast("", makeMoveCommand.getGameID(), new NotificationMessage("WHITE is in checkmate, gameover"));
            gameData.game().setTeamTurn(ChessGame.TeamColor.NONE);

        } else if (gameData.game().isInCheck(ChessGame.TeamColor.WHITE)) {
            connections.broadcast("", makeMoveCommand.getGameID(), new NotificationMessage("WHITE is in check"));
        } else if (gameData.game().isInCheckmate(ChessGame.TeamColor.BLACK)) {
            connections.broadcast("", makeMoveCommand.getGameID(), new NotificationMessage("BLACK is in checkmate, gameover"));
            gameData.game().setTeamTurn(ChessGame.TeamColor.NONE);

        } else if (gameData.game().isInCheck(ChessGame.TeamColor.BLACK)) {
            connections.broadcast("", makeMoveCommand.getGameID(), new NotificationMessage("BLACK is in check"));
        }
        GameDataDAO gameDataDAO = new SQLGameDataDAO();
        gameDataDAO.updateGame(gameData);
        connections.broadcast("", makeMoveCommand.getGameID(), new LoadGameMessage(gameData.game()));
    }

    private void resign(ResignCommand resignCommand, Session session) throws Exception, ResponseException, DataAccessException {
        String authToken = resignCommand.getAuthToken();
        String userName = getUserName(authToken);
        GameData gameData = getGameData(resignCommand.getGameID());
        ChessGame.TeamColor teamColor = getTeamColor(userName, resignCommand.getGameID());
        if (teamColor == ChessGame.TeamColor.GREY) {
            throw new Exception("Observers cannot resign");
        } else if (gameData.game().getTeamTurn() == ChessGame.TeamColor.NONE) {
            throw new Exception("Game Already over");
        }
        gameData.game().setTeamTurn(ChessGame.TeamColor.NONE);

        SQLGameDataDAO gameDataDAO = new SQLGameDataDAO();
        gameDataDAO.updateGame(gameData);
        connections.broadcast("", resignCommand.getGameID(), new NotificationMessage(userName + " has resigned"));
    }

    private void leave(LeaveCommand leaveCommand, Session session) throws DataAccessException, IOException {
        String userName = getUserName(leaveCommand.getAuthToken());
        ChessGame.TeamColor color = getTeamColor(userName, leaveCommand.getGameID());
        GameData gameData = getGameData(leaveCommand.getGameID());
        GameData copy;
        if (color == ChessGame.TeamColor.WHITE) {
            copy = new GameData(null, gameData.blackUsername(), gameData);
        } else if (color == ChessGame.TeamColor.BLACK) {
            copy = new GameData(gameData.blackUsername(), null, gameData);
        } else {
            copy = gameData;
        }
        SQLGameDataDAO gameDataDAO = new SQLGameDataDAO();
        gameDataDAO.updateGame(copy);
        connections.remove(userName, leaveCommand.getGameID());
        connections.broadcast(userName, leaveCommand.getGameID(), new NotificationMessage(userName + " has left the game"));
    }

    private void loadGame(LoadGameCommand loadGameCommand, Session session) throws DataAccessException, IOException {
        String authToken = loadGameCommand.getAuthToken();
        String userName = getUserName(authToken);
        GameData gameData = getGameData(loadGameCommand.getGameID());
        connections.send(userName, loadGameCommand.getGameID(), new LoadGameMessage(gameData.game()));
    }

    public String getUserName(String authToken) throws DataAccessException {
        SQLAuthDataDAO authDataDAO = new SQLAuthDataDAO();
        AuthData authData = authDataDAO.getAuth(authToken);
        if (authData == null) {
            throw new DataAccessException("Unauthorized");
        }
        return authData.username();
    }

    public GameData getGameData(int gameID) throws DataAccessException {
        SQLGameDataDAO gameDataDAO = new SQLGameDataDAO();
        GameData gameData = gameDataDAO.getGame(gameID);
        if (gameData == null) {
            throw new DataAccessException("Invalid Game ID");
        }

        return gameData;
    }

    public ChessGame.TeamColor getTeamColor(String userName, int gameID) throws DataAccessException {
        GameData gameData = getGameData(gameID);
        if (gameData.whiteUsername() != null && gameData.whiteUsername().equals(userName)) {
            return ChessGame.TeamColor.WHITE;
        }

        if (gameData.blackUsername() != null && gameData.blackUsername().equals(userName)) {
            return ChessGame.TeamColor.BLACK;
        }
        return ChessGame.TeamColor.GREY;
    }

    public boolean checkUserColor(ChessGame.TeamColor turnColor, String userName, int gameID) throws DataAccessException {
        GameData gameData = getGameData(gameID);
        boolean ret;
        switch (turnColor) {
            case WHITE -> ret = gameData.whiteUsername() != null && gameData.whiteUsername().equals(userName);
            case BLACK -> ret = gameData.blackUsername() != null && gameData.blackUsername().equals(userName);
            default -> ret = false;
        }
        return ret;
    }

    private String convertPositionToString(ChessPosition position) {
        if (position.getRow() < 1 || position.getRow() > 8 || position.getColumn() < 1 || position.getColumn() > 8) {
            throw new IllegalArgumentException("Invalid move");
        }
        char column = (char) ('a' + position.getColumn() - 1);
        return column + String.valueOf(position.getRow());
    }

    private String getPieceAtPosition(ChessGame game, ChessPosition position) {
        return game.getBoard().getPiece(position).getPieceType().toString();
    }
}