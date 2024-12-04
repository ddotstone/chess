package websocket.commands;

import com.google.gson.Gson;

public class LoadGameCommand extends UserGameCommand {


    public LoadGameCommand(String authToken, Integer gameID) {
        super(CommandType.LOAD_GAME, authToken, gameID);
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
