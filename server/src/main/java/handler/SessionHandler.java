package handler;

import dataaccess.BadRequestException;
import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDataDAO;
import dataaccess.MemoryUserDataDAO;

import static handler.HandlerUnitFunctions.*;

import request.*;
import response.*;
import spark.Request;
import spark.Response;
import service.UserService;

import javax.xml.crypto.Data;

public class SessionHandler {
    private final UserService userService;

    public SessionHandler() {
        this.userService = new UserService(new MemoryUserDataDAO(), new MemoryAuthDataDAO());
    }

    public Object LoginHandler(Request req, Response res) throws DataAccessException {
        LoginRequest loginRequest = DeserializeJson(req.body(), LoginRequest.class);
        if (loginRequest.username() == null || loginRequest.password() == null) {
            throw new BadRequestException();
        }
        LoginResponse loginResponse = userService.login(loginRequest);
        var body = SerializeJson(loginResponse, LoginResponse.class);
        res.body(body);
        res.status(200);
        return body;
    }

    public Object LogoutHandler(Request req, Response res) throws DataAccessException {
        String authToken = GetAuth(req);
        userService.logout(authToken);
        res.status(200);
        return "{}";
    }

}
