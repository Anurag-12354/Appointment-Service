package com.anurag.appointmentbooking.service;

import com.anurag.appointmentbooking.dto.AppointmentRequest;
import com.anurag.appointmentbooking.dto.AppointmentResponse;
import com.anurag.appointmentbooking.dto.RescheduleAppointmentRequest;
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
import com.anurag.appointmentbooking.model.DoctorSchedule;
import com.anurag.appointmentbooking.repository.DoctorScheduleRepository;
import org.springframework.security.access.AccessDeniedException;

import java.time.Duration;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
        private final DoctorScheduleRepository doctorScheduleRepository;

        public AppointmentService(
                        AppointmentRepository appointmentRepository,
                        UserRepository userRepository,
                        DoctorRepository doctorRepository,
                        MedicalServiceRepository medicalServiceRepository,
                 DoctorScheduleRepository doctorScheduleRepository) {
                this.appointmentRepository = appointmentRepository;
                this.userRepository = userRepository;
                this.doctorRepository = doctorRepository;
                this.medicalServiceRepository = medicalServiceRepository;
                this.doctorScheduleRepository= doctorScheduleRepository;
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
                validateAppointmentTime(
        doctor,
        request.startTime(),
        endTime,
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
        private void validateAppointmentTime(
        Doctor doctor,
        LocalDateTime startTime,
        LocalDateTime endTime,
        int durationMinutes) {

    DoctorSchedule schedule = doctorScheduleRepository
            .findByDoctorIdAndDayOfWeek(
                    doctor.getId(),
                    startTime.getDayOfWeek())
            .orElseThrow(() -> new IllegalStateException(
                    "The doctor is not working on the selected day"));

    if (!startTime.toLocalDate()
            .equals(endTime.toLocalDate())) {
        throw new IllegalStateException(
                "The appointment must start and end on the same day");
    }

    boolean startsBeforeOpening = startTime.toLocalTime()
            .isBefore(schedule.getStartTime());

    boolean endsAfterClosing = endTime.toLocalTime()
            .isAfter(schedule.getEndTime());

    if (startsBeforeOpening || endsAfterClosing) {
        throw new IllegalStateException(
                "The appointment is outside the doctor's working hours");
    }

    long minutesFromOpening = Duration.between(
            schedule.getStartTime(),
            startTime.toLocalTime())
            .toMinutes();

    if (minutesFromOpening % durationMinutes != 0) {
        throw new IllegalStateException(
                "The selected start time is not a valid appointment slot");
    }
}
@Transactional
public AppointmentResponse cancelAppointment(
        Long appointmentId,
        String authenticatedEmail) {

    String normalizedEmail = authenticatedEmail
            .trim()
            .toLowerCase(Locale.ROOT);

    Appointment appointment = appointmentRepository
            .findByIdForUpdate(appointmentId)
            .orElseThrow(() -> new ResourceNotFoundException(
                    "Appointment not found with ID: "
                            + appointmentId));

    if (!appointment.getUser()
            .getEmail()
            .equals(normalizedEmail)) {
        throw new AccessDeniedException(
                "You cannot cancel another user's appointment");
    }

    if (appointment.getStatus()
            == AppointmentStatus.CANCELLED) {
        throw new IllegalStateException(
                "The appointment is already cancelled");
    }

    if (appointment.getStatus()
            == AppointmentStatus.COMPLETED) {
        throw new IllegalStateException(
                "A completed appointment cannot be cancelled");
    }

    if (!appointment.getStartTime()
            .isAfter(LocalDateTime.now())) {
        throw new IllegalStateException(
                "A past or started appointment cannot be cancelled");
    }

    appointment.setStatus(AppointmentStatus.CANCELLED);

    Appointment savedAppointment =
            appointmentRepository.save(appointment);

    return mapToResponse(savedAppointment);
}
@Transactional
public AppointmentResponse rescheduleAppointment(
        Long appointmentId,
        RescheduleAppointmentRequest request,
        String authenticatedEmail) {

    String normalizedEmail = authenticatedEmail
            .trim()
            .toLowerCase(Locale.ROOT);

    User user = userRepository
            .findByEmailForUpdate(normalizedEmail)
            .orElseThrow(() -> new ResourceNotFoundException(
                    "Authenticated user was not found"));

    Appointment appointment = appointmentRepository
            .findByIdForUpdate(appointmentId)
            .orElseThrow(() -> new ResourceNotFoundException(
                    "Appointment not found with ID: "
                            + appointmentId));

    if (!appointment.getUser().getId().equals(user.getId())) {
        throw new AccessDeniedException(
                "You cannot reschedule another user's appointment");
    }

    if (appointment.getStatus()
            == AppointmentStatus.CANCELLED) {
        throw new IllegalStateException(
                "A cancelled appointment cannot be rescheduled");
    }

    if (appointment.getStatus()
            == AppointmentStatus.COMPLETED) {
        throw new IllegalStateException(
                "A completed appointment cannot be rescheduled");
    }

    if (!appointment.getStartTime()
            .isAfter(LocalDateTime.now())) {
        throw new IllegalStateException(
                "A past or started appointment cannot be rescheduled");
    }

    Doctor doctor = doctorRepository
            .findByIdForUpdate(
                    appointment.getDoctor().getId())
            .orElseThrow(() -> new ResourceNotFoundException(
                    "Doctor not found"));

    MedicalService medicalService =
            appointment.getMedicalService();

    LocalDateTime newEndTime = request.startTime()
            .plusMinutes(
                    medicalService.getDurationMinutes());

    validateAppointmentTime(
            doctor,
            request.startTime(),
            newEndTime,
            medicalService.getDurationMinutes());

    checkRescheduleConflicts(
            appointmentId,
            user,
            doctor,
            request.startTime(),
            newEndTime);

    appointment.setStartTime(request.startTime());
    appointment.setEndTime(newEndTime);

    Appointment savedAppointment =
            appointmentRepository.save(appointment);

    return mapToResponse(savedAppointment);
}
private void checkRescheduleConflicts(
        Long appointmentId,
        User user,
        Doctor doctor,
        LocalDateTime startTime,
        LocalDateTime endTime) {

    long doctorConflicts = appointmentRepository
            .countOverlappingDoctorAppointmentsExcludingId(
                    doctor.getId(),
                    appointmentId,
                    startTime,
                    endTime,
                    AppointmentStatus.CANCELLED);

    if (doctorConflicts > 0) {
        throw new IllegalStateException(
                "The doctor already has an appointment during this time");
    }

    long userConflicts = appointmentRepository
            .countOverlappingUserAppointmentsExcludingId(
                    user.getId(),
                    appointmentId,
                    startTime,
                    endTime,
                    AppointmentStatus.CANCELLED);

    if (userConflicts > 0) {
        throw new IllegalStateException(
                "You already have an appointment during this time");
    }
}
}