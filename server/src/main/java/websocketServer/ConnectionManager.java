package websocketServer;

import org.eclipse.jetty.websocket.api.Session;
import server.Server;
import websocket.messages.NotificationMessage;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.ServerMessage;


import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<String, ConcurrentHashMap<String, Connection>> games = new ConcurrentHashMap<>();

    public void add(String userName, int gameID, Session session) {
        if (games.get(String.valueOf(gameID)) == null) {
            games.put(String.valueOf(gameID), new ConcurrentHashMap<String, Connection>());
        }
        ConcurrentHashMap<String, Connection> connections = games.get(String.valueOf(gameID));
        var connection = new Connection(userName, session);
        connections.put(userName, connection);
    }

    public void remove(String userName, int gameID) {

        if (games.get(String.valueOf(gameID)) != null) {
            ConcurrentHashMap<String, Connection> connections = games.get(String.valueOf(gameID));
            connections.remove(userName);
        }
    }

    public void broadcast(String excludeUserName, int gameID, ServerMessage message) throws IOException {
        if (games.get(String.valueOf(gameID)) == null) {
            return;
        }
        ConcurrentHashMap<String, Connection> connections = games.get(String.valueOf(gameID));

        var removeList = new ArrayList<Connection>();
        for (var c : connections.values()) {
            if (c.session.isOpen()) {
                if (!c.userName.equals(excludeUserName)) {
                    c.send(message.toString());
                }
            } else {
                removeList.add(c);
            }
        }
    }

    public void send(String userName, int gameID, ServerMessage message) throws IOException {
        if (games.get(String.valueOf(gameID)) == null) {
            return;
        }
        ConcurrentHashMap<String, Connection> connections = games.get(String.valueOf(gameID));
        var conn = connections.get(userName);
        if (conn.session.isOpen()) {
            conn.send(message.toString());
        }
    }

    public void sendClean(String userName, int gameID, ServerMessage message, Session session) throws IOException {
        if (games.get(String.valueOf(gameID)) != null) {
            ConcurrentHashMap<String, Connection> connections = games.get(String.valueOf(gameID));
            var conn = connections.get(userName);
            if (conn == null) {
                var connection = new Connection(userName, session);
                if (session.isOpen())
                    connection.send(message.toString());
            } else if (conn.session.isOpen()) {
                conn.send(message.toString());
            }
        } else {
            var connection = new Connection(userName, session);
            if (session.isOpen())
                connection.send(message.toString());
        }
    }
}