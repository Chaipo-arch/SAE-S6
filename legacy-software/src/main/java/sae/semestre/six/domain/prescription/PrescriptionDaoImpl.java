package sae.semestre.six.domain.prescription;

import sae.semestre.six.dao.AbstractHibernateDao;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Implementation of the PrescriptionDao interface for accessing prescription data using Hibernate.
 */
@Repository
public class PrescriptionDaoImpl extends AbstractHibernateDao<Prescription, Long> implements PrescriptionDao {
    /**
     * Retrieves all prescriptions for a specific patient by their ID.
     *
     * @param patientId The ID of the patient
     * @return List of Prescription entities for the patient
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<Prescription> findByPatientId(Long patientId) {
        return getEntityManager()
                .createQuery("FROM Prescription WHERE patient.id = :patientId")
                .setParameter("patientId", patientId)
                .getResultList();
    }

    /**
     * Finds a prescription by its prescription number.
     *
     * @param prescriptionId The prescription number
     * @return The Prescription entity, or null if not found
     */
    @Override
    public Prescription findByPrescriptionId(String prescriptionId) {
        return getEntityManager()
                .createQuery("FROM Prescription WHERE prescriptionNumber = :prescriptionId", Prescription.class)
                .setParameter("prescriptionId", prescriptionId)
                .getSingleResult();
    }

    /**
     * Gets the next available prescription ID (auto-incremented).
     *
     * @return The next prescription ID as an integer
     */
    @Override
    public int getNextId() {
        Object singleResult = getEntityManager()
                .createQuery("SELECT MAX(p.id) FROM Prescription p")
                .getSingleResult();
        if (singleResult == null) {
            return 1; // If no prescriptions exist, start with ID 1
        } else if (singleResult instanceof Number) {
            return ((Number) singleResult).intValue() + 1;
        } else {
            throw new IllegalStateException("Unexpected result type: " + singleResult.getClass());
        }
    }
}

