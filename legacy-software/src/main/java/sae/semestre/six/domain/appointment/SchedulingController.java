package sae.semestre.six.domain.appointment;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/scheduling")
public class SchedulingController {

    private final AppointmentService appointmentsService;

    public SchedulingController(
            AppointmentService appointmentsService) {
        this.appointmentsService = appointmentsService;
    }

    @PostMapping("/appointment")
    public String scheduleAppointment(
            @RequestParam Long doctorId,
            @RequestParam Long patientId,
            @RequestParam Long roomId,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime appointmentDateTime) {
        appointmentsService.create(doctorId,patientId,roomId,appointmentDateTime);
        return "Appointment scheduled successfully";
    }

    @GetMapping("/available-slots/{doctorId}")
    public ResponseEntity<List<LocalDateTime>> getAvailableSlots(
            @PathVariable Long doctorId,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(appointmentsService.getAvailableSlots(doctorId,date));
    }
}