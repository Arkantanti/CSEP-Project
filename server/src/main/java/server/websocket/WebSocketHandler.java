package server.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import commons.SyncEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@Component
public class WebSocketHandler extends TextWebSocketHandler {
    private final Set<WebSocketSession> sessions = new HashSet<>();
    private final ObjectWriter writer = new ObjectMapper().writer();

    @Override
    public void afterConnectionEstablished(WebSocketSession session){
        sessions.add(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status){
        sessions.remove(session);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception{
        if (session.isOpen()){
            sessions.remove(session);
            session.close();
        };
    }

    /**
     * called to send an event to all clients
     * @param event the event to send
     */
    public void broadcast(SyncEvent event) {
        String msg;
        try{
            msg =  writer.writeValueAsString(event);
        }
        catch (JsonProcessingException e){
            e.printStackTrace();
            msg = "Invalid Event";
        }

        TextMessage message = new TextMessage(msg);

        System.out.println("Sending: " + msg);
        System.out.println("To " + sessions.size() + " sessions");
        for (WebSocketSession session : sessions){
            try{
                session.sendMessage(message);
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }
    }
}
