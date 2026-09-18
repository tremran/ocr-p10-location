package com.example.chatpoc.realtime;

import com.example.chatpoc.dto.ChatMessageResponse;

import java.util.Map;

public interface RealtimeMessagingPort {
    void publishConversationMessage(Long conversationId, ChatMessageResponse message);

    void publishConversationEvent(Long agencyId, Map<String, Object> event);
}
