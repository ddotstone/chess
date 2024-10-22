package dataaccess;

import model.GameData;

import java.util.ArrayList;
import java.util.Collection;


public interface GameDataDAO {

    public void clear() throws DataAccessException;

    public GameData getGame(int gameID) throws DataAccessException;

    public void CreateGame(GameData gameData) throws DataAccessException;

    public void UpdateGame(GameData gameData) throws DataAccessException;

    public Collection<GameData> listGames() throws DataAccessException;
}

