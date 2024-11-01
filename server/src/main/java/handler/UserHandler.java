package handler;

import static handler.HandlerUnitFunctions.*;

import dataaccess.*;
import request.*;
import response.*;
import service.UserService;
import spark.Request;
import spark.Response;


public class UserHandler {
    private final UserService userService;

    public UserHandler() throws DataAccessException {
        this.userService = new UserService(new MemoryUserDataDAO(), new SQLAuthDataDAO());
    }

    public Object registerHandler(Request req, Response res) throws DataAccessException {
        RegisterRequest registerRequest = deserializeJson(req.body(), RegisterRequest.class);
        if (registerRequest.username() == null ||
                registerRequest.password() == null ||
                registerRequest.email() == null) {
            throw new BadRequestException();
        }
        RegisterResponse registerResponse = userService.register(registerRequest);
        var body = serializeJson(registerResponse, RegisterResponse.class);
        res.body(body);
        res.status(200);
        return body;
    }
}
