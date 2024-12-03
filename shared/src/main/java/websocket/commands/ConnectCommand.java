package websocket.commands;

import chess.ChessMove;
import com.google.gson.Gson;

public class ConnectCommand extends UserGameCommand {


    public ConnectCommand(String authToken, Integer gameID) {
        super(CommandType.CONNECT, authToken, gameID);
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
