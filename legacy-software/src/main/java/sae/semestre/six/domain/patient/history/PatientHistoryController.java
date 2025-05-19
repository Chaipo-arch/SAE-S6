package sae.semestre.six.domain.patient.history;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * Contrôleur REST pour la gestion de l’historique médical des patients.
 * Fournit des endpoints pour rechercher des historiques, ajouter des entrées,
 * obtenir un résumé, et créer un nouvel historique.
 */
@RestController
@RequestMapping("/patient-history")
public class PatientHistoryController {

    private final PatientHistoryService patientHistoryService;

    @Autowired
    public PatientHistoryController(PatientHistoryService patientHistoryService) {
        this.patientHistoryService = patientHistoryService;
    }

    /**
     * Endpoint GET permettant de rechercher des historiques de patients selon plusieurs critères.
     *
     * @param keyword mot-clé à rechercher (ex. : nom, type de traitement…)
     * @param startDate date de début de la recherche
     * @param endDate date de fin de la recherche
     * @return liste des historiques correspondant aux critères
     */
    @GetMapping("/search")
    public List<PatientHistory> searchHistory(
            @RequestParam String keyword,
            @RequestParam Date startDate,
            @RequestParam Date endDate) {
        
        
        List<PatientHistory> results = patientHistoryService.searchHistory(
            keyword, startDate, endDate);

        return results;
    }

    /**
     * Endpoint POST permettant d'ajouter une entrée (traitement, facture, etc.) à un historique existant.
     *
     * @param idHistoryPatient identifiant de l’historique du patient
     * @param idTreatment identifiant du traitement
     * @param idBill identifiant de la facture
     * @param idLabResults identifiant des résultats de laboratoire
     * @param idAppointment identifiant du rendez-vous
     * @param idPrescription identifiant de l’ordonnance
     * @return réponse HTTP confirmant l’ajout de l’entrée
     */
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

    /**
     * Endpoint GET permettant de récupérer un résumé global pour un patient donné.
     *
     * @param patientId identifiant du patient
     * @return résumé contenant le nombre d’historiques et le total facturé
     */
    @GetMapping("/patient/{patientId}/summary")
    public PatientSummary getPatientSummary(@PathVariable Long patientId) {
        return patientHistoryService.getPatientSummary(patientId);
    }

    /**
     * Endpoint POST permettant de créer un nouvel historique pour un patient.
     *
     * @param idPatient identifiant du patient concerné
     * @param patientHistoryInformation données nécessaires à la création de l’historique
     * @return réponse HTTP confirmant la création de l’historique
     */
    @Transactional
    @PostMapping
    public ResponseEntity<String> createPatientHistory(@RequestParam Long idPatient,@RequestBody PatientHistoryInformation patientHistoryInformation) {
        patientHistoryService.createPatientHistory(idPatient,patientHistoryInformation);
        return ResponseEntity.ok("L'historique du patient est créé.");
    }

} 