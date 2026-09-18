package com.example.chatpoc.realtime;

import com.example.chatpoc.dto.ChatMessageResponse;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@ConditionalOnProperty(name = "realtime.provider", havingValue = "azure")
public class AzureWebPubSubMessagingAdapter implements RealtimeMessagingPort {

    @Override
    public void publishConversationMessage(Long conversationId, ChatMessageResponse message) {
        throw new UnsupportedOperationException(
                "Azure Web PubSub est sélectionné mais son client n'est pas encore configuré"
        );
    }

    @Override
    public void publishConversationEvent(Long agencyId, Map<String, Object> event) {
        throw new UnsupportedOperationException(
                "Azure Web PubSub est sélectionné mais son client n'est pas encore configuré"
        );
    }
}
