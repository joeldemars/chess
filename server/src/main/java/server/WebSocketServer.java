package server;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

@WebSocket
public class WebSocketServer {
    @OnWebSocketMessage
    public void onMessage(Session session, String message) {
        System.out.println("Received message: " + message);
    }
}
