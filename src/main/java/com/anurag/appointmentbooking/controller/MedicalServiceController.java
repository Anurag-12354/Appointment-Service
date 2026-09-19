package com.anurag.appointmentbooking.controller;

import com.anurag.appointmentbooking.dto.MedicalServiceRequest;
import com.anurag.appointmentbooking.dto.MedicalServiceResponse;
import com.anurag.appointmentbooking.service.MedicalServiceService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/services")
public class MedicalServiceController {

    private final MedicalServiceService medicalServiceService;

    public MedicalServiceController(
            MedicalServiceService medicalServiceService) {
        this.medicalServiceService = medicalServiceService;
    }

    @PostMapping
    public ResponseEntity<MedicalServiceResponse> createService(
            @Valid @RequestBody MedicalServiceRequest request) {
        MedicalServiceResponse response = medicalServiceService.createService(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
}
