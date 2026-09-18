package com.example.chatpoc.realtime;

import com.example.chatpoc.dto.ChatMessageResponse;

import java.util.Map;
import java.util.List;

public interface RealtimeMessagingPort {
    void publishConversationMessage(Long conversationId, ChatMessageResponse message);

    void publishConversationEvent(Long agencyId, Map<String, Object> event);

    String createClientAccessUrl(String userId, List<String> groups);
}
