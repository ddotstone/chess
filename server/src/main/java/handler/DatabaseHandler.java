package handler;

import com.google.gson.Gson;
import dataaccess.*;
import service.DatabaseService;
import spark.Request;
import spark.Response;

import java.util.Map;

public class DatabaseHandler {
    public Object ClearRequest(Request req, Response res) throws DataAccessException {
        Gson jsonResponse;
        (new DatabaseService(new MemoryAuthDataDAO(),
                new MemoryUserDataDAO(),
                new MemoryGameDataDAO())).clear();
        res.status(200);
        return null;
    }
}
