package com.doctor_service.controller;

import com.doctor_service.dto.SearchResultDto;
import com.doctor_service.entity.Doctor;
import com.doctor_service.entity.DoctorAppointmentSchedule;
import com.doctor_service.entity.TimeSlots;
import com.doctor_service.repository.DoctorRepository;
import com.doctor_service.repository.TimeSlotsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/doctor")
public class SearchController {

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private TimeSlotsRepository timeSlotsRepository;

    // Example:
    // http://localhost:8080/api/v1/doctor/search?specialization=cardiologist&areaName=btm
    @GetMapping("/search")
    public ResponseEntity<List<SearchResultDto>> searchDoctors(
            @RequestParam String specialization,
            @RequestParam String areaName
    ) {
        LocalDate today = LocalDate.now();
        List<SearchResultDto> result = new ArrayList<>();

        List<Doctor> doctors = doctorRepository.findBySpecializationAndArea(specialization, areaName);

        for (Doctor doctor : doctors) {
            // new DTO for each doctor
            SearchResultDto dto = new SearchResultDto();

            List<LocalDate> validDates = new ArrayList<>();
            List<LocalTime> allTimeSlots = new ArrayList<>();

            List<DoctorAppointmentSchedule> schedules = doctor.getAppointmentSchedules();

            for (DoctorAppointmentSchedule schedule : schedules) {
                LocalDate scheduleDate = schedule.getDate();
                LocalTime now = LocalTime.now();
                List<TimeSlots> timeSlots = timeSlotsRepository.getAllTimeSlots(schedule.getId());

                for (TimeSlots ts : timeSlots) {
                    LocalTime slotTime = ts.getTime();

                    // If schedule is today → only future times
                    if (scheduleDate.isEqual(today)) {
                        if (slotTime.isAfter(now)) {
                            allTimeSlots.add(slotTime);
                        }
                    }
                    // If schedule is in the future → add all times
                    else if (scheduleDate.isAfter(today)) {
                        allTimeSlots.add(slotTime);
                    }
                }
            }

            // ✅ Fill doctor info
            dto.setDoctorId(doctor.getId());
            dto.setName(doctor.getName());
            dto.setArea(doctor.getArea().getName());
            dto.setCity(doctor.getCity().getName());
            dto.setQualification(doctor.getQualification());
            dto.setSpecialization(doctor.getSpecialization());
            dto.setDates(validDates);
            dto.setTimeSlots(allTimeSlots);

            result.add(dto);
        }

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

   // http://localhost:8080/api/v1/doctor/getdoctorbyid?id=1
    @GetMapping("/getdoctorbyid")
    public Doctor getDoctorById(
            @RequestParam long id
    ){
        return doctorRepository.findById(id).get();
    }
}
