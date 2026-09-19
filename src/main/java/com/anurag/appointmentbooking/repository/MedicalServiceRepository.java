package com.anurag.appointmentbooking.repository;

import com.anurag.appointmentbooking.model.MedicalService;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MedicalServiceRepository
        extends JpaRepository<MedicalService, Long> {

    List<MedicalService> findByActiveTrue();

    Optional<MedicalService> findByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCase(String name);
}