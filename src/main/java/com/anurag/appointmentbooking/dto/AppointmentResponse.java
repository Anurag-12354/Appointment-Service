package com.anurag.appointmentbooking.dto;

import com.anurag.appointmentbooking.model.AppointmentStatus;

import java.time.LocalDateTime;

public record AppointmentResponse(
        Long id,
        Long userId,
        Long doctorId,
        String doctorName,
        Long serviceId,
        String serviceName,
        LocalDateTime startTime,
        LocalDateTime endTime,
        AppointmentStatus status,
        String notes) {
}