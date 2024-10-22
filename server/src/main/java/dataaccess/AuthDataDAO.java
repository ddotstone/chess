package dataaccess;

import model.AuthData;

import javax.xml.crypto.Data;

public interface AuthDataDAO {
    public void createAuth() throws DataAccessException;

    public AuthData getAuth(String authToken) throws DataAccessException;

    public void deleteAuth(String authToken) throws DataAccessException;
}
