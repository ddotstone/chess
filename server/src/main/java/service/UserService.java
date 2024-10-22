package service;

import model.UserData;
import model.AuthData;
import dataaccess.*;
import request.*;
import response.*;

import java.util.UUID;

public class UserService {
    private final UserDataDAO userDataDAO;
    private final AuthDataDAO authDataDAO;

    public UserService(UserDataDAO userDataDao, AuthDataDAO authDataDao) {
        this.userDataDAO = userDataDao;
        this.authDataDAO = authDataDao;
    }


    public RegisterResponse register(RegisterRequest registerRequest) throws DataAccessException {
        if (userDataDAO.getUser(registerRequest.username()) != null) {
            return null;
        }
        AuthData authData = new AuthData(getUUID(), registerRequest.username());
        authDataDAO.createAuth(authData);
        UserData user = new UserData(registerRequest.username(),
                registerRequest.password(),
                registerRequest.email());

        userDataDAO.createUser(user);
        return new RegisterResponse(authData.authToken());
    }

    public LoginResponse login(LoginRequest loginRequest) throws DataAccessException {
        UserData userStored = userDataDAO.getUser(loginRequest.username());
        if (userStored == null) {
            return null;
        }
        if (!userStored.password().equals(loginRequest.password())) {
            return null;
        }

        AuthData authData = new AuthData(getUUID(), loginRequest.username());
        authDataDAO.createAuth(authData);
        return new LoginResponse(authData.authToken());
    }

    public void logout(LogoutRequest logoutRequest) throws DataAccessException {
        authDataDAO.deleteAuth(logoutRequest.authToken());
    }

    private String getUUID() {
        return UUID.randomUUID().toString();
    }
}