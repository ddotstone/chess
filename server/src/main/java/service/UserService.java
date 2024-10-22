package service;

import model.UserData;
import model.AuthData;
import dataaccess.*;

import java.util.UUID;

public class UserService {
    private final UserDataDAO userDataDAO;
    private final AuthDataDAO authDataDAO;

    public UserService(UserDataDAO userDataDao, AuthDataDAO authDataDao) {
        this.userDataDAO = userDataDao;
        this.authDataDAO = authDataDao;
    }


    public AuthData register(UserData user) throws DataAccessException {
        if (userDataDAO.getUser(user.username()) != null) {
            return null;
        }
        AuthData authData = new AuthData(getUUID(), user.username());
        authDataDAO.createAuth(authData);
        userDataDAO.createUser(user);
        return authData;
    }

    public AuthData login(UserData user) throws DataAccessException {
        UserData userStored = userDataDAO.getUser(user.username());
        if (userStored == null) {
            return null;
        }
        if (!userStored.equals(user)) {
            return null;
        }

        AuthData authData = new AuthData(getUUID(), user.username());
        authDataDAO.createAuth(authData);
        return authData;
    }

    public void logout(AuthData auth) throws DataAccessException {
        authDataDAO.deleteAuth(auth.authToken());
    }

    private String getUUID() {
        return UUID.randomUUID().toString();
    }
}