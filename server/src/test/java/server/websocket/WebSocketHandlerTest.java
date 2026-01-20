package server.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import commons.SyncEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;


public class WebSocketHandlerTest {
    private WebSocketHandler handler;
    private ObjectMapper mapper;

    @BeforeEach
    void setUp(){
        handler = new WebSocketHandler();
        mapper = new ObjectMapper();
    }

    @Test
    void testWebSocketSessionOpened() throws IOException {
        WebSocketSession session = mock(WebSocketSession.class);

        SyncEvent testEvent = new SyncEvent.RecipeDeleted(1L);
        // recipeDeleted is being used because it requires little setup
        handler.broadcast(testEvent);

        verify(session, never()).sendMessage(any(TextMessage.class));
        // verify nothing has been sent to the session

        handler.afterConnectionEstablished(session);
        handler.broadcast(testEvent);

        verify(session, atLeastOnce()).sendMessage(any(TextMessage.class));
        // check at least one message is sent to the session
    }

    @Test
    void testWebSocketSessionClosed() throws IOException {
        WebSocketSession session = mock(WebSocketSession.class);

        SyncEvent testEvent = new SyncEvent.RecipeDeleted(1L);

        handler.afterConnectionEstablished(session);
        session.close();
        handler.afterConnectionClosed(session, CloseStatus.NORMAL);

        handler.broadcast(testEvent);


        verify(session, never()).sendMessage(any(TextMessage.class));
        // check at least no message is sent to the session after it closes
    }

    @Test
    void testSendMessageToAllSockets() throws IOException {
        WebSocketSession session1 = mock(WebSocketSession.class);
        WebSocketSession session2 = mock(WebSocketSession.class);

        handler.afterConnectionEstablished(session1);
        handler.afterConnectionEstablished(session2);

        SyncEvent testEvent = new SyncEvent.RecipeIngredientDeleted(1L, 100L);

        handler.broadcast(testEvent);

        ArgumentCaptor<TextMessage> captor = ArgumentCaptor.forClass(TextMessage.class);

        verify(session1, times(1)).sendMessage(captor.capture());
        verify(session2, times(1)).sendMessage(captor.capture());

        TextMessage message1 = captor.getAllValues().get(0);
        TextMessage message2 = captor.getAllValues().get(1);

        assertEquals(message1.getPayload(), message2.getPayload());
        // make sure both messages are equal
    }

    @Test
    void testMessageTypePersisted() throws IOException {
        WebSocketSession session = mock(WebSocketSession.class);

        handler.afterConnectionEstablished(session);

        SyncEvent.RecipeIngredientDeleted riDeleteEvent = new SyncEvent.RecipeIngredientDeleted(1L, 100L);
        SyncEvent.RecipeDeleted recipeDeleteEvent = new SyncEvent.RecipeDeleted(10L);

        handler.broadcast(riDeleteEvent);
        handler.broadcast(recipeDeleteEvent);

        ArgumentCaptor<TextMessage> captor = ArgumentCaptor.forClass(TextMessage.class);

        verify(session, times(2)).sendMessage(captor.capture());
        TextMessage message1 = captor.getAllValues().get(0);
        TextMessage message2 = captor.getAllValues().get(1);

        assertNotEquals(message1.getPayload(), message2.getPayload());

        SyncEvent receivedRiDeleteEvent = mapper.readValue(message1.getPayload(), SyncEvent.class);
        SyncEvent receivedRecipeDeleteEvent = mapper.readValue(message2.getPayload(), SyncEvent.class);

        assertEquals(receivedRiDeleteEvent.getClass(), SyncEvent.RecipeIngredientDeleted.class);
        assertEquals(receivedRecipeDeleteEvent.getClass(), SyncEvent.RecipeDeleted.class);

        assertEquals(receivedRiDeleteEvent.getRecipeId(), riDeleteEvent.getRecipeId());
        assertEquals(((SyncEvent.RecipeIngredientDeleted)receivedRiDeleteEvent).getIngredientId(), riDeleteEvent.getIngredientId());

        assertEquals(receivedRecipeDeleteEvent.getRecipeId(), recipeDeleteEvent.getRecipeId());
    }
}
