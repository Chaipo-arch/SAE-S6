package sae.semestre.six.domain.doctor;

import jakarta.transaction.Transactional;
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
public class DoctorController {

    private final DoctorDao doctorDao;

    private final DoctorService doctorService;

    @Autowired
    public DoctorController(DoctorDao doctorDao, DoctorService doctorService) {
        this.doctorDao = doctorDao;
        this.doctorService = doctorService;
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
        Optional<DoctorDTO> doctorDTO = doctorService.getDoctorByNumber(doctorNumber);
        if(doctorDTO.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(doctorDTO.get());
    }


    // Endpoint pour rechercher des docteurs par spécialisation
    @GetMapping("/specialization/{specialization}")
    public ResponseEntity<List<DoctorDTO>> getDoctorsBySpecialization(@PathVariable String specialization) {
        return ResponseEntity.ok(doctorService.getDoctorsBySpecialization(specialization));
    }


    // Endpoint pour rechercher des docteurs par département
    @GetMapping("/department/{department}")
    public List<DoctorDTO> getDoctorsByDepartment(@PathVariable String department) {
        return doctorService.getDoctorsByDepartment(department);
    }


    // Endpoint pour ajouter un nouveau docteur ou mettre à jour un docteur existant
    @PostMapping
    public ResponseEntity<?> saveDoctor(@Valid @RequestBody Doctor doctor) {
        doctorService.createDoctor(doctor);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(doctor);
    }

    // Endpoint pour supprimer un docteur par son identifiant
    @Transactional
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDoctor(@PathVariable Long id) {
        doctorDao.deleteById(id); // Méthode delete de GenericDao
        return ResponseEntity
                .status(HttpStatus.OK)
                .body("Doctor with ID " + id + " deleted successfully");
    }
}