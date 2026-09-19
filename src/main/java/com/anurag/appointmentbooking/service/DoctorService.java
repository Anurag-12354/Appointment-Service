package com.anurag.appointmentbooking.service;

import com.anurag.appointmentbooking.dto.DoctorDetailsResponse;
import com.anurag.appointmentbooking.dto.DoctorRequest;
import com.anurag.appointmentbooking.dto.DoctorResponse;
import com.anurag.appointmentbooking.dto.MedicalServiceResponse;
import com.anurag.appointmentbooking.exception.DuplicateResourceException;
import com.anurag.appointmentbooking.exception.ResourceNotFoundException;
import com.anurag.appointmentbooking.model.Doctor;
import com.anurag.appointmentbooking.model.MedicalService;
import com.anurag.appointmentbooking.repository.DoctorRepository;
import com.anurag.appointmentbooking.repository.MedicalServiceRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;

@Service
public class DoctorService {

        private final DoctorRepository doctorRepository;
        private final MedicalServiceRepository medicalServiceRepository;

        public DoctorService(
                        DoctorRepository doctorRepository,
                        MedicalServiceRepository medicalServiceRepository) {
                this.doctorRepository = doctorRepository;
                this.medicalServiceRepository = medicalServiceRepository;
        }

        @Transactional
        public DoctorResponse createDoctor(DoctorRequest request) {

                String normalizedEmail = request.email()
                                .trim()
                                .toLowerCase(Locale.ROOT);

                if (doctorRepository.existsByEmail(normalizedEmail)) {
                        throw new DuplicateResourceException(
                                        "A doctor with this email already exists");
                }

                Doctor doctor = new Doctor(
                                request.name().trim(),
                                request.specialization().trim(),
                                normalizedEmail);

                Doctor savedDoctor = doctorRepository.save(doctor);

                return mapToResponse(savedDoctor);
        }

        private DoctorResponse mapToResponse(Doctor doctor) {
                return new DoctorResponse(
                                doctor.getId(),
                                doctor.getName(),
                                doctor.getSpecialization(),
                                doctor.getEmail(),
                                doctor.isActive());
        }

        @Transactional
        public DoctorDetailsResponse assignService(
                        Long doctorId,
                        Long serviceId) {
                Doctor doctor = doctorRepository.findById(doctorId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Doctor not found with ID: " + doctorId));

                MedicalService medicalService = medicalServiceRepository.findById(serviceId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Medical service not found with ID: " + serviceId));

                if (!doctor.isActive()) {
                        throw new IllegalStateException(
                                        "Services cannot be assigned to an inactive doctor");
                }

                if (!medicalService.isActive()) {
                        throw new IllegalStateException(
                                        "An inactive medical service cannot be assigned");
                }

                boolean alreadyAssigned = doctor.getServices()
                                .stream()
                                .anyMatch(service -> service.getId().equals(serviceId));

                if (!alreadyAssigned) {
                        doctor.addService(medicalService);
                }

                return mapToDetailsResponse(doctor);
        }

        private DoctorDetailsResponse mapToDetailsResponse(Doctor doctor) {

                List<MedicalServiceResponse> services = doctor.getServices()
                                .stream()
                                .sorted(Comparator.comparing(
                                                MedicalService::getName))
                                .map(service -> new MedicalServiceResponse(
                                                service.getId(),
                                                service.getName(),
                                                service.getDescription(),
                                                service.getDurationMinutes(),
                                                service.getPrice(),
                                                service.isActive()))
                                .toList();

                return new DoctorDetailsResponse(
                                doctor.getId(),
                                doctor.getName(),
                                doctor.getSpecialization(),
                                doctor.getEmail(),
                                doctor.isActive(),
                                services);
        }
}