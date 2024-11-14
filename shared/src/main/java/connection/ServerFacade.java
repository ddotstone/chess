package connection;

import chess.ChessGame;
import com.google.gson.Gson;
import exception.ResponseException;
import model.*;
import model.request.CreateGameRequest;
import model.request.JoinGameRequest;
import model.request.LoginRequest;
import model.request.RegisterRequest;
import model.response.CreateGameResponse;
import model.response.ListGameResponse;
import model.response.LoginResponse;
import model.response.RegisterResponse;


import java.io.*;
import java.net.*;
import java.util.Collection;

public class ServerFacade {

    private final String serverUrl;

    public ServerFacade(String url) {
        serverUrl = url;
    }

    private static void addAuth(String authToken, HttpURLConnection http) {
        if (authToken != null) {
            http.addRequestProperty("authorization", authToken);
        }
    }

    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }

    public AuthData signIn(String username, String password) throws ResponseException {
        LoginRequest request = new LoginRequest(username, password);
        var path = "/session";
        LoginResponse response = this.makeRequest("POST", path, request, null, LoginResponse.class);
        return new AuthData(response.authToken(), username);
    }

    public void signOut(String authToken) throws ResponseException {
        var path = "/session";
        this.makeRequest("DELETE", path, null, authToken, null);
    }

    public void clear() throws ResponseException {
        var path = "/db";
        this.makeRequest("DELETE", path, null, null, null);
    }

    public int createGame(String authToken, String gameName) throws ResponseException {
        var path = "/game";
        CreateGameRequest request = new CreateGameRequest(gameName);
        CreateGameResponse response = this.makeRequest("POST", path, request, authToken, CreateGameResponse.class);
        return response.gameID();
    }

    public void joinGame(String authToken, int gameID, ChessGame.TeamColor teamColor) throws ResponseException {
        var path = "/game";
        JoinGameRequest request = new JoinGameRequest(gameID, teamColor);
        this.makeRequest("PUT", path, request, authToken, null);
    }

    public Collection<GameData> listGames(String authToken) throws ResponseException {
        var path = "/game";
        ListGameResponse response = this.makeRequest("GET", path, null, authToken, ListGameResponse.class);
        return response.games();
    }

    public AuthData register(String username, String password, String email) throws ResponseException {
        var path = "/user";
        RegisterRequest request = new RegisterRequest(username, password, email);
        RegisterResponse response = this.makeRequest("POST", path, request, null, RegisterResponse.class);
        return new AuthData(response.authToken(), response.username());
    }

    private <T> T makeRequest(String method, String path, Object request, String auth, Class<T> responseClass) throws ResponseException {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);

            addAuth(auth, http);
            http.setDoOutput(true);
            writeBody(request, http);
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, ResponseException {
        var status = http.getResponseCode();
        if (!isSuccessful(status)) {
            throw new ResponseException(status, "failure: " + status);
        }
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}