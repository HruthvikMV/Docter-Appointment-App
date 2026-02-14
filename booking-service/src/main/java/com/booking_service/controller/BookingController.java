package com.booking_service.controller;

import com.booking_service.client.DoctorClient;
import com.booking_service.client.PatientClient;
import com.booking_service.dto.Doctor;
import com.booking_service.dto.DoctorAppointmentSchedule;
import com.booking_service.dto.Patient;
import com.booking_service.dto.TimeSlots;
import com.booking_service.entity.BookingConfirmation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/booking")
public class BookingController {

    @Autowired
    private DoctorClient doctorClient;

    @Autowired
    private PatientClient patientClient;

    // Example: http://localhost:8085/api/v1/booking/getdoctor?doctorId=1&patientId=1
    @GetMapping("/getdoctor")
    public String getDoctorById(
            @RequestParam Long doctorId,
            @RequestParam Long patientId,
            @RequestParam LocalDate date,
            @RequestParam LocalTime time

            ) {
        Patient p = patientClient.getPatientById(patientId);
        Doctor d = doctorClient.getDoctorById(doctorId);

        BookingConfirmation bookingConfirmation = new BookingConfirmation();
        bookingConfirmation.setDoctorName(d.getName());
        List<DoctorAppointmentSchedule> appointmentSchedules = d.getAppointmentSchedules();
        for(DoctorAppointmentSchedule app:appointmentSchedules){
            LocalDate localDate = app.getDate();
            if(localDate.isEqual(date)) {
                List<TimeSlots> timeSlots = app.getTimeSlots();
                for (TimeSlots t : timeSlots) {
                    if(timeSlots.equals(time)){
                        System.out.println("Complete Booking");
                    }
                }
            }
        }

        return "done";
    }

}
