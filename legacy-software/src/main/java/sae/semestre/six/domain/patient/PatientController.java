package sae.semestre.six.domain.patient;

import jakarta.persistence.NoResultException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/patient")
public class PatientController {

    private final PatientDao patientDao;

    @Autowired
    public PatientController(PatientDao patientDao) {
        this.patientDao = patientDao;
    }

    @Transactional
    @PostMapping
    public ResponseEntity<String> createPatient(@RequestBody PatientInformation patientInformation) {
        Patient patient = patientInformation.toPatient(null);

        if(patientDao.findByPatientNumber(patient.getPatientNumber()) != null) {
            return ResponseEntity.badRequest().body("Un numéro de patient ne peut pas être attitré à deux patients.");
        }

        patientDao.save(patient);
        return ResponseEntity.ok("Patient créé");
    }

    @Transactional
    @DeleteMapping
    public ResponseEntity<String> deletePatient(@RequestParam("id") Long id) {
        Patient patient = patientDao.findById(id);
        if(patient == null) {
            return ResponseEntity.badRequest( ).body("Aucun patient ne posséde l'id " + id);
        }
        patientDao.delete(patient);
        return ResponseEntity.ok("Patient supprimé");
    }

    @Transactional
    @PostMapping("update/")
    public ResponseEntity<String> updatePatient(@RequestParam("id") Long id, @RequestBody PatientInformation patientInformation) {
        Patient patient = patientDao.findById(id);
        if(patient == null) {
            return ResponseEntity.badRequest( ).body("Aucun patient ne posséde l'id " + id);
        }
        patientDao.update(patientInformation.toPatient(id));
        return ResponseEntity.ok("Patient modifié");
    }

    @GetMapping
    public ResponseEntity<List<Patient>> getPatients() {
        return ResponseEntity.ok(patientDao.findAll());
    }

}