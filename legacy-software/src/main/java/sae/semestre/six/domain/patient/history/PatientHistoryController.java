package sae.semestre.six.domain.patient.history;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/patient-history")
public class PatientHistoryController {
    
    @Autowired
    private PatientHistoryDao patientHistoryDao;
    
    
    @GetMapping("/search")
    public List<PatientHistory> searchHistory(
            @RequestParam String keyword,
            @RequestParam Date startDate,
            @RequestParam Date endDate) {
        
        
        List<PatientHistory> results = patientHistoryDao.searchByMultipleCriteria(
            keyword, startDate, endDate);

        return results;
    }
    
    
    @GetMapping("/patient/{patientId}/summary")
    public PatientSummary getPatientSummary(@PathVariable Long patientId) {
        List<PatientHistory> histories = patientHistoryDao.findCompleteHistoryByPatientId(patientId);

        return new PatientSummary(histories.size(),histories.stream()
                .mapToDouble(PatientHistory::getTotalBilledAmount)
                .sum());
    }
} 