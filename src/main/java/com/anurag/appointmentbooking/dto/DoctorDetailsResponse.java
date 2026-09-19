package com.anurag.appointmentbooking.dto;

import java.util.List;

public record DoctorDetailsResponse(
        Long id,
        String name,
        String specialization,
        String email,
        boolean active,
        List<MedicalServiceResponse> services) {
}