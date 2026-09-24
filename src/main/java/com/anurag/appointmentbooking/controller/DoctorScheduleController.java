package com.anurag.appointmentbooking.controller;

import com.anurag.appointmentbooking.dto.DoctorScheduleRequest;
import com.anurag.appointmentbooking.dto.DoctorScheduleResponse;
import com.anurag.appointmentbooking.service.DoctorScheduleService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doctors/{doctorId}/schedule")
public class DoctorScheduleController {

    private final DoctorScheduleService doctorScheduleService;

    public DoctorScheduleController(
            DoctorScheduleService doctorScheduleService) {
        this.doctorScheduleService = doctorScheduleService;
    }

    @PutMapping
    public ResponseEntity<DoctorScheduleResponse> setSchedule(
            @PathVariable Long doctorId,
            @Valid @RequestBody DoctorScheduleRequest request) {

        return ResponseEntity.ok(
                doctorScheduleService.setSchedule(doctorId, request));
    }

    @GetMapping
    public ResponseEntity<List<DoctorScheduleResponse>> getSchedule(
            @PathVariable Long doctorId) {

        return ResponseEntity.ok(
                doctorScheduleService.getSchedule(doctorId));
    }
}