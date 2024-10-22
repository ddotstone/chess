package service;

import dataaccess.AuthDataDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDataDAO;
import dataaccess.UserDataDAO;

public class DatabaseService {
    private final AuthDataDAO authDataDAO;
    private final UserDataDAO userDataDAO;
    private final GameDataDAO gameDataDAO;

    public DatabaseService(AuthDataDAO authDataDAO, UserDataDAO userDataDAO, GameDataDAO gameDataDAO) {
        this.authDataDAO = authDataDAO;
        this.userDataDAO = userDataDAO;
        this.gameDataDAO = gameDataDAO;
    }

    public void clear() throws DataAccessException {
        authDataDAO.clear();
        userDataDAO.clear();
        gameDataDAO.clear();
    }
}
