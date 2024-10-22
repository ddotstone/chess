package request;

import chess.ChessGame;

public record JoinGameRequest(String authToken, int gameID, ChessGame.TeamColor color) {
}
