package com.example.chatpoc.config;

import com.example.chatpoc.model.Agency;
import com.example.chatpoc.model.ClientAccount;
import com.example.chatpoc.repository.AgencyRepository;
import com.example.chatpoc.repository.ClientAccountRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initAgencies(AgencyRepository agencyRepository) {
        return args -> {
            if (agencyRepository.count() == 0) {
                agencyRepository.save(new Agency("AG-001", "Agence Paris Center"));
                agencyRepository.save(new Agency("AG-002", "Agence Lyon Sud"));
                agencyRepository.save(new Agency("AG-003", "Agence Marseille Port"));
            }
        };
    }

    @Bean
    CommandLineRunner initClients(ClientAccountRepository clientAccountRepository) {
        return args -> {
            if (clientAccountRepository.count() == 0) {
                clientAccountRepository.save(new ClientAccount("client1", "Client 1", "mot de passe client"));
                clientAccountRepository.save(new ClientAccount("client2", "Client 2", "mot de passe client"));
            }
        };
    }
}
