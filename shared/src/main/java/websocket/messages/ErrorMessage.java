package websocket.messages;

import websocket.messages.ServerMessage;
import com.google.gson.Gson;

public class ErrorMessage extends ServerMessage {
    protected final String errorMessage;

    public ErrorMessage(String errorMessage) {
        super(ServerMessageType.ERROR);
        this.errorMessage = errorMessage;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
