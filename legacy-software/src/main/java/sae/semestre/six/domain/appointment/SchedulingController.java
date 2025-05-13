package sae.semestre.six.domain.appointment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import sae.semestre.six.domain.doctor.DoctorDao;
import sae.semestre.six.domain.doctor.Doctor;
import sae.semestre.six.mail.EmailService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RestController
@RequestMapping("/scheduling")
public class SchedulingController {

    @Autowired
    private AppointmentDao appointmentDao;

    @Autowired
    private DoctorDao doctorDao;

    private final EmailService emailService;

    public SchedulingController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/appointment")
    public String scheduleAppointment(
            @RequestParam Long doctorId,
            @RequestParam Long patientId,
            // on attend maintenant un LocalDateTime ISO : "2025-05-13T14:30"
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime appointmentDateTime) {
        try {
            Doctor doctor = doctorDao.findById(doctorId);

            // on récupère les rdvs existants et compare l'heure et la date
            List<Appointment> doctorAppointments = appointmentDao.findByDoctorId(doctorId);
            for (Appointment existing : doctorAppointments) {
                LocalDateTime existingDateTime = existing.getAppointmentDate();
                if (existingDateTime.equals(appointmentDateTime)) {
                    return "Doctor is not available at this time";
                }
            }

            LocalTime time = appointmentDateTime.toLocalTime();
            // créneaux entre 09:00 et 17:00 inclus
            if (time.isBefore(LocalTime.of(9, 0)) || time.isAfter(LocalTime.of(17, 0))) {
                return "Appointments only available between 9 AM and 5 PM";
            }

            // envoi de mail
            emailService.sendEmail(
                    doctor.getEmail(),
                    "New Appointment Scheduled",
                    "You have a new appointment on " + appointmentDateTime
            );

            return "Appointment scheduled successfully";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    @GetMapping("/available-slots")
    public List<LocalDateTime> getAvailableSlots(
            @RequestParam Long doctorId,
            // on passe la journée en ISO date "2025-05-13"
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        // récupération de tous les rdvs pour ce docteur ce jour-là
        List<Appointment> doctorAppsToday = appointmentDao.findByDoctorId(doctorId)
                .stream()
                .filter(app -> app.getAppointmentDate().toLocalDate().equals(date))
                .collect(Collectors.toList());

        // on génère la liste des heures de 9h à 17h
        return IntStream.rangeClosed(9, 17)
                .mapToObj(hour -> LocalDateTime.of(date, LocalTime.of(hour, 0)))
                .filter(slot -> doctorAppsToday.stream()
                        .noneMatch(app -> app.getAppointmentDate().toLocalTime().equals(slot.toLocalTime()))
                )
                .collect(Collectors.toList());
    }
}
