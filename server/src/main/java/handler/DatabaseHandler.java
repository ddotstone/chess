package handler;

import com.google.gson.Gson;
import dataaccess.*;
import service.DatabaseService;
import spark.Request;
import spark.Response;

public class DatabaseHandler {
    public Object clearRequest(Request req, Response res) throws DataAccessException {
        Gson jsonResponse;
        (new DatabaseService(new MemoryAuthDataDAO(),
                new MemoryUserDataDAO(),
                new MemoryGameDataDAO())).clear();
        res.status(200);
        return "{}";
    }
}
