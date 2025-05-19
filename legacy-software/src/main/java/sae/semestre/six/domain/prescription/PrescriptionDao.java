package sae.semestre.six.domain.prescription;

import sae.semestre.six.dao.GenericDao;

import java.util.List;

/**
 * DAO interface for prescription-related database operations.
 */
public interface PrescriptionDao extends GenericDao<Prescription, Long> {
    /**
     * Retrieves all prescriptions for a specific patient by their ID.
     *
     * @param patientId The ID of the patient
     * @return List of Prescription entities for the patient
     */
    List<Prescription> findByPatientId(Long patientId);

    /**
     * Finds a prescription by its prescription number.
     *
     * @param prescriptionId The prescription number
     * @return The Prescription entity, or null if not found
     */
    Prescription findByPrescriptionId(String prescriptionId);

    /**
     * Gets the next available prescription ID (auto-incremented).
     *
     * @return The next prescription ID as an integer
     */
    int getNextId();
}

