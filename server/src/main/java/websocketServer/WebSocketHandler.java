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
import websocket.*;
import websocket.commands.*;
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
                case MAKE_MOVE -> makeMove(new Gson().fromJson(message,
                        ConnectCommand.class), session);
            }
        } catch (Exception e) {
            UserGameCommand userGameCommand = new Gson().fromJson(message, UserGameCommand.class);
            try {
                String userName = getUserName(userGameCommand.getAuthToken());
            }
            catch(DataAccessException dataAccessException)
            {
                System.out.println("Invalid Auth Token");
            }
            String error = "Error: " + e.getMessage();
            System.out.println(error + "\n");
            ErrorMessage errorMessage = new ErrorMessage(error);
            connections.send()
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
        gameData.game().makeMove(makeMoveCommand.getMove());

        GameDataDAO gameDataDAO = new SQLGameDataDAO();
        gameDataDAO.updateGame(gameData);
        connections.broadcast("", new LoadGameMessage(gameData));
        connections.broadcast(userName, new NotificationMessage(""));
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
}