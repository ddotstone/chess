package websocket.messages;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;

public class LoadGameMessage extends ServerMessage {
    protected final ChessGame game;

    public LoadGameMessage(ChessGame game) {
        super(ServerMessageType.LOAD_GAME);
        this.game = game;
    }

    public ChessGame getGameData() {
        return game;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
