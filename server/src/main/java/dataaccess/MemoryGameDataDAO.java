package dataaccess;

import model.GameData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class MemoryGameDataDAO implements GameDataDAO {

    public static final Collection<GameData> gameDataCollection = new ArrayList<>();

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
        Iterator<GameData> listIterator = gameDataCollection.iterator();
        while (listIterator.hasNext()) {
            GameData currentGame = listIterator.next();
            if (currentGame.gameID() == gameData.gameID()) {
                listIterator.remove();
                gameDataCollection.add(gameData);
                return;
            }
        }
        throw new UnauthorizedException();
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        return gameDataCollection;
    }
}
