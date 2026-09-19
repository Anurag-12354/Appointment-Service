package com.anurag.appointmentbooking.dto;

import java.math.BigDecimal;

public record MedicalServiceResponse(
        Long id,
        String name,
        String description,
        Integer durationMinutes,
        BigDecimal price,
        boolean active) {
}