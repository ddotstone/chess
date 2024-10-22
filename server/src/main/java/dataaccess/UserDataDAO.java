package dataaccess;

import model.UserData;

public interface UserDataDAO {

    public void clear() throws DataAccessException;

    public void createUser(UserData userData) throws DataAccessException;

    public UserData getUser(String username) throws DataAccessException;
}
