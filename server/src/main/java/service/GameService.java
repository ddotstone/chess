package service;

import java.util.Collection;

import chess.ChessGame;
import dataaccess.AuthDataDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDataDAO;
import dataaccess.MemoryAuthDataDAO;
import model.AuthData;
import model.GameData;

public class GameService {
    private static int gameCount = 0;
    private final AuthDataDAO authDataDAO;
    private final GameDataDAO gameDataDAO;

    public GameService(AuthDataDAO authDataDAO, GameDataDAO gameDataDAO) {
        this.authDataDAO = authDataDAO;
        this.gameDataDAO = gameDataDAO;
    }

    public Collection<GameData> listGames(AuthData auth) throws DataAccessException {
        if (authDataDAO.getAuth(auth.authToken()) == null) {
            return null;
        }
        return gameDataDAO.listGames();
    }

    public GameData CreateGame(AuthData authData, GameData gameData) throws DataAccessException {
        if (authDataDAO.getAuth(authData.authToken()) == null) {
            return null;
        }
        GameData game = new GameData(gameCount++,
                gameData.whiteUsername(),
                gameData.blackUsername(),
                gameData.gameName(),
                gameData.game());
        gameDataDAO.CreateGame(game);
        return game;
    }

    public void JoinGame(AuthData auth, GameData game) throws DataAccessException {
        AuthData authData = authDataDAO.getAuth(auth.authToken());
        if (authData == null) {
            return;
        }

        GameData currGame = gameDataDAO.getGame(game.gameID());
        if (currGame == null) {
            return;
        }
        GameData updatedGame = new GameData(currGame,
                game.whiteUsername(),
                game.blackUsername(),
                currGame.gameName(),
                currGame.game());

        gameDataDAO.UpdateGame(updatedGame);
        return;
    }
}
