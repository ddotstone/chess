package websocket.messages;

import websocket.messages.ServerMessage;

import com.google.gson.Gson;

public class NotificationMessage extends ServerMessage {
    protected final String message;

    public NotificationMessage(String message) {
        super(ServerMessageType.NOTIFICATION);
        this.message = message;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
