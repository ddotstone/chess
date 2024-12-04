package websocketServer;

import chess.ChessGame;
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
import server.Server;
import websocket.commands.*;
import websocket.messages.ErrorMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;
import websocket.messages.LoadGameMessage;

import java.io.IOException;
import java.util.Timer;

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
            String userName = "";
            try {
                userName = getUserName(userGameCommand.getAuthToken());
            } catch (DataAccessException dataAccessException) {
                System.out.println("Invalid Auth Token");
            }
            String error = "Error: " + e.getMessage();
            System.out.println(error + "\n");
            ErrorMessage errorMessage = new ErrorMessage(error);
            connections.send(userName, errorMessage);
        }
    }

    private void connect(ConnectCommand connectCommand, Session session) throws DataAccessException, IOException {
        String userName = getUserName(connectCommand.getAuthToken());
        connections.add(userName, session);
        ChessGame.TeamColor color = getTeamColor(userName, connectCommand.getGameID());
        String broadcastMessage = userName + "has joined";
        switch (color) {
            case ChessGame.TeamColor.WHITE -> broadcastMessage = userName + "has joined as white";
            case ChessGame.TeamColor.BLACK -> broadcastMessage = userName + "has joined as black";
            case ChessGame.TeamColor.GREY -> broadcastMessage = userName + "has joined as an observer";
        }
        connections.broadcast(userName, new NotificationMessage(broadcastMessage));
        connections.send(userName, new LoadGameMessage(getGameData(connectCommand.getGameID())));
    }

    private void makeMove(MakeMoveCommand makeMoveCommand, Session session) throws Exception, DataAccessException, IOException {
        GameData gameData = getGameData(makeMoveCommand.getGameID());
        String userName = getUserName(makeMoveCommand.getAuthToken());
        ChessGame.TeamColor teamColor = getTeamColor(userName, makeMoveCommand.getGameID());
        ChessGame.TeamColor turnColor = gameData.game().getTeamTurn();
        if (!checkUserColor(turnColor, userName, gameData.gameID())) {
            if (teamColor == ChessGame.TeamColor.GREY) {
                throw new Exception("You cannot play while observing");
            } else {
                throw new Exception("It is currently " + turnColor.toString() + "'s turn");
            }
        }
        gameData.game().makeMove(makeMoveCommand.getMove());

        GameDataDAO gameDataDAO = new SQLGameDataDAO();
        gameDataDAO.updateGame(gameData);
        connections.broadcast("", new LoadGameMessage(gameData));
        connections.broadcast(userName, new NotificationMessage(""));
    }

    private void resign(ResignCommand resignCommand, Session session) throws Exception, ResponseException, DataAccessException {
        String authToken = resignCommand.getAuthToken();
        String userName = getUserName(authToken);
        GameData gameData = getGameData(resignCommand.getGameID());
        ChessGame.TeamColor teamColor = getTeamColor(userName, resignCommand.getGameID());
        if (teamColor == ChessGame.TeamColor.GREY) {
            throw new Exception("Observers cannot resign");
        }
        gameData.game().setTeamTurn(ChessGame.TeamColor.NONE);

        SQLGameDataDAO gameDataDAO = new SQLGameDataDAO();
        gameDataDAO.updateGame(gameData);
        connections.broadcast(userName, new NotificationMessage(userName + "has resigned"));
    }

    private void leave(LeaveCommand leaveCommand, Session session) throws DataAccessException, IOException {
        String userName = getUserName(leaveCommand.getAuthToken());
        ChessGame.TeamColor coor = getTeamColor(userName, leaveCommand.getGameID());
        connections.remove(userName);
        connections.broadcast(userName, new NotificationMessage(userName + " has left the game"));
    }

    private void loadGame(LoadGameCommand loadGameCommand, Session session) throws DataAccessException, IOException {
        String authToken = loadGameCommand.getAuthToken();
        String userName = getUserName(authToken);
        GameData gameData = getGameData(loadGameCommand.getGameID());
        connections.send(userName, new LoadGameMessage(gameData));
    }

    public String getUserName(String authToken) throws DataAccessException {
        SQLAuthDataDAO authDataDAO = new SQLAuthDataDAO();
        AuthData authData = authDataDAO.getAuth(authToken);
        return authData.username();
    }

    public GameData getGameData(int gameID) throws DataAccessException {
        SQLGameDataDAO gameDataDAO = new SQLGameDataDAO();
        return gameDataDAO.getGame(gameID);
    }

    public ChessGame.TeamColor getTeamColor(String userName, int gameID) throws DataAccessException {
        GameData gameData = getGameData(gameID);
        if (gameData.whiteUsername().equals(userName)) {
            return ChessGame.TeamColor.WHITE;
        }

        if (gameData.blackUsername().equals(userName)) {
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
}