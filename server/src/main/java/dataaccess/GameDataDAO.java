package dataaccess;

import model.GameData;

import javax.xml.crypto.Data;
import java.util.Collection;

public interface GameDataDAO {
    public GameData getGame(String gameID) throws DataAccessException;

    public void CreateGame(GameData gameData) throws DataAccessException;

    public void UpdateGame(GameData gameData) throws DataAccessException;

    public Collection<GameData> listGames() throws DataAccessException;
}
