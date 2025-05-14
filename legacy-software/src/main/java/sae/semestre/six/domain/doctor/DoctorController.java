package sae.semestre.six.domain.doctor;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/doctor")
@CrossOrigin("*") // Ou spécifiez une origine spécifique
public class DoctorController {

    private final DoctorDao doctorDao;

    @Autowired
    public DoctorController(DoctorDao doctorDao) {
        this.doctorDao = doctorDao;
    }

    // Endpoint pour récupérer tous les docteurs
    @GetMapping
    public List<Doctor> getAllDoctors() {
        return doctorDao.findAll();
    }

    // Endpoint pour rechercher un docteur par son numéro unique
    @GetMapping("/number/{doctorNumber}")
    public Optional<Doctor> getDoctorByNumber(@PathVariable String doctorNumber) {
        return doctorDao.findByDoctorNumber(doctorNumber);
    }

    // Endpoint pour rechercher des docteurs par spécialisation
    @GetMapping("/specialization/{specialization}")
    public List<Doctor> getDoctorsBySpecialization(@PathVariable String specialization) {
        return doctorDao.findBySpecialization(specialization);
    }

    // Endpoint pour rechercher des docteurs par département
    @GetMapping("/department/{department}")
    public List<Doctor> getDoctorsByDepartment(@PathVariable String department) {
        return doctorDao.findByDepartment(department);
    }

    // Endpoint pour ajouter un nouveau docteur ou mettre à jour un docteur existant
    @PostMapping
    public ResponseEntity<?> saveDoctor(@Valid @RequestBody Doctor doctor) {
        doctorDao.save(doctor);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(doctor);
    }

    // Endpoint pour supprimer un docteur par son identifiant
    @DeleteMapping("/{id}")
    public void deleteDoctor(@PathVariable Long id) {
        doctorDao.deleteById(id); // Méthode delete de GenericDao
    }
}