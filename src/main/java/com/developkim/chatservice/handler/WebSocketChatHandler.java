package com.developkim.chatservice.handler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Slf4j
@Component
public class WebSocketChatHandler extends TextWebSocketHandler {

    private static final Map<String, WebSocketSession> session = new ConcurrentHashMap<>();

    // 웹소켓 클라이언트가 서버로 연결한 이후로 실행하는 메서드
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("{} connected.", session.getId());

        this.session.put(session.getId(), session);
    }

    // 웹소켓에서 메시지를 보냈을 때 처리하는 메서드
    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        log.info("{} sent {}", session.getId(), message.getPayload());

        this.session.values().forEach(webSocketSession -> {
            try {
                webSocketSession.sendMessage(message);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    // 서버에 접속된 클라이언트가 연결을 끊었을 때 실행하는 메서드
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.info("{} disconnected.", session.getId());

        this.session.remove(session.getId());
    }
}
