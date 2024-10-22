package request;

import chess.ChessGame;

public record JoinGameRequest(int gameID, ChessGame.TeamColor playerColor) {
    @Override
    public String toString() {
        return "JoinGameRequest{" +
                "gameID=" + gameID +
                ", playerColor=" + playerColor +
                '}';
    }
}
