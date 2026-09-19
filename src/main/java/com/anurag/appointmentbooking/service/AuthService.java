package com.anurag.appointmentbooking.service;

import com.anurag.appointmentbooking.dto.LoginRequest;
import com.anurag.appointmentbooking.dto.LoginResponse;
import com.anurag.appointmentbooking.exception.ResourceNotFoundException;
import com.anurag.appointmentbooking.model.User;
import com.anurag.appointmentbooking.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class AuthService {

        private final AuthenticationManager authenticationManager;
        private final UserRepository userRepository;

        public AuthService(
                        AuthenticationManager authenticationManager,
                        UserRepository userRepository) {
                this.authenticationManager = authenticationManager;
                this.userRepository = userRepository;
        }

        public LoginResponse login(LoginRequest request) {

                String normalizedEmail = request.email()
                                .trim()
                                .toLowerCase(Locale.ROOT);

                authenticationManager.authenticate(
                                UsernamePasswordAuthenticationToken.unauthenticated(
                                                normalizedEmail,
                                                request.password()));

                User user = userRepository.findByEmail(normalizedEmail)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Authenticated user was not found"));

                return new LoginResponse(
                                user.getId(),
                                user.getName(),
                                user.getEmail(),
                                user.getRole(),
                                "Login successful");
        }
}