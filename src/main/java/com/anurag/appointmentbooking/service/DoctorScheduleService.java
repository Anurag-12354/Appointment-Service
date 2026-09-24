package com.anurag.appointmentbooking.service;

import com.anurag.appointmentbooking.dto.DoctorScheduleRequest;
import com.anurag.appointmentbooking.dto.DoctorScheduleResponse;
import com.anurag.appointmentbooking.exception.ResourceNotFoundException;
import com.anurag.appointmentbooking.model.Doctor;
import com.anurag.appointmentbooking.model.DoctorSchedule;
import com.anurag.appointmentbooking.repository.DoctorRepository;
import com.anurag.appointmentbooking.repository.DoctorScheduleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
public class DoctorScheduleService {

    private final DoctorScheduleRepository doctorScheduleRepository;
    private final DoctorRepository doctorRepository;

    public DoctorScheduleService(
            DoctorScheduleRepository doctorScheduleRepository,
            DoctorRepository doctorRepository) {
        this.doctorScheduleRepository = doctorScheduleRepository;
        this.doctorRepository = doctorRepository;
    }

    @Transactional
    public DoctorScheduleResponse setSchedule(
            Long doctorId,
            DoctorScheduleRequest request) {

        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Doctor not found with ID: " + doctorId));

        if (!doctor.isActive()) {
            throw new IllegalStateException(
                    "A schedule cannot be added to an inactive doctor");
        }

        if (!request.startTime().isBefore(request.endTime())) {
            throw new IllegalStateException(
                    "Start time must be before end time");
        }

        DoctorSchedule schedule = doctorScheduleRepository
                .findByDoctorIdAndDayOfWeek(
                        doctorId,
                        request.dayOfWeek())
                .orElseGet(() -> new DoctorSchedule(
                        doctor,
                        request.dayOfWeek(),
                        request.startTime(),
                        request.endTime()));

        schedule.updateTimes(
                request.startTime(),
                request.endTime());

        DoctorSchedule savedSchedule = doctorScheduleRepository.save(schedule);

        return mapToResponse(savedSchedule);
    }

    @Transactional(readOnly = true)
    public List<DoctorScheduleResponse> getSchedule(Long doctorId) {

        if (!doctorRepository.existsById(doctorId)) {
            throw new ResourceNotFoundException(
                    "Doctor not found with ID: " + doctorId);
        }

        return doctorScheduleRepository
                .findAllByDoctorId(doctorId)
                .stream()
                .sorted(Comparator.comparingInt(
                        schedule -> schedule.getDayOfWeek().getValue()))
                .map(this::mapToResponse)
                .toList();
    }

    private DoctorScheduleResponse mapToResponse(
            DoctorSchedule schedule) {

        return new DoctorScheduleResponse(
                schedule.getId(),
                schedule.getDoctor().getId(),
                schedule.getDayOfWeek(),
                schedule.getStartTime(),
                schedule.getEndTime());
    }
}