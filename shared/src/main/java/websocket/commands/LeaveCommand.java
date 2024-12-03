package websocket.commands;

import com.google.gson.Gson;

public class LeaveCommand extends UserGameCommand {


    public LeaveCommand(String authToken, Integer gameID) {
        super(CommandType.LEAVE, authToken, gameID);
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
