package sae.semestre.six.domain.doctor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
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

}
