package com.doctor_service.repository;

import com.doctor_service.entity.TimeSlots;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TimeSlotsRepository extends JpaRepository<TimeSlots, Long> {
    @Query(value = "SELECT * FROM time_slots WHERE appointment_schedule_id = :appointmentScheduleId", nativeQuery = true)
    List<TimeSlots> getAllTimeSlots(@Param("appointmentScheduleId") long appointmentScheduleId);
}
