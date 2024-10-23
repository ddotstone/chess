package handler;

import static handler.HandlerUnitFunctions.*;

import dataaccess.*;
import request.*;
import response.*;
import spark.Request;
import spark.Response;
import service.UserService;

public class SessionHandler {
    private final UserService userService;

    public SessionHandler() {
        this.userService = new UserService(new MemoryUserDataDAO(), new MemoryAuthDataDAO());
    }

    public Object loginHandler(Request req, Response res) throws DataAccessException {
        LoginRequest loginRequest = deserializeJson(req.body(), LoginRequest.class);
        if (loginRequest.username() == null || loginRequest.password() == null) {
            throw new BadRequestException();
        }
        LoginResponse loginResponse = userService.login(loginRequest);
        var body = serializeJson(loginResponse, LoginResponse.class);
        res.body(body);
        res.status(200);
        return body;
    }

    public Object logoutHandler(Request req, Response res) throws DataAccessException {
        String authToken = getAuth(req);
        userService.logout(authToken);
        res.status(200);
        return "{}";
    }

}
