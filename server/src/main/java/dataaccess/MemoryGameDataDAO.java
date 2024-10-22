package dataaccess;

import model.GameData;

import java.util.ArrayList;
import java.util.Collection;

public class MemoryGameDataDAO implements GameDataDAO {
    Collection<GameData> gameDataCollection;

    public MemoryGameDataDAO() {
        gameDataCollection = new ArrayList<>();
        return;
    }

    public GameData getGame(int gameID) throws DataAccessException {
        for (GameData game : gameDataCollection) {
            if (game.gameID() == gameID) {
                return game;
            }
        }
        return null;
    }

    public void CreateGame(GameData gameData) throws DataAccessException {
        gameDataCollection.add(gameData);
    }

    public void UpdateGame(GameData gameData) throws DataAccessException {
        int gameID = gameData.gameID();

        GameData gameToUpdate;
        for (GameData game : gameDataCollection) {
            gameDataCollection.remove(game);
        }
        gameDataCollection.add(gameData);
        return;
    }

    public Collection<GameData> listGames() throws DataAccessException {
        return gameDataCollection;
    }
}
