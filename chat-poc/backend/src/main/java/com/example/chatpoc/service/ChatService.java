package com.example.chatpoc.service;

import com.example.chatpoc.dto.ChatMessageRequest;
import com.example.chatpoc.dto.ChatMessageResponse;
import com.example.chatpoc.model.*;
import com.example.chatpoc.repository.AgencyRepository;
import com.example.chatpoc.repository.ConversationRepository;
import com.example.chatpoc.repository.MessageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ChatService {

    private final AgencyRepository agencyRepository;
    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;

    public ChatService(AgencyRepository agencyRepository,
                       ConversationRepository conversationRepository,
                       MessageRepository messageRepository) {
        this.agencyRepository = agencyRepository;
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
    }

    @Transactional
    public Conversation startConversation(String agencyCode, String guestId) {
        Agency agency = agencyRepository.findByCode(agencyCode);
        if (agency == null) {
            throw new IllegalArgumentException("Agence inconnue: " + agencyCode);
        }

        Conversation existing = conversationRepository.findByAgencyIdAndGuestIdAndStatus(
            agency.getId(), guestId, ConversationStatus.OPEN
        );
        if (existing != null) {
            return existing;
        }

        return conversationRepository.save(new Conversation(agency, guestId));
    }

    @Transactional(readOnly = true)
    public List<Conversation> getConversationsForAgency(Long agencyId) {
        return conversationRepository.findByAgencyId(agencyId);
    }

    @Transactional(readOnly = true)
    public List<com.example.chatpoc.model.Message> getMessages(Long conversationId) {
        return messageRepository.findByConversationIdOrderByCreatedAtAsc(conversationId);
    }

    @Transactional
    public Message closeConversation(Long conversationId) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new IllegalArgumentException("Conversation inconnue: " + conversationId));
        conversation.setStatus(ConversationStatus.CLOSED);
        conversationRepository.save(conversation);
        return messageRepository.save(new Message(
                conversation,
                SenderType.AGENCY,
                "conversation terminée, pour continuer à échanger, merci de lancer un nouveau chat"
        ));
    }

    @Transactional
    public ChatMessageResponse sendMessage(ChatMessageRequest request) {
        Long conversationId = Long.parseLong(request.getConversationId());
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new IllegalArgumentException("Conversation inconnue: " + conversationId));

        SenderType senderType = SenderType.valueOf(request.getSenderType());
        String content = request.getContent() == null ? "" : request.getContent().trim();
        if (content.isBlank()) {
            throw new IllegalArgumentException("Le message ne peut pas être vide");
        }

        if (conversation.getStatus() == ConversationStatus.CLOSED) {
            throw new IllegalStateException("La conversation est fermée");
        }

        if (senderType == SenderType.CUSTOMER && !conversation.getGuestId().equals(request.getGuestId())) {
            throw new IllegalStateException("Accès non autorisé à la conversation");
        }

        Message message = new Message(conversation, senderType, content);
        messageRepository.save(message);

        return new ChatMessageResponse(
                conversation.getId(),
                senderType.name(),
                message.getContent(),
                message.getCreatedAt()
        );
    }
}
