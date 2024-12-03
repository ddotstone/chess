package websocket.commands;

import com.google.gson.Gson;

public class ResignCommand extends UserGameCommand {


    public ResignCommand(String authToken, Integer gameID) {
        super(CommandType.RESIGN, authToken, gameID);
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
