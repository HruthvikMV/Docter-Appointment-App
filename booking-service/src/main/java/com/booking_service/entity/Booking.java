package com.booking_service.entity;

import jakarta.persistence.*;

@Entity
@Table(name="bookings")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private long doctorId;
    private long patientId;


}
