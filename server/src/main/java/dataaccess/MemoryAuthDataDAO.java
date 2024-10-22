package dataaccess;

import model.AuthData;

import java.util.Collection;

public class MemoryAuthDataDAO {
    Collection<AuthData> authDataCollection;

    public void createAuth(AuthData authData) throws DataAccessException {
        authDataCollection.add(authData);
    }

    public AuthData getAuth(String authToken) throws DataAccessException {
        for (AuthData auth : authDataCollection) {
            if (auth.authToken().equals(authToken)) {
                return auth;
            }
        }
        return null;
    }

    public void deleteAuth(AuthData authData) throws DataAccessException;
}
