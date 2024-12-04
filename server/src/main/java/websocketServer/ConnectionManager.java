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
    public final ConcurrentHashMap<String, Connection> connections = new ConcurrentHashMap<>();

    public void add(String userName, Session session) {
        var connection = new Connection(userName, session);
        connections.put(userName, connection);
    }

    public void remove(String userName) {
        connections.remove(userName);
    }

    public void broadcast(String excludeUserName, ServerMessage message) throws IOException {
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

        // Clean up any connections that were left open.
        for (var c : removeList) {
            connections.remove(c.userName);
        }
    }

    public void send(String userName, ServerMessage message) throws IOException {
        var conn = connections.get(userName);
        if (conn.session.isOpen()) {
            conn.send(message.toString());
        }
    }
}