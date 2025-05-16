package sae.semestre.six.domain.patient.history;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/patient-history")
public class PatientHistoryController {

    private final PatientHistoryService patientHistoryService;

    @Autowired
    public PatientHistoryController(PatientHistoryService patientHistoryService) {
        this.patientHistoryService = patientHistoryService;
    }
    
    @GetMapping("/search")
    public List<PatientHistory> searchHistory(
            @RequestParam String keyword,
            @RequestParam Date startDate,
            @RequestParam Date endDate) {
        
        
        List<PatientHistory> results = patientHistoryService.searchHistory(
            keyword, startDate, endDate);

        return results;
    }

    @PostMapping("/entry")
    public ResponseEntity<String> addHistoryEntry(@RequestParam Long idHistoryPatient,
                                                  @RequestParam Long idTreatment,
                                                  @RequestParam Long idBill,
                                                  @RequestParam Long idLabResults,
                                                  @RequestParam Long idAppointment,
                                                  @RequestParam Long idPrescription) {
        patientHistoryService.addHistoryEntry(idHistoryPatient,idTreatment,idBill,idLabResults,idAppointment,idPrescription);
        return ResponseEntity.ok("Entrée d'historique ajoutée.");
    }
    
    @GetMapping("/patient/{patientId}/summary")
    public PatientSummary getPatientSummary(@PathVariable Long patientId) {
        return patientHistoryService.getPatientSummary(patientId);
    }

    @Transactional
    @PostMapping
    public ResponseEntity<String> createPatientHistory(@RequestParam Long idPatient,@RequestParam PatientHistoryInformation patientHistoryInformation) {
        patientHistoryService.createPatientHistory(idPatient,patientHistoryInformation);
        return ResponseEntity.ok("L'historique du patient est créé.");
    }

} 