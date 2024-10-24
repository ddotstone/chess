package dataaccess;

import model.GameData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class MemoryGameDataDAO implements GameDataDAO {

    public static final Collection<GameData> GAME_DATA_COLLECTION = new ArrayList<>();

    @Override
    public void clear() throws DataAccessException {
        GAME_DATA_COLLECTION.clear();
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        for (GameData game : GAME_DATA_COLLECTION) {
            if (game.gameID() == gameID) {
                return game;
            }
        }
        return null;
    }

    @Override
    public void createGame(GameData gameData) throws DataAccessException {
        GAME_DATA_COLLECTION.add(gameData);
    }

    @Override
    public void updateGame(GameData gameData) throws DataAccessException {
        int gameID = gameData.gameID();

        GameData gameToUpdate;
        Iterator<GameData> listIterator = GAME_DATA_COLLECTION.iterator();
        while (listIterator.hasNext()) {
            GameData currentGame = listIterator.next();
            if (currentGame.gameID() == gameData.gameID()) {
                listIterator.remove();
                GAME_DATA_COLLECTION.add(gameData);
                return;
            }
        }
        throw new UnauthorizedException();
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        return GAME_DATA_COLLECTION;
    }
}
