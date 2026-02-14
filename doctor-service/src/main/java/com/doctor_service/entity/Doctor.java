package com.doctor_service.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "doctor")
public class Doctor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "specialization", nullable = false)
    private String specialization;

    @Column(name = "qualification",nullable = false)
    private String qualification;

    @Column(name = "contact", nullable = false)
    private String contact;

    @Column(name = "experience", nullable = false)
    private String experience;

    @Column(name = "url", nullable = false, length = 2000)
    private String url;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "state_id")
    private State state;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "city_id")
    private City city;



    @Column(name = "address", nullable = false, length = 1000)
    private String address;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "area_id")
    private Area area;

    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<DoctorAppointmentSchedule> appointmentSchedules;

    public List<DoctorAppointmentSchedule> getAppointmentSchedules() {
        return appointmentSchedules;
    }

    public void setAppointmentSchedules(List<DoctorAppointmentSchedule> appointmentSchedules) {
        this.appointmentSchedules = appointmentSchedules;
    }

    public Area getArea() {
        return area;
    }

    public void setArea(Area area) {
        this.area = area;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getQualification() {
        return qualification;
    }

    public void setQualification(String qualification) {
        this.qualification = qualification;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}