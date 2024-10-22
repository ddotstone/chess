package model;

import chess.ChessGame;

public record GameData(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game) {
    public GameData(GameData copy, String whiteUsername, String blackUsername, String gameName, ChessGame game) {
        this(copy.gameID(),
                (whiteUsername == null) ? copy.whiteUsername() : whiteUsername,
                (blackUsername == null) ? copy.blackUsername() : blackUsername,
                (gameName == null) ? copy.gameName() : gameName,
                (game == null) ? new ChessGame(copy.game()) : new ChessGame(game));
    }

    @Override
    public String toString() {
        return "{" +
                "gameID=" + gameID +
                ", whiteUsername='" + whiteUsername + '\'' +
                ", blackUsername='" + blackUsername + '\'' +
                ", gameName='" + gameName + '\'' +
                '}';
    }
};