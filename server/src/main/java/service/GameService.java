package service;

import chess.ChessGame;
import dataaccess.*;
import model.*;
import request.*;
import response.*;

public class GameService {
    private final AuthDataDAO authDataDAO;
    private final GameDataDAO gameDataDAO;

    public GameService(AuthDataDAO authDataDAO, GameDataDAO gameDataDAO) {
        this.authDataDAO = authDataDAO;
        this.gameDataDAO = gameDataDAO;
    }

    public ListGameResponse listGames(String authToken) throws DataAccessException {
        if (authDataDAO.getAuth(authToken) == null) {
            throw new UnauthorizedException();
        }
        return new ListGameResponse(gameDataDAO.listGames());
    }

    public CreateGameResponse createGame(String authToken, CreateGameRequest createGameRequest) throws DataAccessException {
        if (authDataDAO.getAuth(authToken) == null) {
            throw new UnauthorizedException();
        }
        GameData game = new GameData(0,
                null,
                null,
                createGameRequest.gameName(),
                null);
        int gameID = gameDataDAO.createGame(game);
        return new CreateGameResponse(gameID);
    }

    public void joinGame(String authToken, JoinGameRequest joinGameRequest) throws DataAccessException {
        AuthData authData = authDataDAO.getAuth(authToken);
        if (authData == null) {
            throw new UnauthorizedException();
        }

        GameData currGame = gameDataDAO.getGame(joinGameRequest.gameID());

        if (currGame == null) {
            throw new BadRequestException();
        }

        String blackUsername = currGame.blackUsername();
        String whiteUsername = currGame.whiteUsername();

        if (joinGameRequest.playerColor() == ChessGame.TeamColor.BLACK) {
            if (currGame.blackUsername() != null) {
                throw new AlreadyTakenException();
            }
            blackUsername = authData.username();
        } else if (joinGameRequest.playerColor() == ChessGame.TeamColor.WHITE) {
            if (currGame.whiteUsername() != null) {
                throw new AlreadyTakenException();
            }
            whiteUsername = authData.username();
        }

        GameData updatedGame = new GameData(currGame,
                whiteUsername,
                blackUsername,
                currGame.gameName(),
                currGame.game());

        gameDataDAO.updateGame(updatedGame);
        return;
    }
}
