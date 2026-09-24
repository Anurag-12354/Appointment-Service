package com.anurag.appointmentbooking.repository;

import com.anurag.appointmentbooking.model.Appointment;
import com.anurag.appointmentbooking.model.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.EntityGraph;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;

import java.util.Optional;

import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentRepository
    extends JpaRepository<Appointment, Long> {
  @EntityGraph(attributePaths = { "doctor", "medicalService" })
  List<Appointment> findAllByUserIdOrderByStartTimeDesc(Long userId);

  @EntityGraph(attributePaths = {
      "user",
      "doctor",
      "medicalService"
  })
  List<Appointment> findAllByOrderByStartTimeDesc();

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

  @Query("""
      SELECT COUNT(a)
      FROM Appointment a
      WHERE a.user.id = :userId
        AND a.status <> :cancelledStatus
        AND a.startTime < :requestedEnd
        AND a.endTime > :requestedStart
      """)
  long countOverlappingUserAppointments(
      @Param("userId") Long userId,
      @Param("requestedStart") LocalDateTime requestedStart,
      @Param("requestedEnd") LocalDateTime requestedEnd,
      @Param("cancelledStatus") AppointmentStatus cancelledStatus);

      @Query("""
        SELECT a
        FROM Appointment a
        WHERE a.doctor.id = :doctorId
          AND a.status <> :cancelledStatus
          AND a.startTime < :periodEnd
          AND a.endTime > :periodStart
        ORDER BY a.startTime ASC
        """)
List<Appointment> findDoctorAppointmentsForPeriod(
        @Param("doctorId") Long doctorId,
        @Param("periodStart") LocalDateTime periodStart,
        @Param("periodEnd") LocalDateTime periodEnd,
        @Param("cancelledStatus") AppointmentStatus cancelledStatus);
@Lock(LockModeType.PESSIMISTIC_WRITE)
@Query("""
        SELECT a
        FROM Appointment a
        WHERE a.id = :appointmentId
        """)
Optional<Appointment> findByIdForUpdate(
        @Param("appointmentId") Long appointmentId);
        @Query("""
        SELECT COUNT(a)
        FROM Appointment a
        WHERE a.doctor.id = :doctorId
          AND a.id <> :appointmentId
          AND a.status <> :cancelledStatus
          AND a.startTime < :requestedEnd
          AND a.endTime > :requestedStart
        """)
long countOverlappingDoctorAppointmentsExcludingId(
        @Param("doctorId") Long doctorId,
        @Param("appointmentId") Long appointmentId,
        @Param("requestedStart") LocalDateTime requestedStart,
        @Param("requestedEnd") LocalDateTime requestedEnd,
        @Param("cancelledStatus") AppointmentStatus cancelledStatus);

@Query("""
        SELECT COUNT(a)
        FROM Appointment a
        WHERE a.user.id = :userId
          AND a.id <> :appointmentId
          AND a.status <> :cancelledStatus
          AND a.startTime < :requestedEnd
          AND a.endTime > :requestedStart
        """)
long countOverlappingUserAppointmentsExcludingId(
        @Param("userId") Long userId,
        @Param("appointmentId") Long appointmentId,
        @Param("requestedStart") LocalDateTime requestedStart,
        @Param("requestedEnd") LocalDateTime requestedEnd,
        @Param("cancelledStatus") AppointmentStatus cancelledStatus);
}