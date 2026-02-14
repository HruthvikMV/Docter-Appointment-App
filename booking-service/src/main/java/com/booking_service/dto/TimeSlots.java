package com.booking_service.dto;


import java.time.LocalTime;

public class TimeSlots {
    private Long id;

    private LocalTime time;

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