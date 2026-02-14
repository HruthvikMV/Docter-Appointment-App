package com.booking_service.client;

import com.booking_service.dto.Doctor;
import com.booking_service.dto.Patient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "patient-service", url = "http://localhost:8082/api/v1/patient")
public interface PatientClient {
    @GetMapping("/getpatientbyid")
    public Patient getPatientById(@RequestParam long id);
}
