package com.anurag.appointmentbooking.controller;

import com.anurag.appointmentbooking.dto.AppointmentRequest;
import com.anurag.appointmentbooking.dto.AppointmentResponse;
import com.anurag.appointmentbooking.dto.RescheduleAppointmentRequest;
import com.anurag.appointmentbooking.service.AppointmentService;
import jakarta.validation.Valid;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(
            AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @PostMapping
    public ResponseEntity<AppointmentResponse> createAppointment(
            @Valid @RequestBody AppointmentRequest request,
            Authentication authentication) {
        AppointmentResponse response = appointmentService.createAppointment(
                request,
                authentication.getName());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/my")
    public ResponseEntity<List<AppointmentResponse>> getMyAppointments(
            Authentication authentication) {

        List<AppointmentResponse> appointments = appointmentService.getMyAppointments(
                authentication.getName());

        return ResponseEntity.ok(appointments);
    }

    @GetMapping
    public ResponseEntity<List<AppointmentResponse>> getAllAppointments() {

        List<AppointmentResponse> appointments = appointmentService.getAllAppointments();

        return ResponseEntity.ok(appointments);
    }
    @PatchMapping("/{appointmentId}/cancel")
public ResponseEntity<AppointmentResponse> cancelAppointment(
        @PathVariable Long appointmentId,
        Authentication authentication) {

    AppointmentResponse response =
            appointmentService.cancelAppointment(
                    appointmentId,
                    authentication.getName());

    return ResponseEntity.ok(response);
}
@PatchMapping("/{appointmentId}/reschedule")
public ResponseEntity<AppointmentResponse> rescheduleAppointment(
        @PathVariable Long appointmentId,
        @Valid @RequestBody RescheduleAppointmentRequest request,
        Authentication authentication) {

    AppointmentResponse response =
            appointmentService.rescheduleAppointment(
                    appointmentId,
                    request,
                    authentication.getName());

    return ResponseEntity.ok(response);
}
}