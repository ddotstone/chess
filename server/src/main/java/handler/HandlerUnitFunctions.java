package handler;

import com.google.gson.Gson;
import dataaccess.BadRequestException;

import java.lang.reflect.Type;

import dataaccess.DataAccessException;
import spark.Request;

public class HandlerUnitFunctions {
    public static String SerializeJson(Object target, Type typeOfTarget) {
        if (target == null) {
            return null;
        }
        Gson serializer = new Gson();
        return serializer.toJson(target, typeOfTarget);
    }

    public static <T> T DeserializeJson(String target, Class<T> classOfT) {
        if (target == null) {
            return null;
        }
        Gson serializer = new Gson();
        return serializer.fromJson(target, classOfT);
    }

    public static String GetAuth(Request req) throws DataAccessException {
        String authToken = req.headers("authorization");

        if (authToken == null) {
            throw new BadRequestException();
        }
        return authToken;
    }
}