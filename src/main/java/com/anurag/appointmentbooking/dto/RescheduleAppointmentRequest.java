package com.anurag.appointmentbooking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record RescheduleAppointmentRequest(

        @NotNull(message = "New start time is required")
        @Future(message = "New start time must be in the future")
        LocalDateTime startTime) {
}