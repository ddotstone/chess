package service;

import chess.ChessGame;
import dataaccess.*;
import model.*;
import request.*;
import response.*;

public class GameService {
    private static int gameCount = 0;
    private final AuthDataDAO authDataDAO;
    private final GameDataDAO gameDataDAO;

    public GameService(AuthDataDAO authDataDAO, GameDataDAO gameDataDAO) {
        this.authDataDAO = authDataDAO;
        this.gameDataDAO = gameDataDAO;
    }

    public ListGameResponse ListGames(String authToken) throws DataAccessException {
        if (authDataDAO.getAuth(authToken) == null) {
            throw new UnauthorizedException();
        }
        return new ListGameResponse(gameDataDAO.listGames());
    }

    public CreateGameResponse CreateGame(String authToken, CreateGameRequest createGameRequest) throws DataAccessException {
        if (authDataDAO.getAuth(authToken) == null) {
            throw new UnauthorizedException();
        }
        GameData game = new GameData(gameCount++,
                null,
                null,
                createGameRequest.gameName(),
                null);
        gameDataDAO.CreateGame(game);
        return new CreateGameResponse(gameCount);
    }

    public void JoinGame(String authToken, JoinGameRequest joinGameRequest) throws DataAccessException {
        AuthData authData = authDataDAO.getAuth(authToken);
        if (authData == null) {
            throw new UnauthorizedException();
        }

        GameData currGame = gameDataDAO.getGame(joinGameRequest.gameID());
        if (currGame == null) {
            throw new BadRequestException();
        }

        String blackUsername;
        String whiteUsername;

        if (joinGameRequest.playerColor() == ChessGame.TeamColor.BLACK) {
            if (currGame.blackUsername() != null) {
                throw new AlreadyTakenException();
            }
            blackUsername = authData.username();
            whiteUsername = null;
        } else if (joinGameRequest.playerColor() == ChessGame.TeamColor.WHITE) {
            if (currGame.whiteUsername() != null) {
                throw new AlreadyTakenException();
            }
            whiteUsername = authData.username();
            blackUsername = null;
        } else {
            whiteUsername = null;
            blackUsername = null;
        }

        GameData updatedGame = new GameData(currGame,
                whiteUsername,
                blackUsername,
                currGame.gameName(),
                currGame.game());

        gameDataDAO.UpdateGame(updatedGame);
        return;
    }
}
