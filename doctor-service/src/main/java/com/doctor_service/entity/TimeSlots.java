package com.doctor_service.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.time.LocalTime;

@Entity
@Table(name = "time_slots")
public class TimeSlots {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name="time")
    private LocalTime time;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_schedule_id")
    @JsonBackReference
    private DoctorAppointmentSchedule doctorAppointmentSchedule;

    public DoctorAppointmentSchedule getDoctorAppointmentSchedule() {
        return doctorAppointmentSchedule;
    }

    public void setDoctorAppointmentSchedule(DoctorAppointmentSchedule doctorAppointmentSchedule) {
        this.doctorAppointmentSchedule = doctorAppointmentSchedule;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}