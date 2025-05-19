package sae.semestre.six.domain.prescription;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sae.semestre.six.domain.inventory.Inventory;
import sae.semestre.six.domain.inventory.InventoryDao;
import sae.semestre.six.domain.patient.PatientDao;
import sae.semestre.six.domain.patient.Patient;
import sae.semestre.six.domain.billing.BillingService;
import sae.semestre.six.file.FileHandler;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Service class for handling prescription-related operations such as creating prescriptions,
 * retrieving patient prescriptions, and calculating prescription costs.
 */
@Service
public class PrescriptionService {

    private final FileHandler fileHandler;
    private final InventoryDao inventoryDao;
    private final BillingService billingService;
    private final PatientDao patientDao;
    private final PrescriptionDao prescriptionDao;
    private static final String AUDIT_FILE = "C:\\hospital\\prescriptions.log";

    /**
     * Constructs a PrescriptionService with required dependencies.
     *
     * @param fileHandler      File handler for audit logging
     * @param inventoryDao     DAO for inventory operations
     * @param billingService   Service for billing operations
     * @param patientDao       DAO for patient operations
     * @param prescriptionDao  DAO for prescription operations
     */
    @Autowired
    public PrescriptionService(FileHandler fileHandler,
                              InventoryDao inventoryDao,
                              BillingService billingService,
                              PatientDao patientDao,
                              PrescriptionDao prescriptionDao) {
        this.fileHandler = fileHandler;
        this.inventoryDao = inventoryDao;
        this.billingService = billingService;
        this.patientDao = patientDao;
        this.prescriptionDao = prescriptionDao;
    }

    /**
     * Adds a new prescription for a patient, updates inventory, logs the action, and processes billing.
     *
     * @param dto Data transfer object containing prescription details
     * @return Status message indicating success or failure
     */
    @Transactional
    public String addPrescription(PrescriptionDTO dto) {
        try {
            int prescriptionCounter = prescriptionDao.getNextId();
            // Get all the inventories entries and calculate the cost
            List<Inventory> inventories = new ArrayList<>();
            double cost = 0;
            for (String code : dto.medicineCodes()) {
                Inventory inventory;
                try {
                    inventory = inventoryDao.findByItemCode(code);
                } catch (Exception e) {
                    return "Failed: inventory not found for code " + code;
                }
                cost += inventory.getUnitPrice();
                inventories.add(inventory);
            }
            Patient patient;
            try {
                patient = patientDao.findById(Long.parseLong(dto.patientId()));
                if (patient == null) {
                    return "Failed: patient not found for id " + dto.patientId();
                }
            } catch (Exception e) {
                return "Failed: patient not found for id " + dto.patientId();
            }
            String prescriptionId = "RX" + prescriptionCounter;
            Prescription prescription = new Prescription(prescriptionId, patient, inventories, dto.notes(), cost);
            prescriptionDao.save(prescription);
            fileHandler.writeToFile(AUDIT_FILE,
                    new Date() + " - " + prescriptionId + "\n");

            billingService.processBill(
                    dto.patientId(),
                    "SYSTEM",
                    new String[]{"PRESCRIPTION_" + prescriptionId}
            );

            for (Inventory inventory : inventories) {
                inventory.setQuantity(inventory.getQuantity() - 1);
                inventoryDao.update(inventory);
            }

            return "Prescription " + prescriptionId + " created and billed";
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            return "Failed: " + e;
        }
    }

    /**
     * Retrieves all prescriptions for a given patient.
     *
     * @param patientId The ID of the patient
     * @return List of PrescriptionDTOs for the patient
     */
    @Transactional(readOnly = true)
    public List<PrescriptionDTO> getPatientPrescriptions(String patientId) {
        return prescriptionDao.findByPatientId(Long.parseLong(patientId))
                .stream()
                .map(PrescriptionDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Calculates the total cost of a prescription, including a 20% markup.
     *
     * @param prescriptionId The prescription number
     * @return The total cost with markup, or 0.0 if not found
     */
    public Double getPrescriptionCost(String prescriptionId) {
        Prescription prescription;
        try {
            prescription = prescriptionDao.findByPrescriptionId(prescriptionId);
        } catch (Exception e) {
            return 0.0;
        }
        if (prescription == null) return 0.0;
        double total = 0.0;
        List<Inventory> inventories = prescription.getMedicines();
        if (inventories != null) {
            for (Inventory inventory : inventories) {
                if (inventory != null) {
                    total += inventory.getUnitPrice();
                }
            }
        }
        return total * 1.2;
    }
}
