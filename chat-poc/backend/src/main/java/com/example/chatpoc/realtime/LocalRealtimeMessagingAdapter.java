package com.example.chatpoc.realtime;

import com.example.chatpoc.dto.ChatMessageResponse;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@ConditionalOnProperty(name = "realtime.provider", havingValue = "local", matchIfMissing = true)
public class LocalRealtimeMessagingAdapter implements RealtimeMessagingPort {

    private final SimpMessagingTemplate messagingTemplate;

    public LocalRealtimeMessagingAdapter(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @Override
    public void publishConversationMessage(Long conversationId, ChatMessageResponse message) {
        messagingTemplate.convertAndSend("/topic/conversation." + conversationId, message);
    }

    @Override
    public void publishConversationEvent(Long agencyId, Map<String, Object> event) {
        messagingTemplate.convertAndSend("/topic/agency." + agencyId + ".conversations", event);
    }
}
