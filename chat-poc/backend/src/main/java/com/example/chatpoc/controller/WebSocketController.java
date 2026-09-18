package com.example.chatpoc.controller;

import com.example.chatpoc.dto.ChatMessageRequest;
import com.example.chatpoc.service.ChatService;
import com.example.chatpoc.realtime.RealtimeMessagingPort;
import jakarta.validation.Valid;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {

    private final ChatService chatService;
    private final RealtimeMessagingPort realtimeMessagingPort;

    public WebSocketController(ChatService chatService, RealtimeMessagingPort realtimeMessagingPort) {
        this.chatService = chatService;
        this.realtimeMessagingPort = realtimeMessagingPort;
    }

    @MessageMapping("/chat.send")
    public void sendMessage(@Valid @Payload ChatMessageRequest request) {
        var response = chatService.sendMessage(request);
        realtimeMessagingPort.publishConversationMessage(response.getConversationId(), response);
    }
}
