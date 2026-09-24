package com.anurag.appointmentbooking.dto;

import java.time.LocalDateTime;

public record AvailableSlotResponse(
                LocalDateTime startTime,
                LocalDateTime endTime) {
}