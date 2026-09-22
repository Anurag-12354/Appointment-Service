package com.anurag.appointmentbooking.repository;

import com.anurag.appointmentbooking.model.Doctor;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    List<Doctor> findByActiveTrue();

    List<Doctor> findBySpecializationIgnoreCase(
            String specialization);

    boolean existsByEmail(String email);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT d FROM Doctor d WHERE d.id = :doctorId")
    Optional<Doctor> findByIdForUpdate(
            @Param("doctorId") Long doctorId);
}