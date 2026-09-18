package com.example.chatpoc.service;

import com.example.chatpoc.dto.ChatMessageRequest;
import com.example.chatpoc.model.Agency;
import com.example.chatpoc.model.Conversation;
import com.example.chatpoc.model.ConversationStatus;
import com.example.chatpoc.model.Message;
import com.example.chatpoc.model.SenderType;
import com.example.chatpoc.repository.AgencyRepository;
import com.example.chatpoc.repository.ConversationRepository;
import com.example.chatpoc.repository.MessageRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

    @Mock
    private AgencyRepository agencyRepository;

    @Mock
    private ConversationRepository conversationRepository;

    @Mock
    private MessageRepository messageRepository;

    @InjectMocks
    private ChatService chatService;

    @Test
    void reusesOpenConversationForSameClientAndAgency() {
        Agency agency = agency(1L, "AG-001", "Agence Paris Center");
        Conversation existing = new Conversation(agency, "client1");
        when(agencyRepository.findByCode("AG-001")).thenReturn(agency);
        when(conversationRepository.findByAgencyIdAndGuestIdAndStatus(1L, "client1", ConversationStatus.OPEN))
                .thenReturn(existing);

        Conversation result = chatService.startConversation("AG-001", "client1");

        assertThat(result).isSameAs(existing);
        verify(conversationRepository, never()).save(any(Conversation.class));
    }

    @Test
    void createsNewConversationWhenPreviousConversationIsClosed() {
        Agency agency = agency(1L, "AG-001", "Agence Paris Center");
        Conversation fresh = new Conversation(agency, "client1");
        when(agencyRepository.findByCode("AG-001")).thenReturn(agency);
        when(conversationRepository.findByAgencyIdAndGuestIdAndStatus(1L, "client1", ConversationStatus.OPEN))
                .thenReturn(null);
        when(conversationRepository.save(any(Conversation.class))).thenReturn(fresh);

        Conversation result = chatService.startConversation("AG-001", "client1");

        assertThat(result).isSameAs(fresh);
        assertThat(result.getStatus()).isEqualTo(ConversationStatus.OPEN);
        verify(conversationRepository).save(any(Conversation.class));
    }

    @Test
    void rejectsBlankMessage() {
        Conversation conversation = conversation(10L, "client1", ConversationStatus.OPEN);
        when(conversationRepository.findById(10L)).thenReturn(Optional.of(conversation));

        ChatMessageRequest request = messageRequest("10", "CUSTOMER", "client1", "   ");

        assertThatThrownBy(() -> chatService.sendMessage(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Le message ne peut pas être vide");
        verifyNoInteractions(messageRepository);
    }

    @Test
    void rejectsCustomerAccessToAnotherConversation() {
        Conversation conversation = conversation(10L, "client1", ConversationStatus.OPEN);
        when(conversationRepository.findById(10L)).thenReturn(Optional.of(conversation));

        ChatMessageRequest request = messageRequest("10", "CUSTOMER", "client2", "Bonjour");

        assertThatThrownBy(() -> chatService.sendMessage(request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Accès non autorisé à la conversation");
        verifyNoInteractions(messageRepository);
    }

    @Test
    void rejectsMessageOnClosedConversation() {
        Conversation conversation = conversation(10L, "client1", ConversationStatus.CLOSED);
        when(conversationRepository.findById(10L)).thenReturn(Optional.of(conversation));

        ChatMessageRequest request = messageRequest("10", "CUSTOMER", "client1", "Bonjour");

        assertThatThrownBy(() -> chatService.sendMessage(request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("La conversation est fermée");
        verifyNoInteractions(messageRepository);
    }

    @Test
    void closesConversationAndPersistsTerminationMessage() {
        Conversation conversation = conversation(10L, "client1", ConversationStatus.OPEN);
        Message terminationMessage = new Message(
                conversation,
                SenderType.AGENCY,
                "conversation terminée, pour continuer à échanger, merci de lancer un nouveau chat"
        );
        when(conversationRepository.findById(10L)).thenReturn(Optional.of(conversation));
        when(messageRepository.save(any(Message.class))).thenReturn(terminationMessage);

        Message result = chatService.closeConversation(10L);

        assertThat(conversation.getStatus()).isEqualTo(ConversationStatus.CLOSED);
        assertThat(result.getContent()).isEqualTo("conversation terminée, pour continuer à échanger, merci de lancer un nouveau chat");
        verify(conversationRepository).save(conversation);
        verify(messageRepository).save(any(Message.class));
    }

    private static Agency agency(Long id, String code, String name) {
        Agency agency = new Agency(code, name);
        setId(agency, id);
        return agency;
    }

    private static Conversation conversation(Long id, String guestId, ConversationStatus status) {
        Conversation conversation = new Conversation(agency(1L, "AG-001", "Agence Paris Center"), guestId);
        setId(conversation, id);
        conversation.setStatus(status);
        return conversation;
    }

    private static void setId(Object entity, Long id) {
        try {
            var field = entity.getClass().getDeclaredField("id");
            field.setAccessible(true);
            field.set(entity, id);
        } catch (ReflectiveOperationException exception) {
            throw new AssertionError(exception);
        }
    }

    private static ChatMessageRequest messageRequest(String conversationId, String senderType,
                                                      String guestId, String content) {
        ChatMessageRequest request = new ChatMessageRequest();
        request.setConversationId(conversationId);
        request.setSenderType(senderType);
        request.setGuestId(guestId);
        request.setContent(content);
        return request;
    }
}
