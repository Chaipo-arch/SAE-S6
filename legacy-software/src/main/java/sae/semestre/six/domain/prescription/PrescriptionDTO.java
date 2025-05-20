package sae.semestre.six.domain.prescription;

import sae.semestre.six.domain.inventory.Inventory;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Data Transfer Object (DTO) for Prescription entity.
 * Encapsulates prescription details for API communication.
 */
public record PrescriptionDTO(
        String prescriptionNumber,
        String patientId,
        List<String> medicineCodes,
        String notes,
        Double totalCost
) {
    /**
     * Creates a PrescriptionDTO from a Prescription entity.
     *
     * @param prescription The Prescription entity
     * @return The corresponding PrescriptionDTO
     */
    public static PrescriptionDTO fromEntity(Prescription prescription) {
        return new PrescriptionDTO(
                prescription.getPrescriptionNumber(),
                prescription.getPatient() != null ? String.valueOf(prescription.getPatient().getId()) : null,
                prescription.getMedicines() != null
                        ? prescription.getMedicines().stream().map(Inventory::getItemCode).collect(Collectors.toList())
                        : null,
                prescription.getNotes(),
                prescription.getTotalCost()
        );
    }
}
