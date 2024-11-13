package ui;

import ui.client.ChessClient;
import ui.client.SignedOutChessClient;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Scanner;

import static ui.EscapeSequences.*;


public class Repl {
    private ChessClient client;

    public Repl(String serverUrl) {
        client = new SignedOutChessClient(serverUrl);
    }

    public void run() throws InvocationTargetException, InstantiationException, IllegalAccessException {
        System.out.println(WHITE_QUEEN + " Welcome to the Chess. Sign in to start. " + WHITE_QUEEN);
        System.out.print(SET_TEXT_COLOR_BLUE + client.help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            if (client.transferStates() != null) {
                Class<?> clientClass = client.transferStates();
                Constructor<?> constructor = null;
                try {
                    constructor = clientClass.getConstructor(client.getClass());
                } catch (Exception ex) {
                    System.out.println("Failure Switching States\n");
                }

                client = (ChessClient) constructor.newInstance(client);
                System.out.println(SET_TEXT_COLOR_BLUE + client.eval("help"));
            }
            String line = scanner.nextLine();
            try {
                result = client.eval(line);
                System.out.print(SET_TEXT_COLOR_BLUE + result + "\n");
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(SET_TEXT_COLOR_RED + msg + "\n");
            }
        }
        System.out.println();
    }
}
