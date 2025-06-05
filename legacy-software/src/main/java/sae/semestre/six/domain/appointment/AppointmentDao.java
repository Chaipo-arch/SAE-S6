package sae.semestre.six.domain.appointment;

import org.springframework.stereotype.Repository;
import sae.semestre.six.dao.GenericDao;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentDao extends GenericDao<Appointment, Long> {
    List<Appointment> findByPatientId(Long patientId);
    List<Appointment> findByDoctorId(Long doctorId);
    List<Appointment> findByDateRange(LocalDateTime startDate, LocalDateTime endDate);
} 