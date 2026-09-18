package com.example.chatpoc.dto;

import java.time.Instant;

public class ChatMessageResponse {
    private Long conversationId;
    private String senderType;
    private String content;
    private Instant createdAt;
    private String status;

    public ChatMessageResponse(Long conversationId, String senderType, String content, Instant createdAt) {
        this(conversationId, senderType, content, createdAt, null);
    }

    public ChatMessageResponse(Long conversationId, String senderType, String content, Instant createdAt, String status) {
        this.conversationId = conversationId;
        this.senderType = senderType;
        this.content = content;
        this.createdAt = createdAt;
        this.status = status;
    }

    public Long getConversationId() {
        return conversationId;
    }

    public String getSenderType() {
        return senderType;
    }

    public String getContent() {
        return content;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public String getStatus() {
        return status;
    }
}
