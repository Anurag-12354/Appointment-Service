package com.anurag.appointmentbooking.dto;

public record DoctorResponse(
        Long id,
        String name,
        String specialization,
        String email,
        boolean active) {
}
