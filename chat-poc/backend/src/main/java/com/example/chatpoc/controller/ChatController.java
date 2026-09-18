package com.example.chatpoc.controller;

import com.example.chatpoc.dto.ChatMessageRequest;
import com.example.chatpoc.dto.ChatMessageResponse;
import com.example.chatpoc.model.Agency;
import com.example.chatpoc.model.ClientAccount;
import com.example.chatpoc.model.Conversation;
import com.example.chatpoc.model.Message;
import com.example.chatpoc.repository.AgencyRepository;
import com.example.chatpoc.repository.ClientAccountRepository;
import com.example.chatpoc.repository.ConversationRepository;
import com.example.chatpoc.service.ChatService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class ChatController {

    private final ChatService chatService;
    private final AgencyRepository agencyRepository;
        private final ClientAccountRepository clientAccountRepository;
    private final ConversationRepository conversationRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public ChatController(ChatService chatService,
                         AgencyRepository agencyRepository,
                         ClientAccountRepository clientAccountRepository,
                         ConversationRepository conversationRepository,
                         SimpMessagingTemplate messagingTemplate) {
        this.chatService = chatService;
        this.agencyRepository = agencyRepository;
        this.clientAccountRepository = clientAccountRepository;
        this.conversationRepository = conversationRepository;
        this.messagingTemplate = messagingTemplate;
    }

    @GetMapping("/agencies")
    public List<Map<String, Object>> getAgencies() {
        return agencyRepository.findAll().stream()
                .map(this::toAgencyMap)
                .collect(Collectors.toList());
    }

        @PostMapping("/auth/login")
        public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> payload) {
                String username = payload.get("username");
                String password = payload.get("password");
                ClientAccount client = clientAccountRepository.findByUsername(username).orElse(null);

                if (client == null || !client.getPassword().equals(password)) {
                        return ResponseEntity.status(401).body(Map.of("error", "Identifiants invalides"));
                }

                return ResponseEntity.ok(Map.of(
                                "id", client.getId(),
                                "username", client.getUsername(),
                                "displayName", client.getDisplayName()
                ));
        }

    @PostMapping("/conversations/start")
    public ResponseEntity<Map<String, Object>> startConversation(@RequestBody Map<String, String> payload) {
        String agencyCode = payload.get("agencyCode");
        String guestId = payload.getOrDefault("guestId", "guest-" + System.currentTimeMillis());

        Conversation conversation = chatService.startConversation(agencyCode, guestId);
        publishConversationEvent("CONVERSATION_UPDATED", conversation);

        return ResponseEntity.ok(Map.<String, Object>of(
                "id", conversation.getId(),
                "agencyCode", conversation.getAgency().getCode(),
                "agencyName", conversation.getAgency().getName(),
                "guestId", conversation.getGuestId(),
                "status", conversation.getStatus().name()
        ));
    }

        @GetMapping("/clients/{guestId}/active-conversation")
        public ResponseEntity<Map<String, Object>> getActiveConversation(@PathVariable String guestId) {
                List<Conversation> activeConversations = conversationRepository.findByGuestIdAndStatusWithAgency(
                                guestId,
                                com.example.chatpoc.model.ConversationStatus.OPEN
                );
                if (activeConversations.isEmpty()) {
                        return ResponseEntity.noContent().build();
                }

                Conversation conversation = activeConversations.get(0);
                return ResponseEntity.ok(Map.<String, Object>of(
                                "id", conversation.getId(),
                                "agencyCode", conversation.getAgency().getCode(),
                                "agencyName", conversation.getAgency().getName(),
                                "guestId", conversation.getGuestId(),
                                "status", conversation.getStatus().name()
                ));
        }

    @GetMapping("/conversations/{conversationId}/messages")
    public List<Map<String, Object>> getMessages(@PathVariable Long conversationId) {
        return chatService.getMessages(conversationId).stream()
                .map(message -> Map.<String, Object>of(
                        "id", message.getId(),
                        "senderType", message.getSenderType().name(),
                        "content", message.getContent(),
                        "createdAt", message.getCreatedAt()
                ))
                .collect(Collectors.toList());
    }

        @PostMapping("/conversations/{conversationId}/close")
        public ResponseEntity<Map<String, Object>> closeConversation(@PathVariable Long conversationId) {
                Message terminationMessage = chatService.closeConversation(conversationId);
                Conversation conversation = terminationMessage.getConversation();
                ChatMessageResponse messageResponse = new ChatMessageResponse(
                                conversation.getId(),
                                terminationMessage.getSenderType().name(),
                                terminationMessage.getContent(),
                                terminationMessage.getCreatedAt(),
                                conversation.getStatus().name()
                );
                messagingTemplate.convertAndSend(
                                "/topic/conversation." + conversation.getId(),
                                messageResponse
                );
                publishConversationEvent("CONVERSATION_CLOSED", conversation);
                return ResponseEntity.ok(Map.<String, Object>of(
                                "id", conversation.getId(),
                                "status", conversation.getStatus().name()
                ));
        }

    @GetMapping("/agencies/{agencyId}/conversations")
    public List<Map<String, Object>> getAgencyConversations(@PathVariable Long agencyId) {
                return conversationRepository.findByAgencyIdWithAgency(agencyId).stream()
                .map(conversation -> Map.<String, Object>of(
                        "id", conversation.getId(),
                        "agencyName", conversation.getAgency().getName(),
                        "guestId", conversation.getGuestId(),
                        "status", conversation.getStatus().name(),
                        "createdAt", conversation.getCreatedAt()
                ))
                .collect(Collectors.toList());
    }

    private void publishConversationEvent(String eventType, Conversation conversation) {
        messagingTemplate.convertAndSend(
                "/topic/agency." + conversation.getAgency().getId() + ".conversations",
                Map.<String, Object>of(
                        "type", eventType,
                        "conversationId", conversation.getId(),
                        "status", conversation.getStatus().name()
                )
        );
    }

    @PostMapping("/messages")
    public ResponseEntity<ChatMessageResponse> sendMessage(@Valid @RequestBody ChatMessageRequest request) {
        ChatMessageResponse response = chatService.sendMessage(request);
        messagingTemplate.convertAndSend(
                "/topic/conversation." + response.getConversationId(),
                response
        );
        return ResponseEntity.ok(response);
    }

    private Map<String, Object> toAgencyMap(Agency agency) {
                return Map.<String, Object>of(
                "id", agency.getId(),
                "code", agency.getCode(),
                "name", agency.getName()
        );
    }
}
