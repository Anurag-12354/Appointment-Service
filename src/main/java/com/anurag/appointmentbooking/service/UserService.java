package com.anurag.appointmentbooking.service;

import com.anurag.appointmentbooking.dto.RegisterRequest;
import com.anurag.appointmentbooking.dto.RegisterResponse;
import com.anurag.appointmentbooking.exception.DuplicateResourceException;
import com.anurag.appointmentbooking.model.Role;
import com.anurag.appointmentbooking.model.User;
import com.anurag.appointmentbooking.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public RegisterResponse register(RegisterRequest request) {

        String normalizedEmail = request.email()
                .trim()
                .toLowerCase(Locale.ROOT);

        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new DuplicateResourceException(
                    "An account with this email already exists");
        }

        String passwordHash = passwordEncoder.encode(request.password());

        User user = new User(
                request.name().trim(),
                normalizedEmail,
                passwordHash,
                Role.USER);

        User savedUser = userRepository.save(user);

        return new RegisterResponse(
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getEmail(),
                savedUser.getRole(),
                savedUser.getCreatedAt());
    }
}