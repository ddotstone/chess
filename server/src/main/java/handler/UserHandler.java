package handler;

import dataaccess.*;
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

    public Object RegisterHandler(Request req, Response res) throws DataAccessException {
        RegisterRequest registerRequest = DeserializeJson(req.body(), RegisterRequest.class);
        if (registerRequest.username() == null ||
                registerRequest.password() == null ||
                registerRequest.email() == null) {
            throw new BadRequestException();
        }
        RegisterResponse registerResponse = userService.register(registerRequest);
        var body = SerializeJson(registerResponse, RegisterResponse.class);
        res.body(body);
        res.status(200);
        return body;
    }
}
