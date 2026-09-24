package com.anurag.appointmentbooking.controller;

import com.anurag.appointmentbooking.dto.AvailableSlotResponse;
import com.anurag.appointmentbooking.service.AvailabilityService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/doctors")
public class AvailabilityController {

    private final AvailabilityService availabilityService;

    public AvailabilityController(
            AvailabilityService availabilityService) {
        this.availabilityService = availabilityService;
    }

    @GetMapping("/{doctorId}/available-slots")
    public ResponseEntity<List<AvailableSlotResponse>>
            getAvailableSlots(
                    @PathVariable Long doctorId,
                    @RequestParam Long serviceId,
                    @RequestParam
                    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                    LocalDate date) {

        return ResponseEntity.ok(
                availabilityService.getAvailableSlots(
                        doctorId,
                        serviceId,
                        date));
    }
}