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
            throw new AlreadyTakenException();
        }
        AuthData authData = new AuthData(getUUID(), registerRequest.username());
        authDataDAO.createAuth(authData);
        UserData user = new UserData(registerRequest.username(),
                registerRequest.password(),
                registerRequest.email());

        userDataDAO.createUser(user);
        return new RegisterResponse(registerRequest.username(), authData.authToken());
    }

    public LoginResponse login(LoginRequest loginRequest) throws DataAccessException {
        UserData userStored = userDataDAO.getUser(loginRequest.username());
        if (userStored == null) {
            throw new UnauthorizedException();
        }
        if (!userStored.password().equals(loginRequest.password())) {
            throw new UnauthorizedException();
        }

        AuthData authData = new AuthData(getUUID(), loginRequest.username());
        authDataDAO.createAuth(authData);
        return new LoginResponse(loginRequest.username(), authData.authToken());
    }

    public void logout(String authToken) throws DataAccessException {
        authDataDAO.deleteAuth(authToken);
    }

    private String getUUID() {
        return UUID.randomUUID().toString();
    }
}