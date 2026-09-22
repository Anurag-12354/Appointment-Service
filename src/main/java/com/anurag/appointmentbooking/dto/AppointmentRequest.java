package com.anurag.appointmentbooking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record AppointmentRequest(

        @NotNull(message = "Doctor ID is required") Long doctorId,

        @NotNull(message = "Service ID is required") Long serviceId,

        @NotNull(message = "Start time is required") @Future(message = "Appointment must be in the future") LocalDateTime startTime,

        @Size(max = 1000, message = "Notes cannot exceed 1000 characters") String notes) {
}
