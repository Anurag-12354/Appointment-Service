package com.anurag.appointmentbooking.service;

import com.anurag.appointmentbooking.dto.AppointmentRequest;
import com.anurag.appointmentbooking.dto.AppointmentResponse;
import com.anurag.appointmentbooking.exception.ResourceNotFoundException;
import com.anurag.appointmentbooking.model.Appointment;
import com.anurag.appointmentbooking.model.AppointmentStatus;
import com.anurag.appointmentbooking.model.Doctor;
import com.anurag.appointmentbooking.model.MedicalService;
import com.anurag.appointmentbooking.model.User;
import com.anurag.appointmentbooking.repository.AppointmentRepository;
import com.anurag.appointmentbooking.repository.DoctorRepository;
import com.anurag.appointmentbooking.repository.MedicalServiceRepository;
import com.anurag.appointmentbooking.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

@Service
public class AppointmentService {

        private final AppointmentRepository appointmentRepository;
        private final UserRepository userRepository;
        private final DoctorRepository doctorRepository;
        private final MedicalServiceRepository medicalServiceRepository;

        public AppointmentService(
                        AppointmentRepository appointmentRepository,
                        UserRepository userRepository,
                        DoctorRepository doctorRepository,
                        MedicalServiceRepository medicalServiceRepository) {
                this.appointmentRepository = appointmentRepository;
                this.userRepository = userRepository;
                this.doctorRepository = doctorRepository;
                this.medicalServiceRepository = medicalServiceRepository;
        }

        @Transactional
        public AppointmentResponse createAppointment(
                        AppointmentRequest request,
                        String authenticatedEmail) {
                String normalizedEmail = authenticatedEmail
                                .trim()
                                .toLowerCase(Locale.ROOT);

                User user = userRepository
                                .findByEmailForUpdate(normalizedEmail)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Authenticated user was not found"));

                Doctor doctor = doctorRepository
                                .findByIdForUpdate(request.doctorId())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Doctor not found with ID: "
                                                                + request.doctorId()));

                MedicalService medicalService = medicalServiceRepository
                                .findById(request.serviceId())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Medical service not found with ID: "
                                                                + request.serviceId()));

                validateBooking(
                                doctor,
                                medicalService,
                                request.serviceId());

                LocalDateTime endTime = request.startTime()
                                .plusMinutes(
                                                medicalService.getDurationMinutes());

                checkAppointmentConflicts(
                                user,
                                doctor,
                                request.startTime(),
                                endTime);

                String notes = request.notes() == null
                                ? null
                                : request.notes().trim();

                Appointment appointment = new Appointment(
                                user,
                                doctor,
                                medicalService,
                                request.startTime(),
                                endTime,
                                notes);

                Appointment savedAppointment = appointmentRepository.save(appointment);

                return mapToResponse(savedAppointment);
        }

        private void validateBooking(
                        Doctor doctor,
                        MedicalService medicalService,
                        Long serviceId) {
                if (!doctor.isActive()) {
                        throw new IllegalStateException(
                                        "Appointments cannot be booked with an inactive doctor");
                }

                if (!medicalService.isActive()) {
                        throw new IllegalStateException(
                                        "The selected medical service is inactive");
                }

                boolean doctorProvidesService = doctor.getServices()
                                .stream()
                                .anyMatch(service -> service.getId().equals(serviceId));

                if (!doctorProvidesService) {
                        throw new IllegalStateException(
                                        "The selected doctor does not provide this service");
                }
        }

        private void checkAppointmentConflicts(
                        User user,
                        Doctor doctor,
                        LocalDateTime startTime,
                        LocalDateTime endTime) {
                long doctorConflicts = appointmentRepository
                                .countOverlappingAppointments(
                                                doctor.getId(),
                                                startTime,
                                                endTime,
                                                AppointmentStatus.CANCELLED);

                if (doctorConflicts > 0) {
                        throw new IllegalStateException(
                                        "The doctor already has an appointment during this time");
                }

                long userConflicts = appointmentRepository
                                .countOverlappingUserAppointments(
                                                user.getId(),
                                                startTime,
                                                endTime,
                                                AppointmentStatus.CANCELLED);

                if (userConflicts > 0) {
                        throw new IllegalStateException(
                                        "You already have an appointment during this time");
                }
        }

        private AppointmentResponse mapToResponse(
                        Appointment appointment) {
                return new AppointmentResponse(
                                appointment.getId(),
                                appointment.getUser().getId(),
                                appointment.getDoctor().getId(),
                                appointment.getDoctor().getName(),
                                appointment.getMedicalService().getId(),
                                appointment.getMedicalService().getName(),
                                appointment.getStartTime(),
                                appointment.getEndTime(),
                                appointment.getStatus(),
                                appointment.getNotes());
        }

        @Transactional(readOnly = true)
        public List<AppointmentResponse> getMyAppointments(
                        String authenticatedEmail) {

                String normalizedEmail = authenticatedEmail
                                .trim()
                                .toLowerCase(Locale.ROOT);

                User user = userRepository
                                .findByEmail(normalizedEmail)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Authenticated user was not found"));

                return appointmentRepository
                                .findAllByUserIdOrderByStartTimeDesc(user.getId())
                                .stream()
                                .map(this::mapToResponse)
                                .toList();
        }

        @Transactional(readOnly = true)
        public List<AppointmentResponse> getAllAppointments() {

                return appointmentRepository
                                .findAllByOrderByStartTimeDesc()
                                .stream()
                                .map(this::mapToResponse)
                                .toList();
        }
}