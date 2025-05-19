package sae.semestre.six.domain.prescription;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import sae.semestre.six.domain.inventory.Inventory;
import sae.semestre.six.domain.inventory.InventoryDao;
import sae.semestre.six.domain.patient.Patient;
import sae.semestre.six.domain.patient.PatientDao;

import jakarta.transaction.Transactional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the PrescriptionController REST endpoints.
 * <p>
 * These tests cover adding prescriptions, error handling for unknown medicines and patients,
 * retrieving patient prescriptions, and calculating prescription costs.
 * <p>
 * The test class uses @SpringBootTest and @AutoConfigureMockMvc to run with a real application context
 * and MockMvc for HTTP request simulation. Data is rolled back after each test due to @Transactional.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class TestPrescriptionController {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private InventoryDao inventoryDao;

    @Autowired
    private PatientDao patientDao;

    private String patientId;

    /**
     * Sets up test data before each test. Creates a patient and two inventory items.
     */
    @BeforeEach
    public void setup() {
        // Create a patient for testing
        Patient patient = new Patient();
        patient.setFirstName("John");
        patient.setLastName("Doe");
        patient.setDateOfBirth(new java.util.Date());
        patient.setPatientNumber("PTEST001"); // Ensure not-null constraint is satisfied
        patientDao.save(patient);
        patientId = String.valueOf(patient.getId());

        // Create inventory items for testing
        Inventory med1 = new Inventory();
        med1.setItemCode("TESTING001");
        med1.setName("Paracetamol");
        med1.setQuantity(100);
        med1.setUnitPrice(2.5);
        med1.setReorderLevel(10);
        med1.setLastRestocked(new java.util.Date());
        inventoryDao.save(med1);

        Inventory med2 = new Inventory();
        med2.setItemCode("TESTING002");
        med2.setName("Ibuprofen");
        med2.setQuantity(50);
        med2.setUnitPrice(3.0);
        med2.setReorderLevel(5);
        med2.setLastRestocked(new java.util.Date());
        inventoryDao.save(med2);
    }

    /**
     * Tests successful addition of a prescription for a patient with valid medicines.
     */
    @Test
    @DisplayName("POST /prescriptions/add: nominal case")
    public void testAddPrescription_Success() throws Exception {
        String requestJson = """
            {
              "patientId": "%s",
              "medicineCodes": ["TESTING001", "TESTING002"],
              "notes": "Take after meals"
            }
            """.formatted(patientId);

        mockMvc.perform(post("/prescriptions/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("created and billed")));
    }

    /**
     * Tests error handling when adding a prescription with an unknown medicine code.
     */
    @Test
    @DisplayName("POST /prescriptions/add: error - unknown medicine")
    public void testAddPrescription_UnknownMedicine() throws Exception {
        String requestJson = """
            {
              "patientId": "%s",
              "medicineCodes": ["UNKNOWN"],
              "notes": "Test"
            }
            """.formatted(patientId);

        mockMvc.perform(post("/prescriptions/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Failed:")));
    }

    /**
     * Tests error handling when adding a prescription for an unknown patient.
     */
    @Test
    @DisplayName("POST /prescriptions/add: error - unknown patient")
    public void testAddPrescription_UnknownPatient() throws Exception {
        String requestJson = """
            {
              "patientId": "999999",
              "medicineCodes": ["TESTING001"],
              "notes": "Test"
            }
            """;

        mockMvc.perform(post("/prescriptions/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Failed:")));
    }

    /**
     * Tests retrieval of prescriptions for a patient with at least one prescription.
     */
    @Test
    @DisplayName("GET /prescriptions/patient/{patientId}: nominal case")
    public void testGetPatientPrescriptions_Success() throws Exception {
        // Add a prescription first
        String requestJson = """
            {
              "patientId": "%s",
              "medicineCodes": ["TESTING001"],
              "notes": "Morning"
            }
            """.formatted(patientId);

        mockMvc.perform(post("/prescriptions/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isOk());

        mockMvc.perform(get("/prescriptions/patient/" + patientId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].patientId").value(patientId))
                .andExpect(jsonPath("$[0].medicineCodes", hasItem("TESTING001")));
    }

    /**
     * Tests retrieval of prescriptions for a patient with no prescriptions.
     */
    @Test
    @DisplayName("GET /prescriptions/patient/{patientId}: no prescriptions")
    public void testGetPatientPrescriptions_Empty() throws Exception {
        // Use a new patient with no prescriptions
        Patient newPatient = new Patient();
        newPatient.setFirstName("Jane");
        newPatient.setLastName("Smith");
        newPatient.setDateOfBirth(new java.util.Date());
        newPatient.setPatientNumber("PTEST002"); // Ensure not-null constraint is satisfied
        patientDao.save(newPatient);
        String newPatientId = String.valueOf(newPatient.getId());

        mockMvc.perform(get("/prescriptions/patient/" + newPatientId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    /**
     * Tests calculation of prescription cost for a valid prescription.
     */
    @Test
    @DisplayName("GET /prescriptions/cost/{prescriptionId}: nominal case")
    public void testGetPrescriptionCost_Success() throws Exception {
        // Add a prescription first
        String requestJson = """
            {
              "patientId": "%s",
              "medicineCodes": ["TESTING001", "TESTING002"],
              "notes": "Evening"
            }
            """.formatted(patientId);

        String response = mockMvc.perform(post("/prescriptions/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        // Extract prescription number from response
        String prescriptionNumber = "RX" + response.replaceAll("[A-Za-z ]", "");

        mockMvc.perform(get("/prescriptions/cost/" + prescriptionNumber))
                .andExpect(status().isOk())
                .andExpect(content().string(not("0.0")));
    }
}
