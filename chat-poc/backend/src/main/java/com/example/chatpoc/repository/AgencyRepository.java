package com.example.chatpoc.repository;

import com.example.chatpoc.model.Agency;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AgencyRepository extends JpaRepository<Agency, Long> {
    Agency findByCode(String code);
}
