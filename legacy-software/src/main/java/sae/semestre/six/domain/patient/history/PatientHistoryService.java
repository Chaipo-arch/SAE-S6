package sae.semestre.six.domain.patient.history;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sae.semestre.six.domain.patient.Patient;
import sae.semestre.six.domain.patient.PatientDao;
import sae.semestre.six.exception.InvalidDataException;

import java.util.Date;
import java.util.List;

/**
 * Service de gestion de l'historique des patients.
 * Fournit des méthodes pour rechercher, créer et enrichir l'historique médical,
 * ainsi que pour générer un résumé de patient.
 */
@Service
public class PatientHistoryService {

    private final PatientHistoryDao patientHistoryDao;
    private final PatientDao patientDao;

    /**
     * Constructeur injectant les dépendances nécessaires au service.
     *
     * @param patientHistoryDao DAO pour accéder aux historiques des patients.
     * @param patientDao DAO pour accéder aux données des patients.
     */
    @Autowired
    public PatientHistoryService(PatientHistoryDao patientHistoryDao, PatientDao patientDao) {
        this.patientHistoryDao = patientHistoryDao;
        this.patientDao = patientDao;
    }


    /**
     * Recherche les historiques de patients correspondant à un mot-clé et à une période donnée.
     *
     * @param keyword mot-clé pour filtrer les entrées (nom, traitement, etc.)
     * @param startDate date de début de la période
     * @param endDate date de fin de la période
     * @return liste des historiques correspondant aux critères
     */
    public List<PatientHistory> searchHistory(String keyword, Date startDate, Date endDate) {
        return patientHistoryDao.searchByMultipleCriteria(keyword, startDate, endDate);
    }

    /**
     * Ajoute une entrée (traitement, ordonnance, etc.) à un historique de patient existant.
     *
     * @param idHistoryPatient identifiant de l'historique du patient
     * @param idTreatment identifiant du traitement
     * @param idBill identifiant de la facture
     * @param idLabResults identifiant des résultats de laboratoire
     * @param idAppointment identifiant du rendez-vous
     * @param idPrescription identifiant de l'ordonnance
     * @throws InvalidDataException si l'historique du patient est introuvable
     */
    public void addHistoryEntry(Long idHistoryPatient, Long idTreatment, Long idBill,
                                Long idLabResults, Long idAppointment, Long idPrescription) {

        PatientHistory patientHistory = patientHistoryDao.findById(idHistoryPatient);
        if(patientHistory == null) {
            throw new InvalidDataException("The patient id is incorrect");
        }
        HistoryEntry historyEntry = patientHistoryDao.getHistoryEntryData(
                idPrescription, idTreatment, idLabResults, idBill, idAppointment);
        patientHistory.addHistoryEntry(historyEntry);
    }

    /**
     * Génère un résumé global pour un patient (nombre d’entrées et total facturé).
     *
     * @param patientId identifiant du patient
     * @return un objet résumant les informations clés (PatientSummary)
     */
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
        if(patient == null) {
            throw new InvalidDataException("The patient id is incorrect");
        }
        PatientHistory history = info.toPatientHistory(patient);
        patientHistoryDao.save(history);
    }
}