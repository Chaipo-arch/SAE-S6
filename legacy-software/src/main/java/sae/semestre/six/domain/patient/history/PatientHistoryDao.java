package sae.semestre.six.domain.patient.history;

import sae.semestre.six.dao.GenericDao;

import java.util.List;
import java.util.Date;

public interface PatientHistoryDao extends GenericDao<PatientHistory, Long> {
    List<PatientHistory> findCompleteHistoryByPatientId(Long patientId);
    List<PatientHistory> searchByMultipleCriteria(String keyword, Date startDate, Date endDate);
    HistoryEntry getHistoryEntryData(Long prescriptionId, Long treatmentId, Long labResultId, Long billId, Long appointmentId);
    PatientHistory findForPatient(Long idPatient);
}