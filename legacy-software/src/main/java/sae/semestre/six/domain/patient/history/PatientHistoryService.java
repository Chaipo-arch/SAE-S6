package sae.semestre.six.domain.patient.history;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sae.semestre.six.domain.patient.Patient;
import sae.semestre.six.domain.patient.PatientDao;

import java.util.Date;
import java.util.List;

@Service
public class PatientHistoryService {

    private final PatientHistoryDao patientHistoryDao;
    private final PatientDao patientDao;

    @Autowired
    public PatientHistoryService(PatientHistoryDao patientHistoryDao, PatientDao patientDao) {
        this.patientHistoryDao = patientHistoryDao;
        this.patientDao = patientDao;
    }

    public List<PatientHistory> searchHistory(String keyword, Date startDate, Date endDate) {
        return patientHistoryDao.searchByMultipleCriteria(keyword, startDate, endDate);
    }

    public void addHistoryEntry(Long idHistoryPatient, Long idTreatment, Long idBill,
                                Long idLabResults, Long idAppointment, Long idPrescription) {

        PatientHistory patientHistory = patientHistoryDao.findById(idHistoryPatient);
        HistoryEntry historyEntry = patientHistoryDao.getHistoryEntryData(
                idPrescription, idTreatment, idLabResults, idBill, idAppointment);
        patientHistory.addHistoryEntry(historyEntry);
    }

    public PatientSummary getPatientSummary(Long patientId) {
        List<PatientHistory> histories = patientHistoryDao.findCompleteHistoryByPatientId(patientId);
        return new PatientSummary(
                histories.size(),
                histories.stream().mapToDouble(PatientHistory::getTotalBilledAmount).sum()
        );
    }

    @Transactional
    public void createPatientHistory(Long idPatient, PatientHistoryInformation info) {
        Patient patient = patientDao.findById(idPatient);
        PatientHistory history = info.toPatientHistory(patient);
        patientHistoryDao.save(history);
    }
}