package com.example.chatpoc.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "conversations")
public class Conversation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agency_id", nullable = false)
    private Agency agency;

    @Column(nullable = false)
    private String guestId;

    @Column(nullable = false)
    private Instant createdAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ConversationStatus status;

    protected Conversation() {
    }

    public Conversation(Agency agency, String guestId) {
        this.agency = agency;
        this.guestId = guestId;
        this.createdAt = Instant.now();
        this.status = ConversationStatus.OPEN;
    }

    public Long getId() {
        return id;
    }

    public Agency getAgency() {
        return agency;
    }

    public String getGuestId() {
        return guestId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public ConversationStatus getStatus() {
        return status;
    }

    public void setStatus(ConversationStatus status) {
        this.status = status;
    }
}
