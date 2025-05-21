package sae.semestre.six.domain.appointment;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import sae.semestre.six.domain.doctor.Doctor;
import sae.semestre.six.domain.doctor.DoctorDao;
import sae.semestre.six.domain.patient.Patient;
import sae.semestre.six.domain.patient.PatientDao;
import sae.semestre.six.domain.room.Room;
import sae.semestre.six.domain.room.RoomDao;
import sae.semestre.six.exception.InvalidDataException;
import sae.semestre.six.exception.ResourceNotFoundException;
import sae.semestre.six.mail.EmailService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class AppointmentsService {

    private final AppointmentDao appointmentDao;
    private final RoomDao roomDao;
    private DoctorDao doctorDao;
    private final PatientDao patientDao;
    private final EmailService emailService;

    public AppointmentsService(AppointmentDao appointmentDao , RoomDao roomDao, DoctorDao doctorDao, PatientDao patientDao, EmailService emailService) {
        this.appointmentDao = appointmentDao;
        this.roomDao = roomDao;
        this.doctorDao = doctorDao;
        this.patientDao = patientDao;
        this.emailService = emailService;
    }
    public void assignRoom(Long appointmentId, String roomNumber) {
        roomDao.findByRoomNumber(roomNumber).assignAppointment(appointmentDao.findById(appointmentId));

    }

    public Appointment create(Long doctorId, Long patientId, Long roomId, LocalDateTime appointmentDateTime) {

        Doctor doctor = doctorDao.findById(doctorId);
        Patient patient = patientDao.findById(patientId);
        Room room = roomDao.findById(roomId);
        if(doctor == null || patient == null) {
            throw new ResourceNotFoundException("Doctor or patient not found");
        }

        List<Appointment> doctorAppointments = appointmentDao.findByDoctorId(doctorId);
        boolean conflict = doctorAppointments.stream()
                .anyMatch(appointment -> appointment.isSameDate(appointmentDateTime));

        if (conflict) {
            throw new InvalidDataException("Doctor is not available at this time");
        }

        if(room == null) {
            throw new ResourceNotFoundException("Room not found");
        }
        if(!room.getAvailability().canAcceptPatient()){
            throw new InvalidDataException("Room is not available");
        }

        LocalTime time = appointmentDateTime.toLocalTime();
        if (Appointment.isWorkingHours(time)) {
            throw new InvalidDataException("Appointments only available between 9 AM and 5 PM");
        }

        Appointment appt = new Appointment();
        appt.addDoctor(doctor);
        appt.addPatient(patient);
        appt.setRoomNumber(room.getRoomNumber());
        appt.setAppointmentDate(appointmentDateTime);
        appt.setStatus("SCHEDULED");
        appt.setAppointmentNumber("APPT" + System.currentTimeMillis());
        appointmentDao.save(appt);

        emailService.sendEmail(
                doctor.getEmail(),
                "New Appointment Scheduled",
                "You have a new appointment on " + appointmentDateTime
        );
        return appt;
    }

    public List<LocalDateTime> getAvailableSlots(
             Long doctorId,
             LocalDate date) {
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
