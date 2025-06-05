package sae.semestre.six.domain.prescription;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sae.semestre.six.domain.billing.BillingService;
import sae.semestre.six.domain.doctor.Doctor;
import sae.semestre.six.domain.doctor.DoctorDao;
import sae.semestre.six.domain.inventory.Inventory;
import sae.semestre.six.domain.inventory.InventoryDao;
import sae.semestre.six.domain.patient.Patient;
import sae.semestre.six.domain.patient.PatientDao;
import sae.semestre.six.file.FileHandler;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service class for handling prescription-related operations such as creating prescriptions,
 * retrieving patient prescriptions, and calculating prescription costs.
 */
@Service
@RequiredArgsConstructor
public class PrescriptionService {

    private final InventoryDao inventoryDao;
    private final PrescriptionDao prescriptionDao;
    private final PatientDao patientDao;
    private final DoctorDao doctorDao;
    private final FileHandler fileHandler;
    private final BillingService billingService;

    @Value("${sae.semestre.six.files.prescriptions}")
    private String AUDIT_FILE;

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

            // On crée la prescription
            String prescriptionId = "RX" + prescriptionCounter;
            Prescription prescription = new Prescription(prescriptionId, patient, inventories, dto.notes(), cost);
            prescriptionDao.save(prescription);

            // On renseigne la création dans le fichier d'audit
            fileHandler.writeToFile(AUDIT_FILE,
                    new Date() + " - " + prescriptionId + "\n");

            // On crée la facture correspondante
            Optional<Doctor> systemDoctor = doctorDao.findByDoctorNumber("SYSTEM");
            systemDoctor.ifPresent(doctor -> billingService.processBill(
                    patient,
                    doctor,
                    new String[]{Prescription.BILLABLE_PREFIX + prescriptionId}
            ));

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

    public Prescription getPrescription(String prescriptionId) {
        try {
            return prescriptionDao.findByPrescriptionId(prescriptionId);
        } catch (Exception e) {
            throw new IllegalArgumentException("Prescription not found");
        }
    }
}
