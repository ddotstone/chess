package websocket.messages;

import websocket.messages.ServerMessage;

import com.google.gson.Gson;
import model.GameData;

public class LoadGameMessage extends ServerMessage {
    protected final GameData gameData;

    public LoadGameMessage(GameData gameData) {
        super(ServerMessageType.LOAD_GAME);
        this.gameData = gameData;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
