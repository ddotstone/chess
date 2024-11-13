package ui.client;

import server.ServerFacade;

public interface ChessClient {

    public String eval(String input);

    public String help();

    public Class transferStates();
}
