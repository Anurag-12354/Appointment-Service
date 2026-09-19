package com.anurag.appointmentbooking.repository;

import com.anurag.appointmentbooking.model.Appointment;
import com.anurag.appointmentbooking.model.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentRepository
        extends JpaRepository<Appointment, Long> {

    List<Appointment> findAllByUserIdOrderByStartTimeDesc(Long userId);

    List<Appointment> findAllByDoctorIdOrderByStartTimeAsc(Long doctorId);

    List<Appointment> findAllByStatus(AppointmentStatus status);

    @Query("""
            SELECT COUNT(a)
            FROM Appointment a
            WHERE a.doctor.id = :doctorId
              AND a.status <> :cancelledStatus
              AND a.startTime < :requestedEnd
              AND a.endTime > :requestedStart
            """)
    long countOverlappingAppointments(
            @Param("doctorId") Long doctorId,
            @Param("requestedStart") LocalDateTime requestedStart,
            @Param("requestedEnd") LocalDateTime requestedEnd,
            @Param("cancelledStatus") AppointmentStatus cancelledStatus);
}