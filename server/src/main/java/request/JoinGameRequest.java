package request;

import chess.ChessGame;

public record JoinGameRequest(int gameID, ChessGame.TeamColor playerColor) {
}
