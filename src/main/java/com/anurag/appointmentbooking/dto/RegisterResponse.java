package com.anurag.appointmentbooking.dto;

import com.anurag.appointmentbooking.model.Role;

import java.time.LocalDateTime;

public record RegisterResponse(
        Long id,
        String name,
        String email,
        Role role,
        LocalDateTime createdAt) {
}