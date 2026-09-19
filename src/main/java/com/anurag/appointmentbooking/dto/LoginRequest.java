package com.anurag.appointmentbooking.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
                @NotBlank(message = "Email is Required") @Email(message = "Enter a valid Email") String email,

                @NotBlank(message = "password is required") String password) {
}
