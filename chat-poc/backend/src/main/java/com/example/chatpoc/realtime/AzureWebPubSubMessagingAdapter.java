package com.example.chatpoc.realtime;

import com.example.chatpoc.dto.ChatMessageResponse;
import com.azure.messaging.webpubsub.WebPubSubServiceClient;
import com.azure.messaging.webpubsub.WebPubSubServiceClientBuilder;
import com.azure.messaging.webpubsub.models.GetClientAccessTokenOptions;
import com.azure.messaging.webpubsub.models.WebPubSubContentType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.List;

@Component
@ConditionalOnProperty(name = "realtime.provider", havingValue = "azure")
public class AzureWebPubSubMessagingAdapter implements RealtimeMessagingPort {
    private final WebPubSubServiceClient client;
    private final ObjectMapper objectMapper;

    public AzureWebPubSubMessagingAdapter(ObjectMapper objectMapper,
                                          AzureWebPubSubProperties properties) {
        this.objectMapper = objectMapper;
        this.client = new WebPubSubServiceClientBuilder()
                .connectionString(properties.connectionString())
                .hub(properties.hubName())
                .buildClient();
    }

    @Override
    public void publishConversationMessage(Long conversationId, ChatMessageResponse message) {
        publish("conversation-" + conversationId, message);
    }

    @Override
    public void publishConversationEvent(Long agencyId, Map<String, Object> event) {
        publish("agency-" + agencyId, event);
    }

    @Override
    public String createClientAccessUrl(String userId, List<String> groups) {
        GetClientAccessTokenOptions options = new GetClientAccessTokenOptions().setUserId(userId);
        groups.forEach(options::addGroup);
        return client.getClientAccessToken(options).getUrl();
    }

    private void publish(String group, Object payload) {
        try {
            client.sendToGroup(
                    group,
                    objectMapper.writeValueAsString(payload),
                    WebPubSubContentType.APPLICATION_JSON
            );
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Impossible de sérialiser l'événement temps réel", exception);
        }
    }
}
