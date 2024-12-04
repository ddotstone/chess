package websocket.messages;

import com.google.gson.Gson;

import javax.swing.*;

public class ErrorMessage extends ServerMessage {
    protected final String errorMessage;

    public ErrorMessage(String errorMessage) {
        super(ServerMessageType.ERROR);
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
