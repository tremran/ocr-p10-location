package com.example.chatpoc.dto;

import java.time.Instant;

public class ConversationResponse {
    private Long id;
    private String agencyCode;
    private String status;
    private String guestId;
    private Instant createdAt;

    public ConversationResponse(Long id, String agencyCode, String status, String guestId, Instant createdAt) {
        this.id = id;
        this.agencyCode = agencyCode;
        this.status = status;
        this.guestId = guestId;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public String getAgencyCode() {
        return agencyCode;
    }

    public String getStatus() {
        return status;
    }

    public String getGuestId() {
        return guestId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
