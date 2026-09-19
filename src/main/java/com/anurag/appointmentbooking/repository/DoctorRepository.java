package com.anurag.appointmentbooking.repository;

import com.anurag.appointmentbooking.model.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    List<Doctor> findByActiveTrue();

    List<Doctor> findBySpecializationIgnoreCase(String specialization);

    boolean existsByEmail(String email);
}