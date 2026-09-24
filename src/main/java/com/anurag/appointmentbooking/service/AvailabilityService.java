package com.anurag.appointmentbooking.service;

import com.anurag.appointmentbooking.dto.AvailableSlotResponse;
import com.anurag.appointmentbooking.exception.ResourceNotFoundException;
import com.anurag.appointmentbooking.model.Appointment;
import com.anurag.appointmentbooking.model.AppointmentStatus;
import com.anurag.appointmentbooking.model.Doctor;
import com.anurag.appointmentbooking.model.DoctorSchedule;
import com.anurag.appointmentbooking.model.MedicalService;
import com.anurag.appointmentbooking.repository.AppointmentRepository;
import com.anurag.appointmentbooking.repository.DoctorRepository;
import com.anurag.appointmentbooking.repository.DoctorScheduleRepository;
import com.anurag.appointmentbooking.repository.MedicalServiceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class AvailabilityService {

    private final DoctorRepository doctorRepository;
    private final MedicalServiceRepository medicalServiceRepository;
    private final DoctorScheduleRepository doctorScheduleRepository;
    private final AppointmentRepository appointmentRepository;

    public AvailabilityService(
            DoctorRepository doctorRepository,
            MedicalServiceRepository medicalServiceRepository,
            DoctorScheduleRepository doctorScheduleRepository,
            AppointmentRepository appointmentRepository) {
        this.doctorRepository = doctorRepository;
        this.medicalServiceRepository = medicalServiceRepository;
        this.doctorScheduleRepository = doctorScheduleRepository;
        this.appointmentRepository = appointmentRepository;
    }

    @Transactional(readOnly = true)
    public List<AvailableSlotResponse> getAvailableSlots(
            Long doctorId,
            Long serviceId,
            LocalDate date) {

        if (date.isBefore(LocalDate.now())) {
            throw new IllegalStateException(
                    "Availability cannot be checked for a past date");
        }

        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Doctor not found with ID: " + doctorId));

        MedicalService medicalService = medicalServiceRepository
                .findById(serviceId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Medical service not found with ID: " + serviceId));

        validateAvailabilityRequest(
                doctor,
                medicalService,
                serviceId);

        DoctorSchedule schedule = doctorScheduleRepository
                .findByDoctorIdAndDayOfWeek(
                        doctorId,
                        date.getDayOfWeek())
                .orElse(null);

        if (schedule == null) {
            return List.of();
        }

        LocalDateTime workingStart =
                date.atTime(schedule.getStartTime());

        LocalDateTime workingEnd =
                date.atTime(schedule.getEndTime());

        List<Appointment> bookedAppointments =
                appointmentRepository.findDoctorAppointmentsForPeriod(
                        doctorId,
                        workingStart,
                        workingEnd,
                        AppointmentStatus.CANCELLED);

        return generateAvailableSlots(
                workingStart,
                workingEnd,
                medicalService.getDurationMinutes(),
                bookedAppointments);
    }

    private void validateAvailabilityRequest(
            Doctor doctor,
            MedicalService medicalService,
            Long serviceId) {

        if (!doctor.isActive()) {
            throw new IllegalStateException(
                    "The selected doctor is inactive");
        }

        if (!medicalService.isActive()) {
            throw new IllegalStateException(
                    "The selected medical service is inactive");
        }

        boolean providesService = doctor.getServices()
                .stream()
                .anyMatch(service ->
                        service.getId().equals(serviceId));

        if (!providesService) {
            throw new IllegalStateException(
                    "The selected doctor does not provide this service");
        }
    }

    private List<AvailableSlotResponse> generateAvailableSlots(
            LocalDateTime workingStart,
            LocalDateTime workingEnd,
            int durationMinutes,
            List<Appointment> bookedAppointments) {

        List<AvailableSlotResponse> availableSlots =
                new ArrayList<>();

        LocalDateTime currentStart = workingStart;
        LocalDateTime now = LocalDateTime.now();

        while (!currentStart
                .plusMinutes(durationMinutes)
                .isAfter(workingEnd)) {

            LocalDateTime slotStart = currentStart;
            LocalDateTime slotEnd =
                    slotStart.plusMinutes(durationMinutes);

            boolean overlaps = bookedAppointments
                    .stream()
                    .anyMatch(appointment ->
                            slotStart.isBefore(appointment.getEndTime())
                                    && slotEnd.isAfter(
                                            appointment.getStartTime()));

            if (!overlaps && slotStart.isAfter(now)) {
                availableSlots.add(
                        new AvailableSlotResponse(
                                slotStart,
                                slotEnd));
            }

            currentStart = currentStart
                    .plusMinutes(durationMinutes);
        }

        return availableSlots;
    }
}