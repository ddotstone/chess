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
        String printWhiteUsername = (this.whiteUsername == null) ? "" : this.whiteUsername;
        String printBlackUsername = (this.blackUsername == null) ? "" : this.blackUsername;
        return "{" +
                "gameID=" + gameID +
                ", whiteUsername='" + printWhiteUsername + '\'' +
                ", blackUsername='" + printBlackUsername + '\'' +
                ", gameName='" + gameName + '\'' +
                '}';
    }
};