package ui.client;

import exception.ResponseException;

public interface ChessClient {

    public String getState();

    public String eval(String input) throws ResponseException;

    public String help();

    public Class transferStates();
}
