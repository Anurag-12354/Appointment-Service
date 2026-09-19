package com.anurag.appointmentbooking.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record MedicalServiceRequest(

        @NotBlank(message = "Service name is required") @Size(min = 2, max = 100) String name,

        @Size(max = 500) String description,

        @NotNull(message = "Duration is required") @Min(value = 5, message = "Duration must be at least 5 minutes") @Max(value = 480, message = "Duration cannot exceed 480 minutes") Integer durationMinutes,

        @NotNull(message = "Price is required") @DecimalMin(value = "0.00", message = "Price cannot be negative") @Digits(integer = 8, fraction = 2) BigDecimal price) {
}