package handler;

import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDataDAO;
import dataaccess.MemoryGameDataDAO;
import dataaccess.MemoryUserDataDAO;
import request.*;
import response.*;
import service.UserService;

import static handler.HandlerUnitFunctions.*;

import spark.Request;
import spark.Response;

import javax.xml.crypto.Data;


public class UserHandler {
    private final UserService userService;

    public UserHandler() {
        this.userService = new UserService(new MemoryUserDataDAO(), new MemoryAuthDataDAO());
    }

    public void RegisterHandler(Request req, Response res) throws DataAccessException {
        RegisterRequest registerRequest = DeserializeJson(req.body(), RegisterRequest.class);
        if (registerRequest.username() == null ||
                registerRequest.password() == null ||
                registerRequest.email() == null) {
            return;
        }
        RegisterResponse registerResponse = userService.register(registerRequest);
        res.body(SerializeJson(registerResponse, RegisterResponse.class));
        res.status(200);
        return;
    }
}
