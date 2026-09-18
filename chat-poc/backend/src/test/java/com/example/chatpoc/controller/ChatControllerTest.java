package com.example.chatpoc.controller;

import com.example.chatpoc.model.Agency;
import com.example.chatpoc.model.ClientAccount;
import com.example.chatpoc.model.Conversation;
import com.example.chatpoc.model.ConversationStatus;
import com.example.chatpoc.model.Message;
import com.example.chatpoc.model.SenderType;
import com.example.chatpoc.repository.AgencyRepository;
import com.example.chatpoc.repository.ClientAccountRepository;
import com.example.chatpoc.repository.ConversationRepository;
import com.example.chatpoc.service.ChatService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import com.example.chatpoc.realtime.RealtimeMessagingPort;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ChatController.class)
class ChatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ChatService chatService;

    @MockBean
    private AgencyRepository agencyRepository;

    @MockBean
    private ClientAccountRepository clientAccountRepository;

    @MockBean
    private ConversationRepository conversationRepository;

    @MockBean
        private RealtimeMessagingPort realtimeMessagingPort;

    @Test
    void authenticatesKnownClient() throws Exception {
                ClientAccount client = new ClientAccount("client1", "Client 1", "mot de passe client");
                setId(client, 1L);
        when(clientAccountRepository.findByUsername("client1")).thenReturn(Optional.of(client));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"client1\",\"password\":\"mot de passe client\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("client1"))
                .andExpect(jsonPath("$.displayName").value("Client 1"));
    }

    @Test
    void rejectsInvalidClientCredentials() throws Exception {
        when(clientAccountRepository.findByUsername("client1")).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"client1\",\"password\":\"wrong\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Identifiants invalides"));
    }

    @Test
    void returnsNoContentWhenClientHasNoOpenConversation() throws Exception {
        when(conversationRepository.findByGuestIdAndStatusWithAgency("client1", ConversationStatus.OPEN))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/clients/client1/active-conversation"))
                .andExpect(status().isNoContent());
    }

    @Test
    void returnsActiveConversationWithAgencyName() throws Exception {
        Agency agency = new Agency("AG-001", "Agence Paris Center");
        setId(agency, 1L);
        Conversation conversation = new Conversation(agency, "client1");
        setId(conversation, 10L);
        when(conversationRepository.findByGuestIdAndStatusWithAgency("client1", ConversationStatus.OPEN))
                .thenReturn(List.of(conversation));

        mockMvc.perform(get("/api/clients/client1/active-conversation"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.agencyCode").value("AG-001"))
                .andExpect(jsonPath("$.agencyName").value("Agence Paris Center"))
                .andExpect(jsonPath("$.status").value("OPEN"));
    }

    @Test
    void closesConversationPublishesTerminationMessageAndArchiveEvent() throws Exception {
        Agency agency = new Agency("AG-001", "Agence Paris Center");
        setId(agency, 1L);
        Conversation conversation = new Conversation(agency, "client1");
        setId(conversation, 10L);
        conversation.setStatus(ConversationStatus.CLOSED);
        Message terminationMessage = new Message(
                conversation,
                SenderType.AGENCY,
                "conversation terminée, pour continuer à échanger, merci de lancer un nouveau chat"
        );
        when(chatService.closeConversation(10L)).thenReturn(terminationMessage);

        mockMvc.perform(post("/api/conversations/10/close"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CLOSED"));

        verify(realtimeMessagingPort).publishConversationMessage(eq(10L), any());
        verify(realtimeMessagingPort).publishConversationEvent(eq(1L), any());
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
}
