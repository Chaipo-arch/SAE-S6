package sae.semestre.six.domain.patient.history;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sae.semestre.six.domain.appointment.Appointment;
import sae.semestre.six.domain.patient.Patient;

@RestController
@RequestMapping("/lab-result")
public class LabResultController {


    private final LabResultDao labResultDao;

    public LabResultController(LabResultDao labResultDao) {
        this.labResultDao = labResultDao;
    }

    @PostMapping
    public ResponseEntity<String> createLabResult(@RequestBody LabResultInformation labResultInformation) {
        LabResult labResult = labResultInformation.toLabResult();

        labResultDao.save(labResult);
        return ResponseEntity.ok("Lab result created");
    }


}
