package com.example.chatpoc.repository;

import com.example.chatpoc.model.Conversation;
import com.example.chatpoc.model.ConversationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {
    List<Conversation> findByAgencyId(Long agencyId);

    @Query("select conversation from Conversation conversation join fetch conversation.agency where conversation.agency.id = :agencyId")
    List<Conversation> findByAgencyIdWithAgency(@Param("agencyId") Long agencyId);

    List<Conversation> findByAgencyIdAndStatus(Long agencyId, ConversationStatus status);
    Conversation findByAgencyIdAndGuestIdAndStatus(Long agencyId, String guestId, ConversationStatus status);

    @Query("select conversation from Conversation conversation join fetch conversation.agency where conversation.guestId = :guestId and conversation.status = :status order by conversation.createdAt desc")
    List<Conversation> findByGuestIdAndStatusWithAgency(@Param("guestId") String guestId,
                                                         @Param("status") ConversationStatus status);
}
