package dataaccess;

import model.GameData;

import javax.xml.crypto.Data;
import java.util.ArrayList;
import java.util.Collection;

public interface GameDataDAO {
    public GameData getGame(int gameID) throws DataAccessException;

    public void CreateGame(GameData gameData) throws DataAccessException;

    public void UpdateGame(GameData gameData) throws DataAccessException;

    public Collection<GameData> listGames() throws DataAccessException;
}

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
    }

    public void UpdateGame(GameData gameData) throws DataAccessException {
    }

    public Collection<GameData> listGames() throws DataAccessException {
    }
}
