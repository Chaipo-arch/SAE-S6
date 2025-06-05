package sae.semestre.six.domain.appointment;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import sae.semestre.six.domain.doctor.Doctor;
import sae.semestre.six.domain.doctor.DoctorDao;
import sae.semestre.six.domain.patient.Patient;
import sae.semestre.six.domain.patient.PatientDao;
import sae.semestre.six.domain.room.Room;
import sae.semestre.six.domain.room.RoomDao;
import sae.semestre.six.mail.EmailService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class SchedulingControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DoctorDao doctorDao;

    @Autowired
    private AppointmentDao appointmentDao;

    @PersistenceContext
    private EntityManager entityManager;

    private Doctor doctor;
    private Patient patient;
    private Room room;
    private LocalDateTime validAppointmentTime;

    @Autowired
    private EmailService emailService;

    @BeforeEach
    public void setup() {
        // Créer un docteur pour les tests
        doctor = new Doctor();
        // Utiliser directement SQL pour définir les propriétés du docteur
        doctor.setDoctorNumber("TEST-DOC-001");
        doctor.setSpecialization("Cardiology");
        doctor.setFirstName("Doctor");
        doctor.setLastName("Test");
        doctor.setEmail("doctor.test@example.com");
        doctor.setDepartment("Cardiology");
        doctor.setAppointments(new HashSet<>());
        doctorDao.save(doctor);

        // Créer un patient pour les tests
        patient = new Patient();
        // Définir les propriétés du patient directement dans la base de données
        entityManager.createNativeQuery(
                        "INSERT INTO patients (patient_number, first_name, last_name, email) VALUES (?1, ?2, ?3,?4)")
                .setParameter(1, "TEST-PAT-001")
                .setParameter(2, "John")
                .setParameter(3, "Doe")
                .setParameter(4,"e@e.com")
                .executeUpdate();

        // Récupérer le patient créé
        patient = (Patient) entityManager.createQuery(
                        "FROM Patient WHERE patientNumber = :patientNumber")
                .setParameter("patientNumber", "TEST-PAT-001")
                .getSingleResult();

        // Créer une salle pour les tests
        // Définir les propriétés de la salle directement dans la base de données
        entityManager.createNativeQuery(
                        "INSERT INTO rooms (room_number, type,is_occupied, capacity, current_patient_count) VALUES (?1, ?2,?5, ?3, ?4)")
                .setParameter(1, "TEST-ROOM-001")
                .setParameter(2, "Consultation")
                .setParameter(3, 5)
                .setParameter(4, 0)
                .setParameter(5, false)
                .executeUpdate();

        // Récupérer la salle créée
        room = (Room) entityManager.createQuery(
                        "FROM Room WHERE roomNumber = :roomNumber")
                .setParameter("roomNumber", "TEST-ROOM-001")
                .getSingleResult();

        // Date et heure valides pour un rendez-vous (10h du matin aujourd'hui)
        validAppointmentTime = LocalDateTime.of(LocalDate.now(), LocalTime.of(10, 0));
    }

    @Test
    public void testScheduleAppointment_Success() throws Exception {
        mockMvc.perform(post("/scheduling/appointment")
                        .param("doctorId", doctor.getId().toString())
                        .param("patientId", patient.getId().toString())
                        .param("roomId", room.getId().toString())
                        .param("appointmentDateTime", validAppointmentTime.toString())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(content().string("Appointment scheduled successfully"));

        // Vérifier que l'appointement a bien été enregistré dans la BD
        List<Appointment> doctorAppointments = appointmentDao.findByDoctorId(doctor.getId());
        assert(doctorAppointments.size() == 1);
        assert(doctorAppointments.getFirst().getAppointmentDate().equals(validAppointmentTime));
        assert(doctorAppointments.getFirst().getDoctor().getId().equals(doctor.getId()));
        assert(doctorAppointments.getFirst().getPatient().getId().equals(patient.getId()));
    }

    @Test
    public void testScheduleAppointment_DoctorNotAvailable() throws Exception {
        // Créer d'abord un rendez-vous à l'heure valide
        Appointment existing = new Appointment();
        existing.setAppointmentDate(validAppointmentTime);
        existing.setDoctor(doctor);
        existing.setPatient(patient);
        existing.setAppointmentNumber("EXISTING-APPT-001");
        existing.setStatus("SCHEDULED");
        doctor.getAppointments().add(existing);
        appointmentDao.save(existing);

        // Tenter de créer un autre rendez-vous au même moment
        mockMvc.perform(post("/scheduling/appointment")
                        .param("doctorId", doctor.getId().toString())
                        .param("patientId", patient.getId().toString())
                        .param("roomId", room.getId().toString())
                        .param("appointmentDateTime", validAppointmentTime.toString())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Doctor is not available at this time")));
    }

    @Test
    public void testScheduleAppointment_OutsideWorkingHours() throws Exception {
        // Essayer de planifier un rendez-vous à 7h du matin (avant les heures de travail)
        LocalDateTime earlyAppointment = LocalDateTime.of(LocalDate.now(), LocalTime.of(7, 0));

        mockMvc.perform(post("/scheduling/appointment")
                        .param("doctorId", doctor.getId().toString())
                        .param("patientId", patient.getId().toString())
                        .param("roomId", room.getId().toString())
                        .param("appointmentDateTime", earlyAppointment.toString())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Appointments only available between 9 AM and 5 PM")));

        // Essayer de planifier un rendez-vous à 18h (après les heures de travail)
        LocalDateTime lateAppointment = LocalDateTime.of(LocalDate.now(), LocalTime.of(18, 0));

        mockMvc.perform(post("/scheduling/appointment")
                        .param("doctorId", doctor.getId().toString())
                        .param("patientId", patient.getId().toString())
                        .param("roomId", room.getId().toString())
                        .param("appointmentDateTime", lateAppointment.toString())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Appointments only available between 9 AM and 5 PM")));
    }

    @Test
    public void testScheduleAppointment_EntityNotFound() throws Exception {
        // Test avec un docteur qui n'existe pas
        mockMvc.perform(post("/scheduling/appointment")
                        .param("doctorId", "99999")
                        .param("patientId", patient.getId().toString())
                        .param("roomId", room.getId().toString())
                        .param("appointmentDateTime", validAppointmentTime.toString())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Doctor or patient not found")));

        // Test avec un patient qui n'existe pas
        mockMvc.perform(post("/scheduling/appointment")
                        .param("doctorId", doctor.getId().toString())
                        .param("patientId", "99999")
                        .param("roomId", room.getId().toString())
                        .param("appointmentDateTime", validAppointmentTime.toString())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Doctor or patient not found")));

        // Test avec une salle qui n'existe pas
        mockMvc.perform(post("/scheduling/appointment")
                        .param("doctorId", doctor.getId().toString())
                        .param("patientId", patient.getId().toString())
                        .param("roomId", "99999")
                        .param("appointmentDateTime", validAppointmentTime.toString())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Room not found")));
    }

    @Test
    public void testScheduleAppointment_RoomNotAvailable() throws Exception {
        // Rendre la salle indisponible en la remplissant à capacité
        // Utiliser une requête SQL pour mettre à jour directement le nombre de patients
        entityManager.createNativeQuery(
                        "UPDATE rooms SET current_patient_count = ?1 WHERE room_number = ?2")
                .setParameter(1, 5) // Capacité maximale
                .setParameter(2, "TEST-ROOM-001")
                .executeUpdate();

        // Rafraîchir l'entité room depuis la base de données
        entityManager.refresh(room);

        mockMvc.perform(post("/scheduling/appointment")
                        .param("doctorId", doctor.getId().toString())
                        .param("patientId", patient.getId().toString())
                        .param("roomId", room.getId().toString())
                        .param("appointmentDateTime", validAppointmentTime.toString())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Room is not available")));
    }

    @Test
    public void testGetAvailableSlots() throws Exception {
        // Créer un rendez-vous à 11h pour que ce créneau ne soit pas disponible
        LocalDateTime bookedSlot = LocalDateTime.of(LocalDate.now(), LocalTime.of(11, 0));
        Appointment existing = new Appointment();
        existing.setAppointmentDate(bookedSlot);
        existing.setDoctor(doctor);
        existing.setPatient(patient);
        existing.setAppointmentNumber("EXISTING-APPT-002");
        existing.setStatus("SCHEDULED");
        doctor.getAppointments().add(existing);
        appointmentDao.save(existing);

        // Récupérer les créneaux disponibles pour aujourd'hui
        mockMvc.perform(get("/scheduling/available-slots")
                        .param("doctorId", doctor.getId().toString())
                        .param("date", LocalDate.now().toString())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(8))) // 9 créneaux moins le créneau réservé
                .andExpect(jsonPath("$", not(hasItem(endsWith("11:00:00"))))) // Le créneau de 11h ne doit pas être présent
                .andExpect(jsonPath("$", hasItem(endsWith("10:00:00")))) // Le créneau de 10h doit être présent
                .andExpect(jsonPath("$", hasItem(endsWith("09:00:00")))) // Le créneau de 9h doit être présent
                .andExpect(jsonPath("$", hasItem(endsWith("17:00:00")))); // Le créneau de 17h doit être présent
    }

    @Test
    public void testGetAvailableSlots_DifferentDate() throws Exception {
        // Créer un rendez-vous à 11h aujourd'hui
        LocalDateTime bookedSlot = LocalDateTime.of(LocalDate.now(), LocalTime.of(11, 0));
        Appointment existing = new Appointment();
        existing.setAppointmentDate(bookedSlot);
        existing.setDoctor(doctor);
        existing.setPatient(patient);
        existing.setAppointmentNumber("EXISTING-APPT-003");
        existing.setStatus("SCHEDULED");
        doctor.getAppointments().add(existing);
        appointmentDao.save(existing);

        // Récupérer les créneaux disponibles pour demain (tous les créneaux devraient être disponibles)
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        mockMvc.perform(get("/scheduling/available-slots")
                        .param("doctorId", doctor.getId().toString())
                        .param("date", tomorrow.toString())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(9))) // Tous les créneaux devraient être disponibles
                .andExpect(jsonPath("$[*]", everyItem(containsString(tomorrow.toString()))));
    }
}