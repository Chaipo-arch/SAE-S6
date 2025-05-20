package sae.semestre.six.domain.prescription;

import sae.semestre.six.dao.GenericDao;

import java.util.List;

public interface PrescriptionDao extends GenericDao<Prescription, Long> {
    List<Prescription> findByPatientId(Long patientId);
    Prescription findByPrescriptionNumber(String prescriptionNumber);
    public int getNumberOfPrescriptions();
} 