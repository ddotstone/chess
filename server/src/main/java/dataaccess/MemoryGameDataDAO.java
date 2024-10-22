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

    @Override
    public void clear() throws DataAccessException {
        gameDataCollection.clear();
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        for (GameData game : gameDataCollection) {
            if (game.gameID() == gameID) {
                return game;
            }
        }
        return null;
    }

    @Override
    public void CreateGame(GameData gameData) throws DataAccessException {
        gameDataCollection.add(gameData);
    }

    @Override
    public void UpdateGame(GameData gameData) throws DataAccessException {
        int gameID = gameData.gameID();

        GameData gameToUpdate;
        for (GameData game : gameDataCollection) {
            gameDataCollection.remove(game);
            break;
        }
        gameDataCollection.add(gameData);
        return;
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        return gameDataCollection;
    }
}
