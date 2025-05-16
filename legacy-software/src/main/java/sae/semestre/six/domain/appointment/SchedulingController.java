package sae.semestre.six.domain.appointment;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import sae.semestre.six.domain.doctor.DoctorDao;
import sae.semestre.six.domain.doctor.Doctor;
import sae.semestre.six.domain.patient.Patient;
import sae.semestre.six.domain.patient.PatientDao;
import sae.semestre.six.domain.room.Room;
import sae.semestre.six.domain.room.RoomDao;
import sae.semestre.six.mail.EmailService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RestController
@RequestMapping("/scheduling")
public class SchedulingController {

    private final AppointmentDao appointmentDao;
    private final DoctorDao doctorDao;
    private final PatientDao patientDao;
    private final EmailService emailService;
    private final RoomDao roomDao;

    public SchedulingController(
            AppointmentDao appointmentDao,
            DoctorDao doctorDao, PatientDao patientDao,
            EmailService emailService,
            RoomDao roomDao) {
        this.appointmentDao = appointmentDao;
        this.doctorDao = doctorDao;
        this.patientDao = patientDao;
        this.emailService = emailService;
        this.roomDao = roomDao;
    }

    @PostMapping("/appointment")
    public String scheduleAppointment(
            @RequestParam Long doctorId,
            @RequestParam Long patientId,
            @RequestParam Long roomId,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime appointmentDateTime) {
        // Vérifier la disponibilité du docteur
        Doctor doctor = doctorDao.findById(doctorId);
        Patient patient = patientDao.findById(patientId);
        Room room = roomDao.findById(roomId);
        List<Appointment> doctorAppointments = appointmentDao.findByDoctorId(doctorId);
        boolean conflict = doctorAppointments.stream()
                .anyMatch(existing -> existing.getAppointmentDate().toLocalDate().equals(appointmentDateTime.toLocalDate()) &&
                        existing.getAppointmentDate().getHour() == appointmentDateTime.getHour());

        if (conflict) {
            return "Doctor is not available at this time";
        }
        if(doctor == null || patient == null) {
            return "Doctor or patient not found";
        }
        if(room == null) {
            return "Room not found";
        }
        if(!room.getAvailability().canAcceptPatient()){
            return "Room is not available";
        }

        LocalTime time = appointmentDateTime.toLocalTime();
        if (time.isBefore(LocalTime.of(9, 0)) || time.isAfter(LocalTime.of(17, 0))) {
            return "Appointments only available between 9 AM and 5 PM";
        }

        Appointment appointment = new Appointment();
        appointment.setAppointmentDate(appointmentDateTime);
        appointment.setDoctor(doctor);
        appointment.setPatient(patient);
        appointment.setAppointmentNumber("APPT" + System.currentTimeMillis());
        appointment.setStatus("SCHEDULED");

        doctor.getAppointments().add(appointment);
        appointmentDao.save(appointment);
        // Envoyer un email de confirmation
        emailService.sendEmail(
                doctor.getEmail(),
                "New Appointment Scheduled",
                "You have a new appointment on " + appointmentDateTime
        );

        return "Appointment scheduled successfully";
    }

    @GetMapping("/available-slots")
    public List<LocalDateTime> getAvailableSlots(
            @RequestParam Long doctorId,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<Appointment> doctorAppsToday = appointmentDao.findByDoctorId(doctorId).stream()
                .filter(app -> app.getAppointmentDate().toLocalDate().equals(date))
                .toList();

        return IntStream.rangeClosed(9, 17)
                .mapToObj(hour -> LocalDateTime.of(date, LocalTime.of(hour, 0)))
                .filter(slot -> doctorAppsToday.stream()
                        .noneMatch(app -> app.getAppointmentDate().toLocalTime().equals(slot.toLocalTime())))
                .collect(Collectors.toList());
    }
}