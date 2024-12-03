package websocketClient;

import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;

public interface NotificationHandler {
    void notify(NotificationMessage notification);

    void error(ErrorMessage error);

    void loadGame(LoadGameMessage loadGame);
}
