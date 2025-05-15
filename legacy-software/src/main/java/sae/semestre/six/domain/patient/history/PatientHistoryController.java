package sae.semestre.six.domain.patient.history;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sae.semestre.six.domain.patient.Patient;
import sae.semestre.six.domain.patient.PatientDao;

import java.util.*;

@RestController
@RequestMapping("/patient-history")
public class PatientHistoryController {

    private final PatientHistoryDao patientHistoryDao;

    private final PatientDao patientDao;

    @Autowired
    public PatientHistoryController(PatientHistoryDao patientHistoryDao, PatientDao patientDao) {
        this.patientHistoryDao = patientHistoryDao;

        this.patientDao = patientDao;
    }
    
    @GetMapping("/search")
    public List<PatientHistory> searchHistory(
            @RequestParam String keyword,
            @RequestParam Date startDate,
            @RequestParam Date endDate) {
        
        
        List<PatientHistory> results = patientHistoryDao.searchByMultipleCriteria(
            keyword, startDate, endDate);

        return results;
    }

    public ResponseEntity<String> addHistoryEntry(Long idHistoryPatient, Long idTreatment, Long idLabResults, Long idAppointment,Long id) {
        PatientHistory patientHistory = patientHistoryDao.findById(idHistoryPatient);

        //patientHistory.addHistoryEntry();
        return ResponseEntity.badRequest().body("");
    }

    
    
    @GetMapping("/patient/{patientId}/summary")
    public PatientSummary getPatientSummary(@PathVariable Long patientId) {
        List<PatientHistory> histories = patientHistoryDao.findCompleteHistoryByPatientId(patientId);

        return new PatientSummary(histories.size(),histories.stream()
                .mapToDouble(PatientHistory::getTotalBilledAmount)
                .sum());
    }

    @Transactional
    @PostMapping
    public ResponseEntity<String> createPatientHistory(Long idPatient,PatientHistoryInformation patientHistoryInformation) {
        Patient patient = patientDao.findById(idPatient);
        PatientHistory patientHistory = patientHistoryInformation.toPatientHistory(patient);
        patientHistoryDao.save(patientHistory);
        return ResponseEntity.ok("L'historique du patient est créé.");
    }
} 