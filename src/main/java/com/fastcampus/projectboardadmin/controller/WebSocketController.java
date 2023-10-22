package com.fastcampus.projectboardadmin.controller;


import com.fastcampus.projectboardadmin.dto.websocket.WebSocketMessage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.security.Principal;


@Controller
public class WebSocketController {

    // Principal : Spring Security 인증 정보
    @MessageMapping("/hello")
    @SendTo("/topic/chat")
    public WebSocketMessage chat(WebSocketMessage message, Principal principal) throws Exception {
        Thread.sleep(500);
        return WebSocketMessage.of("안녕하세요" + principal.getName() + "! " + message.content() +  " 라고 하셨나요?");
    }
}
