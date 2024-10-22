package dataaccess;

import model.AuthData;

import javax.xml.crypto.Data;

public interface AuthDataDAO {
    public AuthData createAuth() throws DataAccessException;

    public AuthData getAuth(String authToken) throws DataAccessException;

    public void deleteAuth(AuthData authData) throws DataAccessException;
}
