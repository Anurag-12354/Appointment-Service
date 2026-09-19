package com.anurag.appointmentbooking.dto;

import com.anurag.appointmentbooking.model.Role;

public record LoginResponse(
        Long id,
        String name,
        String email,
        Role role,
        String message) {
}
