package websocket.messages;

import com.google.gson.Gson;

public class NotificationMessage extends ServerMessage {
    protected final String message;

    public NotificationMessage(String message) {
        super(ServerMessageType.NOTIFICATION);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
