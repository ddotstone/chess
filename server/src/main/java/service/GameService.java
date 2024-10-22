package service;

import java.util.Collection;

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

    public ListGameReponse listGames(ListGameRequest listGameRequest) throws DataAccessException {
        if (authDataDAO.getAuth(listGameRequest.authToken()) == null) {
            return null;
        }
        return new ListGameReponse(gameDataDAO.listGames());
    }

    public CreateGameResponse CreateGame(CreateGameRequest createGameRequest) throws DataAccessException {
        if (authDataDAO.getAuth(createGameRequest.authToken()) == null) {
            return null;
        }
        GameData game = new GameData(gameCount++,
                null,
                null,
                createGameRequest.gameName(),
                null);
        gameDataDAO.CreateGame(game);
        return new CreateGameResponse(Integer.toString(gameCount++));
    }

    public void JoinGame(JoinGameRequest joinGameRequest) throws DataAccessException {
        AuthData authData = authDataDAO.getAuth(joinGameRequest.authToken());
        if (authData == null) {
            return;
        }

        GameData currGame = gameDataDAO.getGame(joinGameRequest.gameID());
        if (currGame == null) {
            return;
        }

        String blackUsername;
        String whiteUsername;

        if (joinGameRequest.color() == ChessGame.TeamColor.BLACK) {
            if (currGame.blackUsername() != null) {
                return;
            }
            blackUsername = authData.username();
            whiteUsername = null;
        } else if (joinGameRequest.color() == ChessGame.TeamColor.WHITE) {
            if (currGame.whiteUsername() != null) {
                return;
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
