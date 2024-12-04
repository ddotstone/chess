package websocketServer;

import chess.ChessGame;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import dataaccess.DataAccessException;
import dataaccess.SQLAuthDataDAO;
import dataaccess.SQLGameDataDAO;
import exception.ResponseException;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.*;
import websocket.commands.UserGameCommand;
import websocket.commands.ResignCommand;
import websocket.commands.ConnectCommand;
import websocket.commands.LeaveCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;
import websocket.messages.LoadGameMessage;

import java.io.IOException;
import java.util.Timer;

public class WebSocketHandler {
    private final ConnectionManager connections = new ConnectionManager();

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        JsonObject object = JsonParser.parseString(message).getAsJsonObject();
        JsonElement element = object.get("serverMessageType");
        UserGameCommand.CommandType type =
                UserGameCommand.CommandType.valueOf(element.getAsString());
        try {
            switch (type) {
                case CONNECT -> connect(new Gson().fromJson(message,
                        ConnectCommand.class), session);
            }
        } catch (Exception e) {
            String error = "Error: " + e.getMessage();
            System.out.println(error + "\n");
            ErrorMessage errorMessage = new ErrorMessage(error);
        }
    }

    private void connect(ConnectCommand connectCommand, Session session) throws DataAccessException, IOException {
        String userName = getUserName(connectCommand.getAuthToken();
        connections.add(userName, session);
        ChessGame.TeamColor color = getTeamColor(userName, connectCommand.getGameID());
        String broadcastMessage = userName + "has joined";
        switch(color) {
            case ChessGame.TeamColor.WHITE -> broadcastMessage = userName + "has joined as white";
            case ChessGame.TeamColor.BLACK -> broadcastMessage = userName + "has joined as black";
            case ChessGame.TeamColor.NONE -> broadcastMessage = userName + "has joined as an observer";
        }
        connections.broadcast(userName, new NotificationMessage(broadcastMessage));
        connections.send(userName, new LoadGameMessage(getGameData(connectCommand.getGameID())));
    }

    private void exit(String visitorName) throws IOException {
        connections.remove(visitorName);
        var message = String.format("%s left the shop", visitorName);
        var notification = new Notification(Notification.Type.DEPARTURE, message);
        connections.broadcast(visitorName, notification);
    }

    public void makeNoise(String petName, String sound) throws ResponseException {
        try {
            var message = String.format("%s says %s", petName, sound);
            var notification = new Notification(Notification.Type.NOISE, message);
            connections.broadcast("", notification);
        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
        }
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

    public ChessGame.TeamColor getTeamColor(String userName, int gameID) throws DataAccessException
    {
        GameData gameData = getGameData(gameID);
        if (gameData.whiteUsername().equals(userName))
        {
            return ChessGame.TeamColor.WHITE;
        }

        if (gameData.blackUsername().equals(userName))
        {
            return ChessGame.TeamColor.BLACK;
        }
        return ChessGame.TeamColor.NONE;
    }
}