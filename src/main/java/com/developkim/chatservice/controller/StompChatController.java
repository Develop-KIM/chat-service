package com.developkim.chatservice.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
@Slf4j
public class StompChatController {

    /**
     * 클라이언트에서 "/chats" 경로로 발행된 메시지를 처리하고,
     * 구독 경로 "/sub/chats"로 메시지를 전송
     *
     * @param message 클라이언트로부터 전달된 메시지 (STOMP payload)
     * @return 구독 경로로 전송될 메시지
     */
    @MessageMapping("/chats")
    // STOMP 프로토콜에서 "/chats" 경로로 발행된 메시지를 이 메서드로 매핑합니다.
    // 클라이언트는 "/pub/chats"로 메시지를 발행하며, 이 메서드가 호출됩니다.
    @SendTo("/sub/chats")
    // 처리된 메시지를 "/sub/chats" 경로를 구독 중인 클라이언트에게 브로드캐스트합니다.
    public String handleMessage(@Payload String message) {
        log.info("{} received", message);

        // 메시지를 그대로 반환하여 "/sub/chats"를 구독 중인 클라이언트들에게 전송합니다.
        return message;
    }
}
