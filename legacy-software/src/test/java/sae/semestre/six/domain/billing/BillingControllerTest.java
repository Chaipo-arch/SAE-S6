package sae.semestre.six.domain.billing;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import sae.semestre.six.domain.billing.medical_acts.MedicalAct;
import sae.semestre.six.domain.billing.medical_acts.MedicalActDaoImpl;
import sae.semestre.six.domain.doctor.Doctor;
import sae.semestre.six.domain.doctor.DoctorDaoImpl;
import sae.semestre.six.domain.patient.Patient;
import sae.semestre.six.domain.patient.PatientDaoImpl;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@SpringBootTest
@TestPropertySource(properties = "BILLING_HASH_SECRET=Secret")
@RequiredArgsConstructor
class BillingControllerTest {
    @Autowired
    private BillingController billingController;
    @Autowired
    private Environment env;

    @MockitoSpyBean
    private BillingService billingService;

    @MockitoBean
    private MedicalActDaoImpl medicalActDao;
    @MockitoBean
    private BillDaoImpl billDao;
    @MockitoBean
    private PatientDaoImpl patientDao;
    @MockitoBean
    private DoctorDaoImpl doctorDao;

    private final static String TREATMENT_NAME = "CONSULTATION";
    private final static double TREATMENT_PRICE = 50.0;

    @BeforeEach
    public void setup() {
        MedicalAct medicalAct = MedicalAct.builder()
                .name(TREATMENT_NAME)
                .price(TREATMENT_PRICE)
                .build();
        when(medicalActDao.findByName(TREATMENT_NAME)).thenReturn(medicalAct);
    }

    @Test
    @DisplayName("Total revenue should be positive or zero")
    public void testGetTotalRevenue() {
        ResponseEntity<String> responseEntity = billingController.getTotalRevenue();
        assertSuccessAndBodyNotNull(responseEntity);

        String body = responseEntity.getBody();
        double total = Double.parseDouble(body.replace("Total Revenue: $", ""));
        assertTrue(total >= 0);
    }

    @Test
    @DisplayName("Creation of a bill should create new file")
    public void testProcessBill() {
        List<String> childrenBefore = getNamesOfBillFiles();

        // Mock patient fetching
        Patient patient = new Patient();
        patient.setId(1L);
        when(patientDao.findById(1L)).thenReturn(patient);

        // Mock doctor fetching
        Doctor doctor = new Doctor();
        doctor.setId(1L);
        when(doctorDao.findById(1L)).thenReturn(doctor);

        ResponseEntity<String> responseEntity = billingController.processBill(
                "1",
                "1",
                new String[]{TREATMENT_NAME});

        // Check operation success
        assertSuccessAndBodyNotNull(responseEntity);
        String responseBody = responseEntity.getBody();
        assertTrue(responseBody.contains("successfully"));

        // Check that file was created
        List<String> childrenAfter = getNamesOfBillFiles();
        childrenAfter.removeAll(childrenBefore);
        assertEquals(1, childrenAfter.size());

        // Check file exists and has contents
        File f = new File(getBillsFolder(), childrenAfter.getFirst());
        assertTrue(f.exists());
        assertTrue(f.length() > 0);
    }

    @Test
    @DisplayName("Insurance coverage should be equal to bill amount")
    public void testCalculateInsurance() {
        ResponseEntity<String> responseEntity = billingController.calculateInsurance(1000.0);
        String responseBody = responseEntity.getBody();
        assertNotNull(responseEntity);
        assertSuccessAndBodyNotNull(responseEntity);

        String responseValue = responseBody.replace("Insurance coverage: $", "");
        double result = Double.parseDouble(responseValue);
//        assertEquals(700.0, result, 0.01);
        assertEquals(1000.0, result, 0.01);
    }

    @Test
    @DisplayName("Updating prices shouldn't throw an error")
    public void testUpdatePrice() {
        final double newPrice = 50.0;

        // Action
        ResponseEntity<String> updateResponseEntity = billingController.updatePrice(TREATMENT_NAME, newPrice);
        // Assertions
        Assertions.assertTrue(updateResponseEntity.getStatusCode().is2xxSuccessful());
    }

    @Test
    @DisplayName("Creating prices shouldn't throw an error")
    public void testAddPrice() {
        // Action
        ResponseEntity<String> createResponseEntity = billingController.setPrice(TREATMENT_NAME, TREATMENT_PRICE);
        // Assertions
        Assertions.assertTrue(createResponseEntity.getStatusCode().is2xxSuccessful());
    }

    @Test
    @DisplayName("Integrity check should return false if integrity is compromised")
    public void textCheckIntegrityCompromised() {
        Bill bill = new Bill();
        bill.setHash("0dfcdcc1abb575758da8d0847f7fc1b2464aa19d60505d05ffd06f85094f0cf2");
        bill.setHashSalt("d231eef90efa71e2da81a35d07ac150a");

        when(billDao.findByBillNumber("TEST")).thenReturn(bill);
        // Using "do"-syntax with a spy (instead of a mock)
        doReturn("INCORRECT_BILL_FILE_CONTENTS")
                .when(billingService).getBillFileContents(bill);

        // Action
        ResponseEntity<Boolean> createResponseEntity = billingController.checkBillIntegrity("TEST");

        // Assertions
        assertSuccessAndBodyNotNull(createResponseEntity);
        Assertions.assertEquals(false, createResponseEntity.getBody());
    }

    @Test
    @DisplayName("Integrity check should return true if integrity is preserved")
    public void textCheckIntegrityPreserved() {

        Bill bill = new Bill();
        bill.setHash("0dfcdcc1abb575758da8d0847f7fc1b2464aa19d60505d05ffd06f85094f0cf2");
        bill.setHashSalt("d231eef90efa71e2da81a35d07ac150a");

        when(billDao.findByBillNumber("TEST")).thenReturn(bill);
        // Using "do"-syntax with a spy (instead of a mock)
        doReturn("BILL_FILE_CONTENTS")
                .when(billingService).getBillFileContents(bill);

        // Action
        ResponseEntity<Boolean> createResponseEntity = billingController.checkBillIntegrity("TEST");
        assertSuccessAndBodyNotNull(createResponseEntity);

        // Assertions
        Assertions.assertEquals(true, createResponseEntity.getBody());
    }

    /**
     * Vérifie que le code de retour correspond à une réussite et que le corps de la réponse est
     * non-null
     *
     * @param responseEntity l'entité de réponse HTTP à tester
     */
    private void assertSuccessAndBodyNotNull(ResponseEntity<?> responseEntity) {
        assertTrue(responseEntity.getStatusCode().is2xxSuccessful());
        assertNotNull(responseEntity.getBody());
    }

    /**
     * @return le dossier contenant les fichiers de factures
     */
    private File getBillsFolder() {
        final String FOLDER_NAME = env.getProperty("sae.semestre.six.files.billing");
        assertNotNull(FOLDER_NAME);
        return new File(FOLDER_NAME);
    }

    /**
     * @return les noms des fichiers de factures dans le dossier correspondant
     */
    private List<String> getNamesOfBillFiles() {
        String[] contentsList = Objects.requireNonNull(getBillsFolder().list());
        return new ArrayList<>(Arrays.asList(contentsList));
    }
}
