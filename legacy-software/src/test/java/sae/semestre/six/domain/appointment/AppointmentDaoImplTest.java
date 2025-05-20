package sae.semestre.six.domain.appointment;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentDaoImplTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private TypedQuery<Appointment> query;

    @InjectMocks
    private AppointmentDaoImpl dao;

    @BeforeEach
    void setUp() {
        when(entityManager.createQuery(anyString(), eq(Appointment.class)))
                .thenReturn(query);
    }

    @Test
    @DisplayName("findByPatientId doit retourner les rendez-vous d'un patient")
    void findByPatientId_shouldReturnAppointments() {
        Long patientId = 1L;
        List<Appointment> expectedAppointments = Arrays.asList(new Appointment(), new Appointment());

        when(query.setParameter("patientId", patientId)).thenReturn(query);
        when(query.getResultList()).thenReturn(expectedAppointments);

        List<Appointment> result = dao.findByPatientId(patientId);

        assertEquals(2, result.size());
        verify(entityManager).createQuery(contains("patient.id = :patientId"), eq(Appointment.class));
        verify(query).setParameter("patientId", patientId);
    }

    @Test
    @DisplayName("findByDoctorId doit retourner les rendez-vous d'un docteur")
    void findByDoctorId_shouldReturnAppointments() {
        Long doctorId = 2L;
        List<Appointment> expectedAppointments = Arrays.asList(new Appointment());

        when(query.setParameter("doctorId", doctorId)).thenReturn(query);
        when(query.getResultList()).thenReturn(expectedAppointments);

        List<Appointment> result = dao.findByDoctorId(doctorId);

        assertEquals(1, result.size());
        verify(entityManager).createQuery(contains("doctor.id = :doctorId"), eq(Appointment.class));
        verify(query).setParameter("doctorId", doctorId);
    }

    @Test
    @DisplayName("findByDateRange doit retourner les rendez-vous entre deux dates")
    void findByDateRange_shouldReturnAppointments() {
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = startDate.plusDays(1);
        List<Appointment> appointments = List.of(new Appointment());

        when(query.setParameter("startDate", startDate)).thenReturn(query);
        when(query.setParameter("endDate", endDate)).thenReturn(query);
        when(query.getResultList()).thenReturn(appointments);

        List<Appointment> result = dao.findByDateRange(startDate, endDate);

        assertEquals(1, result.size());
        verify(entityManager).createQuery(contains("appointmentDate BETWEEN"), eq(Appointment.class));
        verify(query).setParameter("startDate", startDate);
        verify(query).setParameter("endDate", endDate);
    }
}