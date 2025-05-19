package sae.semestre.six.domain.patient.history;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import sae.semestre.six.domain.appointment.Appointment;
import sae.semestre.six.domain.billing.Bill;
import sae.semestre.six.domain.doctor.Doctor;
import sae.semestre.six.domain.patient.Patient;
import sae.semestre.six.domain.prescription.Prescription;

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
                .lastName("Curie").gender("F").phoneNumber("0600000000").email("e@e.com")
                .dateOfBirth(new GregorianCalendar(1980, Calendar.JUNE, 1).getTime())
                .build();
        entityManager.persist(testPatient);
    }
    @Test
    void testFindCompleteHistoryByPatientId() {
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
        for (PatientHistoryDao dao : daoImplementations) {
            List<PatientHistory> results = dao.findCompleteHistoryByPatientId(testPatient.getId());

            assertEquals(2,results.size(), "" + dao.getClass());

            assertTrue(history.equals(results.getFirst()) || history2.equals(results.getFirst()), "" + dao.getClass());
        }

    }

    @Test
    void testSearchByMultipleCriteria_shouldMatchDiagnosis() {
        PatientHistory history = PatientHistory.builder().patient(testPatient)
                .visitDate(new GregorianCalendar(2025, Calendar.MARCH, 1).getTime())
                .diagnosis("Pneumonie aiguë").symptoms("Toux, douleurs thoraciques").build();

        entityManager.persist(history);
        entityManager.flush();
        for (PatientHistoryDao dao : daoImplementations) {
            List<PatientHistory> results = dao.searchByMultipleCriteria(
                    "pneumo",
                    new GregorianCalendar(2025, Calendar.JANUARY, 1).getTime(),
                    new GregorianCalendar(2025, Calendar.DECEMBER, 31).getTime()
            );

            assertEquals(1,results.size(), "" + dao.getClass());
            assertEquals(history, results.getFirst(), "" + dao.getClass());
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

            assertTrue(results.isEmpty(), "" + dao.getClass());
        }

    }

    @Test
    void testFindDataForHistoryEntry() {
        PatientHistory patientHistory = new PatientHistoryInformation(new Date(),
                "Diagnosis","Symptoms","Notes").toPatientHistory(testPatient);
        entityManager.persist(patientHistory);
        Doctor doctor = Doctor.builder().doctorNumber("Doc011").firstName("e").lastName("l").email("email@gmail.com").build();
        Prescription prescription = Prescription.builder().prescriptionNumber("P011").patient(testPatient).build();
        Treatment treatment = Treatment.builder().name("TreatmentName").patientHistory(patientHistory).treatmentDate(new Date()).build();

        LabResult labResult = LabResult.builder().testName("Test").patientHistory(patientHistory).build();
        Appointment appointment = Appointment.builder().appointmentNumber("App011").doctor(doctor)
                .patient(testPatient).patientHistory(patientHistory).description("DescriptionApp")
                .status("CONFIRMÉ").appointmentDate(new Date()).build();
        Bill bill = Bill.builder().billNumber("Bil011")
                .patientHistory(patientHistory).patient(testPatient)
                .totalAmount(10.0).status("EN ATTENTE").createdDate(new Date()).build();
        entityManager.persist(patientHistory);
        entityManager.persist(doctor);
        entityManager.persist(appointment);
        entityManager.persist(treatment);
        entityManager.persist(prescription);
        entityManager.persist(bill);
        entityManager.persist(labResult);
        entityManager.flush();
        for(PatientHistoryDao dao : daoImplementations) {
            HistoryEntry historyEntry = dao.getHistoryEntryData(prescription.getId(),treatment.getId(),labResult.getId(),bill.getId(),appointment.getId());

            assertEquals("Bil011",historyEntry.bill().getBillNumber(), "La récupération du bill ne se fait pas correctement " + dao.getClass());
            assertEquals(patientHistory,historyEntry.labResult().getPatientHistory(), "La récupération du labResult ne se fait pas correctement " + dao.getClass());
            assertEquals("DescriptionApp",historyEntry.appointment().getDescription(), "La récupération du appointment ne se fait pas correctement " + dao.getClass());
            assertEquals("TreatmentName",historyEntry.treatment().getName(), "La récupération du treatment ne se fait pas correctement " + dao.getClass());
            assertEquals("P011",historyEntry.prescription().getPrescriptionNumber(), "La récupération de la prescription ne se fait pas correctement " + dao.getClass());

        }
    }

    @Test
    void testGetHistoryEntryDataWithWrongIds() {
        for(PatientHistoryDao dao : daoImplementations) {
            HistoryEntry historyEntry = dao.getHistoryEntryData(0L, 0L, 0L, 0L, 0L);

            assertNull(historyEntry.prescription());
            assertNull(historyEntry.treatment());
            assertNull(historyEntry.labResult());
            assertNull(historyEntry.bill());
            assertNull(historyEntry.appointment());

            Long longValue = null;
            historyEntry = dao.getHistoryEntryData(longValue,longValue,longValue,longValue,longValue);
            assertNull(historyEntry.prescription());
            assertNull(historyEntry.treatment());
            assertNull(historyEntry.labResult());
            assertNull(historyEntry.bill());
            assertNull(historyEntry.appointment());

        }


    }

}
