package sae.semestre.six.domain.doctor;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DoctorDaoImplTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private TypedQuery<Doctor> query;

    @InjectMocks
    private DoctorDaoImpl doctorDao;

    @BeforeEach
    void setUp() {
        // Configuration commune pour le mock d'EntityManager
        when(entityManager.createQuery(anyString(), eq(Doctor.class))).thenReturn(query);
    }

    @Test
    @DisplayName("findByDoctorNumber doit retourner le bon docteur")
    void whenFindByDoctorNumber_thenReturnDoctor() {
        // Préparation
        Doctor doctor = new Doctor();
        doctor.setDoctorNumber("DOC-123");
        doctor.setFirstName("John");
        doctor.setLastName("Smith");

        when(query.setParameter(anyString(), anyString())).thenReturn(query);
        when(query.getSingleResult()).thenReturn(doctor);

        // Exécution
        Optional<Doctor> result = doctorDao.findByDoctorNumber("DOC-123");

        // Vérification
        assertTrue(result.isPresent());
        assertEquals("DOC-123", result.get().getDoctorNumber());
        assertEquals("John", result.get().getFirstName());

        verify(entityManager).createQuery(contains("doctorNumber"), eq(Doctor.class));
        verify(query).setParameter("doctorNumber", "DOC-123");
    }

    @Test
    @DisplayName("findByDoctorNumber doit retourner un Optional vide quand le docteur n'existe pas")
    void whenFindByDoctorNumberWithNonExistingNumber_thenReturnEmpty() {
        // Préparation
        when(query.setParameter(anyString(), anyString())).thenReturn(query);
        when(query.getSingleResult()).thenThrow(new jakarta.persistence.NoResultException());

        // Exécution
        Optional<Doctor> result = doctorDao.findByDoctorNumber("INEXISTANT");

        // Vérification
        assertFalse(result.isPresent());

        verify(entityManager).createQuery(contains("doctorNumber"), eq(Doctor.class));
        verify(query).setParameter("doctorNumber", "INEXISTANT");
    }

    @Test
    @DisplayName("findBySpecialization doit retourner tous les docteurs spécialisés")
    void whenFindBySpecialization_thenReturnList() {
        // Préparation
        Doctor d1 = new Doctor();
        d1.setDoctorNumber("A");
        d1.setSpecialization("Cardio");

        Doctor d2 = new Doctor();
        d2.setDoctorNumber("B");
        d2.setSpecialization("Cardio");

        List<Doctor> doctors = Arrays.asList(d1, d2);

        when(query.setParameter(anyString(), anyString())).thenReturn(query);
        when(query.getResultList()).thenReturn(doctors);

        // Exécution
        List<Doctor> result = doctorDao.findBySpecialization("Cardio");

        // Vérification
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(d -> "A".equals(d.getDoctorNumber())));
        assertTrue(result.stream().anyMatch(d -> "B".equals(d.getDoctorNumber())));

        verify(entityManager).createQuery(contains("specialization"), eq(Doctor.class));
        verify(query).setParameter("specialization", "Cardio");
    }

    @Test
    @DisplayName("findByDepartment doit retourner tous les docteurs du département donné")
    void whenFindByDepartment_thenReturnList() {
        // Préparation
        Doctor d1 = new Doctor();
        d1.setDoctorNumber("X1");
        d1.setDepartment("Cardiology");

        Doctor d2 = new Doctor();
        d2.setDoctorNumber("X2");
        d2.setDepartment("Cardiology");

        List<Doctor> doctors = Arrays.asList(d1, d2);

        when(query.setParameter(anyString(), anyString())).thenReturn(query);
        when(query.getResultList()).thenReturn(doctors);

        // Exécution
        List<Doctor> result = doctorDao.findByDepartment("Cardiology");

        // Vérification
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(d -> "X1".equals(d.getDoctorNumber())));
        assertTrue(result.stream().anyMatch(d -> "X2".equals(d.getDoctorNumber())));

        verify(entityManager).createQuery(contains("department"), eq(Doctor.class));
        verify(query).setParameter("department", "Cardiology");
    }

    @Test
    @DisplayName("findByDepartment doit retourner une liste vide quand aucun docteur dans le département")
    void whenFindByDepartmentWithNoDoctors_thenReturnEmptyList() {
        // Préparation
        when(query.setParameter(anyString(), anyString())).thenReturn(query);

        // Exécution
        List<Doctor> result = doctorDao.findByDepartment("UnknownDept");

        // Vérification
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(entityManager).createQuery(contains("department"), eq(Doctor.class));
        verify(query).setParameter("department", "UnknownDept");
    }
}