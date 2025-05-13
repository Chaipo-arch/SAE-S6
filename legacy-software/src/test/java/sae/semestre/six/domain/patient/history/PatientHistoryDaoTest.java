package sae.semestre.six.domain.patient.history;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import sae.semestre.six.domain.billing.Bill;
import sae.semestre.six.domain.patient.Patient;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
public class PatientHistoryDaoTest {

    @Autowired
    private List<PatientHistoryDao> daoImplementations;

    @PersistenceContext
    private EntityManager entityManager;

    private Patient testPatient;

    @BeforeEach
    void beforeEach() {
        testPatient = Patient.builder().patientNumber("PAT123").firstName("Marie")
                .lastName("Curie").gender("F").phoneNumber("0600000000")
                .dateOfBirth(new GregorianCalendar(1980, Calendar.JUNE, 1).getTime())
                .build();
        entityManager.persist(testPatient);
    }
    @Test
    void testFindCompleteHistoryByPatientId() {
        for (PatientHistoryDao dao : daoImplementations) {
            PatientHistory history = PatientHistory.builder().patient(testPatient).visitDate(new Date())
                    .diagnosis("Covid-19").symptoms("fièvre").build();
            Bill bill = Bill.builder().totalAmount(150.0)
                    .billDate(new Date()).patientHistory(history).build();
            PatientHistory history2 = PatientHistory.builder().patient(testPatient).visitDate(new Date())
                    .diagnosis("Fièvre jaune").symptoms("fièvre").build();
            Bill bill2 = Bill.builder().totalAmount(150.0)
                    .billDate(new Date()).patientHistory(history).build();

            history.addBill(bill);
            history2.addBill(bill2);

            entityManager.persist(history);
            entityManager.persist(bill);
            entityManager.persist(history2);
            entityManager.persist(bill2);
            entityManager.flush();

            List<PatientHistory> results = dao.findCompleteHistoryByPatientId(testPatient.getId());

            assertEquals(2,results.size());

            assertEquals(history,results.getFirst());
        }

    }

    @Test
    void testSearchByMultipleCriteria_shouldMatchDiagnosis() {
        for (PatientHistoryDao dao : daoImplementations) {
            PatientHistory history = PatientHistory.builder().patient(testPatient)
                    .visitDate(new GregorianCalendar(2025, Calendar.MARCH, 1).getTime())
                    .diagnosis("Pneumonie aiguë").symptoms("Toux, douleurs thoraciques").build();

            entityManager.persist(history);
            entityManager.flush();

            List<PatientHistory> results = dao.searchByMultipleCriteria(
                    "pneumo",
                    new GregorianCalendar(2025, Calendar.JANUARY, 1).getTime(),
                    new GregorianCalendar(2025, Calendar.DECEMBER, 31).getTime()
            );

            assertEquals(1,results.size());
            assertEquals(history, results.getFirst());
        }

    }

    @Test
    void testSearchByMultipleCriteria_shouldReturnEmpty() {
        for (PatientHistoryDao dao : daoImplementations) {
            List<PatientHistory> results = dao.searchByMultipleCriteria(
                    "inexistant",
                    new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime(),
                    new GregorianCalendar(2020, Calendar.DECEMBER, 31).getTime()
            );

            assertTrue(results.isEmpty());
        }

    }
}
