package com.anurag.appointmentbooking.service;

import com.anurag.appointmentbooking.dto.MedicalServiceRequest;
import com.anurag.appointmentbooking.dto.MedicalServiceResponse;
import com.anurag.appointmentbooking.exception.DuplicateResourceException;
import com.anurag.appointmentbooking.model.MedicalService;
import com.anurag.appointmentbooking.repository.MedicalServiceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MedicalServiceService {

    private final MedicalServiceRepository medicalServiceRepository;

    public MedicalServiceService(
            MedicalServiceRepository medicalServiceRepository) {
        this.medicalServiceRepository = medicalServiceRepository;
    }

    @Transactional
    public MedicalServiceResponse createService(
            MedicalServiceRequest request) {
        String normalizedName = request.name().trim();

        if (medicalServiceRepository
                .existsByNameIgnoreCase(normalizedName)) {
            throw new DuplicateResourceException(
                    "A medical service with this name already exists");
        }

        String description = request.description() == null
                ? null
                : request.description().trim();

        MedicalService medicalService = new MedicalService(
                normalizedName,
                description,
                request.durationMinutes(),
                request.price());

        MedicalService savedService = medicalServiceRepository.save(medicalService);

        return mapToResponse(savedService);
    }

    private MedicalServiceResponse mapToResponse(
            MedicalService medicalService) {
        return new MedicalServiceResponse(
                medicalService.getId(),
                medicalService.getName(),
                medicalService.getDescription(),
                medicalService.getDurationMinutes(),
                medicalService.getPrice(),
                medicalService.isActive());
    }
}