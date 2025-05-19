package sae.semestre.six.domain.prescription;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public String addPrescription(@RequestBody PrescriptionDTO dto) {
        return prescriptionService.addPrescription(dto);
    }

    /**
     * Retrieves all prescriptions for a given patient.
     *
     * @param patientId The ID of the patient
     * @return List of PrescriptionDTOs for the patient
     */
    @GetMapping("/patient/{patientId}")
    public List<PrescriptionDTO> getPatientPrescriptions(@PathVariable String patientId) {
        return prescriptionService.getPatientPrescriptions(patientId);
    }

    /**
     * Gets the total cost of a prescription, including markup.
     *
     * @param prescriptionId The prescription number
     * @return The total cost with markup
     */
    @GetMapping("/cost/{prescriptionId}")
    public Double getPrescriptionCost(@PathVariable String prescriptionId) {
        return prescriptionService.getPrescriptionCost(prescriptionId);
    }
}
