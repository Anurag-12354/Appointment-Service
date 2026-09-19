package com.anurag.appointmentbooking.model;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "doctors")
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String specialization;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private boolean active = true;

    @ManyToMany
    @JoinTable(name = "doctor_services", joinColumns = @JoinColumn(name = "doctor_id"), inverseJoinColumns = @JoinColumn(name = "service_id"))
    private Set<MedicalService> services = new HashSet<>();

    public Doctor() {
    }

    public Doctor(
            String name,
            String specialization,
            String email) {
        this.name = name;
        this.specialization = specialization;
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Set<MedicalService> getServices() {
        return services;
    }

    public void addService(MedicalService service) {
        services.add(service);
        service.getDoctors().add(this);
    }

    public void removeService(MedicalService service) {
        services.remove(service);
        service.getDoctors().remove(this);
    }
}