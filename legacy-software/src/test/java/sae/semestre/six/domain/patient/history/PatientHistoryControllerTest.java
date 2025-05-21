package sae.semestre.six.domain.patient.history;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import sae.semestre.six.domain.patient.Patient;
import sae.semestre.six.exception.InvalidDataException;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
public class PatientHistoryControllerTest {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private PatientHistoryController patientHistoryController;

    @Autowired
    private PatientHistoryDao patientHistoryDao;

    private Patient testPatient;

    @BeforeEach
    void setUp() {
        LocalDate localDate = LocalDate.now();
        testPatient = Patient.builder().patientNumber("PAT123").firstName("Marie")
                .lastName("Curie").gender("F").phoneNumber("0600000000").email("e@e.e")
                .dateOfBirth(localDate.minusYears(40))
                .build();
        entityManager.persist(testPatient);
    }

    @Test
    void testCreateHistory() {
        patientHistoryController.createPatientHistory(testPatient.getId(),
                new PatientHistoryInformation(new Date(),"diagnosis","symptons","notes"));
        PatientHistory patientHistory = patientHistoryDao.findCompleteHistoryByPatientId(testPatient.getId()).getFirst();
        assertEquals("diagnosis",patientHistory.getDiagnosis());
    }

    @Test
    void testCreateHistoryBadId() {
        assertThrows(InvalidDataException.class, () ->patientHistoryController.createPatientHistory(0L,
                new PatientHistoryInformation(new Date(),"diagnosis","symptons","notes")));
    }

    @Test
    void testTwoHistoryCreatedForTheSamePatient() {
        patientHistoryController.createPatientHistory(testPatient.getId(),
                new PatientHistoryInformation(new Date(),"diagnosis","symptons","notes"));
        PatientHistory patientHistory = patientHistoryDao.findCompleteHistoryByPatientId(testPatient.getId()).getFirst();
        assertEquals("diagnosis",patientHistory.getDiagnosis());

        assertThrows(Exception.class,()->patientHistoryController.createPatientHistory(testPatient.getId(),
                new PatientHistoryInformation(new Date(),"diagnosis","symptons","notes")));
    }
}
