package com.developkim.chatservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@EnableWebSocketMessageBroker
@Configuration
public class StompConfiguration implements WebSocketMessageBrokerConfigurer {

    // 웹소켓 클라이언트가 어떠한 경로로 서버에 접속해야하는지 엔드포인트를 지정해주는 메서드
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 클라이언트가 "/stomp/chats" 엔드포인트로 연결하여 WebSocket 세션을 생성합니다.
        // 이 경로는 STOMP 프로토콜을 통해 서버와 클라이언트가 통신하는 진입점 역할을 합니다.
        registry.addEndpoint("/stomp/chats");
    }

    /**
     * 메시지 브로커 설정을 구성
     * 메시지 발행 경로와 구독 경로를 정의하여 클라이언트와 서버 간 메시지 전달 방식을 설정
     *
     * @param registry 메시지 브로커 설정을 위한 레지스트리 객체
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 클라이언트가 메시지를 발행할 때 사용하는 기본 경로를 설정
        registry.setApplicationDestinationPrefixes("/pub");

        // 메시지 브로커를 활성화하고, 구독자가 메시지를 받을 경로를 설정
       registry.enableSimpleBroker("/sub");
    }
}
