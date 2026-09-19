package com.anurag.appointmentbooking.controller;

import com.anurag.appointmentbooking.dto.DoctorDetailsResponse;
import com.anurag.appointmentbooking.dto.DoctorRequest;
import com.anurag.appointmentbooking.dto.DoctorResponse;
import com.anurag.appointmentbooking.service.DoctorService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/doctors")
public class DoctorController {

    private final DoctorService doctorService;

    public DoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    @PostMapping
    public ResponseEntity<DoctorResponse> createDoctor(
            @Valid @RequestBody DoctorRequest request) {
        DoctorResponse response = doctorService.createDoctor(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PutMapping("/{doctorId}/services/{serviceId}")
    public ResponseEntity<DoctorDetailsResponse> assignService(
            @PathVariable Long doctorId,
            @PathVariable Long serviceId) {
        DoctorDetailsResponse response = doctorService.assignService(doctorId, serviceId);

        return ResponseEntity.ok(response);
    }
}