package dataaccess;

import model.GameData;

import javax.xml.crypto.Data;

public interface GameDataDAO {
    public GameData getGame(String gameID) throws DataAccessException;

    public void CreateGame(GameData gameData) throws DataAccessException;

    public void UpdateGame(GameData gameData) throws DataAccessException;
}
