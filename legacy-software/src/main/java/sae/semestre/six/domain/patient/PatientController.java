package sae.semestre.six.domain.patient;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sae.semestre.six.domain.appointment.Appointment;

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
            return ResponseEntity.badRequest().body("The given patient number can't not be use because another patient with this patient number exist.");
        }

        patientDao.save(patient);
        return ResponseEntity.ok("Patient created");
    }

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

    @Transactional
    @PostMapping("update/")
    public ResponseEntity<String> updatePatient(@RequestParam("id") Long id, @RequestBody PatientInformation patientInformation) {
        Patient patient = patientDao.findById(id);
        if(patient == null) {
            return ResponseEntity.badRequest( ).body("No patient has the id " + id);
        }
        patientDao.update(patientInformation.toPatient(id));
        return ResponseEntity.ok("Patient updated");
    }

    @GetMapping
    public ResponseEntity<List<Patient>> getPatients() {
        return ResponseEntity.ok(patientDao.findAll());
    }

    @GetMapping("s")
    public ResponseEntity<Patient> getPatient(@RequestParam Long id) {
        Patient patient = patientDao.findById(id);
        return ResponseEntity.ok(patient);
    }

}