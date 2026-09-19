package com.anurag.appointmentbooking.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record DoctorRequest(

        @NotBlank(message = "Doctor name is required") @Size(min = 2, max = 100, message = "Name must contain 2–100 characters") String name,

        @NotBlank(message = "Specialization is required") @Size(max = 100, message = "Specialization cannot exceed 100 characters") String specialization,

        @NotBlank(message = "Email is required") @Email(message = "Enter a valid email address") String email) {
}