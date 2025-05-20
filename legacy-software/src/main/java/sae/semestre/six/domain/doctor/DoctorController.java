package sae.semestre.six.domain.doctor;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    public List<DoctorDTO> getAllDoctors() {
        return doctorDao.findAll().stream()
                .map(DoctorDTO::new)
                .collect(Collectors.toList());

    }

    // Endpoint pour rechercher un docteur par son numéro unique
    @GetMapping("/number/{doctorNumber}")
    public ResponseEntity<DoctorDTO> getDoctorByNumber(@PathVariable String doctorNumber) {
        return doctorDao.findByDoctorNumber(doctorNumber)
                .map(doctor -> new DoctorDTO(doctor))
                .map(dto -> ResponseEntity.ok(dto))
                .orElse(ResponseEntity.notFound().build());
    }


    // Endpoint pour rechercher des docteurs par spécialisation
    @GetMapping("/specialization/{specialization}")
    public List<DoctorDTO> getDoctorsBySpecialization(@PathVariable String specialization) {
        return doctorDao.findBySpecialization(specialization).stream()
                .map(doctor -> new DoctorDTO(doctor))
                .collect(Collectors.toList());
    }


    // Endpoint pour rechercher des docteurs par département
    @GetMapping("/department/{department}")
    public List<DoctorDTO> getDoctorsByDepartment(@PathVariable String department) {
        return doctorDao.findByDepartment(department).stream()
                .map(doctor -> new DoctorDTO(doctor))
                .collect(Collectors.toList());
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