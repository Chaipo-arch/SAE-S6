package sae.semestre.six.domain.prescription;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST controller for managing prescription-related endpoints.
 */
@RestController
@AllArgsConstructor
@RequestMapping("/prescriptions")
public class PrescriptionController {

    private final PrescriptionService prescriptionService;

    /**
     * Adds a new prescription for a patient.
     *
     * @param dto The prescription data transfer object
     * @return Status message indicating success or failure
     */
    @PostMapping("/add")
    public ResponseEntity<?> addPrescription(@RequestBody PrescriptionDTO dto) {
        try {
            return new ResponseEntity<>(prescriptionService.addPrescription(dto), HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Retrieves all prescriptions for a given patient.
     *
     * @param patientId The ID of the patient
     * @return List of PrescriptionDTOs for the patient
     */
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<?> getPatientPrescriptions(@PathVariable String patientId) {
        return new ResponseEntity<>(prescriptionService.getPatientPrescriptions(patientId), HttpStatus.OK);
    }

    /**
     * Gets the total cost of a prescription, including markup.
     *
     * @param prescriptionId The prescription number
     * @return The total cost with markup
     */
    @GetMapping("/cost/{prescriptionId}")
    public ResponseEntity<?> getPrescriptionCost(@PathVariable String prescriptionId) {
        Prescription prescription;
        try {
            prescription = prescriptionService.getPrescription(prescriptionId);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
        if (prescription != null) {
            double total = prescription.getCostWithVat();
            return ResponseEntity.ok(Map.of("totalCost", total));
        } else {
            throw new IllegalArgumentException("Prescription not found");
        }
    }
}
