package sae.semestre.six.domain.doctor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import sae.semestre.six.exception.InvalidDataException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DoctorService {

    private final DoctorDao doctorDao;


    @Autowired
    public DoctorService(DoctorDao doctorDao) {
        this.doctorDao = doctorDao;
    }

    public Doctor createDoctor(Doctor doctor) {
        doctorDao.save(doctor);
        return doctor;
    }

    public List<DoctorDTO> getDoctorsBySpecialization(String specialization) {
        return doctorDao.findBySpecialization(specialization).stream()
                .map(DoctorDTO::new)
                .collect(Collectors.toList());
    }

    public void deleteDoctor(Long id)  {
        Doctor doctor = doctorDao.findById(id);
        if(doctor == null) {
            throw new InvalidDataException("The id does not correspond to a doctor");
        }
        
        doctorDao.deleteById(id);
    }

    public Optional<DoctorDTO> getDoctorByNumber(String doctorNumber) {
        return doctorDao.findByDoctorNumber(doctorNumber)
                .map(DoctorDTO::new);
    }

    public List<DoctorDTO> getDoctorsByDepartment(String department) {
        return doctorDao.findByDepartment(department).stream()
                .map(DoctorDTO::new)
                .collect(Collectors.toList());
    }


}
