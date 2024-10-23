package request;

import chess.ChessGame;

public record JoinGameRequest(int gameID, ChessGame.TeamColor playerColor) {
    @Override
    public String toString() {
        return "joinGameRequest{" +
                "gameID=" + gameID +
                ", playerColor=" + playerColor +
                '}';
    }
}
