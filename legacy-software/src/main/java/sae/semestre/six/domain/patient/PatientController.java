package sae.semestre.six.domain.patient;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sae.semestre.six.domain.appointment.Appointment;

import java.util.List;

/**
 * Contrôleur REST pour la gestion des patients.
 * Fournit des endpoints pour créer, modifier, supprimer et consulter des patients.
 */
@RestController
@RequestMapping("/patient")
public class PatientController {

    private final PatientDao patientDao;

    @Autowired
    public PatientController(PatientDao patientDao) {
        this.patientDao = patientDao;
    }

    /**
     * Endpoint POST pour créer un nouveau patient à partir des informations fournies.
     *
     * @param patientInformation DTO contenant les données du patient à créer
     * @return réponse HTTP indiquant si la création a réussi ou échoué (doublon possible)
     */
    @Transactional
    @PostMapping
    public ResponseEntity<String> createPatient(@RequestBody PatientInformation patientInformation) {
        Patient patient = patientInformation.toPatient(null);

        if(patientDao.findByPatientNumber(patient.getPatientNumber()) != null) {
            return ResponseEntity.badRequest().body("The given patient number can't not be use because another patient with this patient number exist.");
        }

        patientDao.save(patient);
        return ResponseEntity.ok("Patient created");
    }

    /**
     * Endpoint DELETE pour supprimer un patient existant à partir de son identifiant.
     *
     * @param id identifiant du patient à supprimer
     * @return réponse HTTP confirmant la suppression ou indiquant que le patient n’existe pas
     */
    @Transactional
    @DeleteMapping
    public ResponseEntity<String> deletePatient(@RequestParam("id") Long id) {
        Patient patient = patientDao.findById(id);
        if(patient == null) {
            return ResponseEntity.badRequest( ).body("No patient has the id " + id);
        }
        patientDao.delete(patient);
        return ResponseEntity.ok("Patient removed");
    }

    /**
     * Endpoint POST pour mettre à jour les informations d’un patient existant.
     *
     * @param id identifiant du patient à mettre à jour
     * @param patientInformation nouvelles données du patient
     * @return réponse HTTP confirmant la mise à jour ou signalant une erreur (patient non trouvé)
     */
    @Transactional
    @PostMapping("update/")
    public ResponseEntity<String> updatePatient(@RequestParam("id") Long id, @RequestBody PatientInformation patientInformation) {
        Patient patient = patientDao.findById(id);
        if(patient == null) {
            return ResponseEntity.badRequest( ).body("No patient has the id " + id);
        }
        patient.updateWith(patientInformation);
        patientDao.update(patient);
        return ResponseEntity.ok("Patient updated");
    }

    /**
     * Endpoint GET pour récupérer la liste complète de tous les patients enregistrés.
     *
     * @return liste de tous les patients
     */
    @GetMapping
    public ResponseEntity<List<Patient>> getPatients() {
        return ResponseEntity.ok(patientDao.findAll());
    }

    /**
     * Endpoint GET pour récupérer un patient spécifique via son identifiant.
     *
     * @param id identifiant du patient à consulter
     * @return objet Patient correspondant à l’identifiant fourni
     */
    @GetMapping("s")
    public ResponseEntity<Patient> getPatient(@RequestParam Long id) {
        Patient patient = patientDao.findById(id);
        return ResponseEntity.ok(patient);
    }

}