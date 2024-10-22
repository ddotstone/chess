package dataaccess;

import model.AuthData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class MemoryAuthDataDAO implements AuthDataDAO {

    private static final Collection<AuthData> authDataCollection = new ArrayList<>();

    @Override
    public void clear() {
        authDataCollection.clear();
    }

    @Override
    public void createAuth(AuthData authData) throws DataAccessException {
        authDataCollection.add(authData);
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        for (AuthData auth : authDataCollection) {
            if (auth.authToken().equals(authToken)) {
                return auth;
            }
        }
        return null;
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        Iterator<AuthData> listIterator = authDataCollection.iterator();
        while (listIterator.hasNext()) {
            AuthData auth = listIterator.next();
            if (auth.authToken().equals(authToken)) {
                listIterator.remove();
                return;
            }
        }
        throw new UnauthorizedException();
    }
}
