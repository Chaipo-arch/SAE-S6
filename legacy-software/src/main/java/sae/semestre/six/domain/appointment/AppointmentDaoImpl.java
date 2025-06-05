package sae.semestre.six.domain.appointment;

import jakarta.persistence.TypedQuery;
import sae.semestre.six.dao.AbstractHibernateDao;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class AppointmentDaoImpl extends AbstractHibernateDao<Appointment, Long> implements AppointmentDao {
    
    @Override
    public List<Appointment> findByPatientId(Long patientId) {
        TypedQuery<Appointment> query = getEntityManager()
                .createQuery("FROM Appointment WHERE patient.id = :patientId" , Appointment.class)
                .setParameter("patientId", patientId);
        return query.getResultList();
    }
    
    @Override
    public List<Appointment> findByDoctorId(Long doctorId) {
        TypedQuery<Appointment> query = getEntityManager()
                .createQuery("FROM Appointment WHERE doctor.id = :doctorId" , Appointment.class)
                .setParameter("doctorId", doctorId);
        return query.getResultList();
    }

    @Override
    public List<Appointment> findByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        TypedQuery<Appointment> query = getEntityManager()
                .createQuery("FROM Appointment WHERE appointmentDate BETWEEN :startDate AND :endDate", Appointment.class)
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate);
        return query.getResultList();
    }

} 