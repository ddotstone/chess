package ui;

import chess.ChessGame;
import ui.client.ChessClient;
import ui.client.InGameChessClient;
import ui.client.SignedOutChessClient;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocketClient.NotificationHandler;
import model.GameData;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Scanner;

import static ui.EscapeSequences.*;


public class Repl implements NotificationHandler {
    private ChessClient client;

    public Repl(String serverUrl) {
        client = new SignedOutChessClient(serverUrl, this);
    }

    public void run() throws InvocationTargetException, InstantiationException, IllegalAccessException {
        System.out.println(WHITE_QUEEN + " Welcome to the Chess. Sign in to start. " + WHITE_QUEEN);
        System.out.print(SET_TEXT_COLOR_BLUE + client.help() + "\n");

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            if (client.transferStates() != null) {
                Class<?> clientClass = client.transferStates();
                Constructor<?> constructor = null;
                try {
                    constructor = clientClass.getConstructor(client.getClass());
                } catch (Exception ex) {
                    System.out.println(SET_BG_COLOR_RED + "Failure Switching States\n");
                }

                client = (ChessClient) constructor.newInstance(client);
                try {
                    System.out.print(SET_TEXT_COLOR_BLUE + client.eval("help") + "\n");
                } catch (Exception ex) {
                    System.out.println(SET_TEXT_COLOR_RED + "Error printing help");
                }

            }
            String line = scanner.nextLine();
            try {
                result = client.eval(line);
                System.out.print(SET_TEXT_COLOR_BLUE + result + "\n");
            } catch (Throwable e) {
                var msg = e.getMessage();
                System.out.print(SET_TEXT_COLOR_RED + msg + "\n");
            }
        }
        System.out.println();
    }

    public void notify(NotificationMessage notificationMessage) {
        System.out.print(SET_TEXT_COLOR_BLUE + notificationMessage.getMessage() + "\n");
    }

    public void error(ErrorMessage errorMessage) {
        System.out.print(SET_TEXT_COLOR_RED + errorMessage.getErrorMessage() + "\n");
    }

    public void loadGame(LoadGameMessage loadGameMessage) {
        ChessGame game = loadGameMessage.getGameData();
        InGameChessClient inGameChessClient = (InGameChessClient) client;
        inGameChessClient.printBoard(game);
    }
}
