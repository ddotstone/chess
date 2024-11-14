package handler;

import static handler.HandlerUnitFunctions.*;

import dataaccess.*;
import model.response.*;
import model.request.*;
import service.UserService;
import spark.Request;
import spark.Response;


public class UserHandler {
    private final UserService userService;

    public UserHandler() throws DataAccessException {
        this.userService = new UserService(new SQLUserDataDAO(), new SQLAuthDataDAO());
    }

    public Object registerHandler(Request req, Response res) throws DataAccessException {
        RegisterRequest registerRequest = deserializeJson(req.body(), RegisterRequest.class);
        if ((registerRequest.username() == null || (registerRequest.username().equals("null")) ||
                registerRequest.password() == null || (registerRequest.password().equals("null")) ||
                registerRequest.email() == null || registerRequest.email().equals("null"))) {
            throw new BadRequestException();
        }
        RegisterResponse registerResponse = userService.register(registerRequest);
        var body = serializeJson(registerResponse, RegisterResponse.class);
        res.body(body);
        res.status(200);
        return body;
    }
}
