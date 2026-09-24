package com.anurag.appointmentbooking.repository;

import com.anurag.appointmentbooking.model.DoctorSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;

public interface DoctorScheduleRepository
        extends JpaRepository<DoctorSchedule, Long> {

    Optional<DoctorSchedule> findByDoctorIdAndDayOfWeek(
            Long doctorId,
            DayOfWeek dayOfWeek);

    List<DoctorSchedule> findAllByDoctorId(Long doctorId);
}