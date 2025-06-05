package sae.semestre.six.domain.patient;

import sae.semestre.six.dao.GenericDao;

import java.util.List;

public interface PatientDao extends GenericDao<Patient, Long> {
    Patient findByPatientNumber(String patientNumber);
    List<Patient> findByLastName(String lastName);
} 