package com.example.chatpoc.repository;

import com.example.chatpoc.model.ClientAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClientAccountRepository extends JpaRepository<ClientAccount, Long> {
    Optional<ClientAccount> findByUsername(String username);
}
