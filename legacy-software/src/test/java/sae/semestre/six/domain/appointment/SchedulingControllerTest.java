package sae.semestre.six.domain.appointment;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import sae.semestre.six.domain.doctor.Doctor;
import sae.semestre.six.domain.doctor.DoctorDao;
import sae.semestre.six.domain.patient.Patient;
import sae.semestre.six.domain.patient.PatientDao;
import sae.semestre.six.domain.room.RoomDao;
import sae.semestre.six.exception.InvalidDataException;
import sae.semestre.six.mail.EmailService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;


class SchedulingControllerTest {

    @Mock
    private AppointmentDao appointmentDao;

    @Mock
    private DoctorDao doctorDao;
    @Mock
    private RoomDao roomDao;
    @Mock
    private PatientDao patientDao;
    @Mock
    private EmailService emailService;

    @InjectMocks
    private AppointmentService appointmentsService;

    private SchedulingController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        controller= new SchedulingController(appointmentsService);

        // No manual instantiation: Mockito will inject mocks into controller
    }

    @Test
    @DisplayName("scheduleAppointment doit renvoyer une erreur si le docteur est occupé")
    void scheduleAppointment_shouldReturnDoctorUnavailable() {
        long doctorId = 1L;
        long patientId = 1L;
        LocalDateTime targetDate = LocalDateTime.of(2025, 5, 13, 15, 0);
        long roomID = 0L;
        Doctor doctor = new Doctor();
        doctor.setEmail("doctor@example.com");

        when(doctorDao.findById(doctorId)).thenReturn(doctor);

        Appointment existingAppointment = new Appointment();
        existingAppointment.setAppointmentDate(targetDate);
        doctor.addAppointment(existingAppointment);
        when(appointmentDao.findByDoctorId(doctorId))
                .thenReturn(List.of(existingAppointment));
        when(patientDao.findById(patientId)).thenReturn(new Patient());


        String result = assertThrows(InvalidDataException.class,()->controller.scheduleAppointment(doctorId, patientId, roomID,targetDate)).getMessage();

        assertEquals("Doctor is not available at this time", result);
        verify(appointmentDao).findByDoctorId(doctorId);
    }

    @Test
    @DisplayName("getAvailableSlots doit renvoyer des créneaux disponibles")
    void getAvailableSlots_shouldReturnFreeSlots() {
        Long doctorId = 1L;
        LocalDate date = LocalDate.of(2025, 5, 13);
        LocalDateTime occupiedSlot = LocalDateTime.of(date, LocalTime.of(10, 0));

        Appointment appointment = new Appointment();
        appointment.setAppointmentDate(occupiedSlot);
        when(appointmentDao.findByDoctorId(doctorId))
                .thenReturn(List.of(appointment));

        List<LocalDateTime> availableSlots = controller.getAvailableSlots(doctorId, date).getBody();

        assertEquals(8, availableSlots.size()); // De 9 à 17, sauf 10h
        assertEquals(LocalDateTime.of(date, LocalTime.of(9, 0)), availableSlots.get(0));
        assertEquals(LocalDateTime.of(date, LocalTime.of(11, 0)), availableSlots.get(1));
        verify(appointmentDao).findByDoctorId(doctorId);
    }
}
