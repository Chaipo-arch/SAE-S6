package sae.semestre.six.domain.doctor;

import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import sae.semestre.six.dao.AbstractHibernateDao;

import java.util.List;
import java.util.Optional;

@Repository
public class DoctorDaoImpl extends AbstractHibernateDao<Doctor, Long> implements DoctorDao {

    @Override
    public Optional<Doctor> findByDoctorNumber(String doctorNumber) {
        TypedQuery<Doctor> query = getEntityManager()
                .createQuery(
                        "SELECT d FROM Doctor d WHERE d.doctorNumber = :doctorNumber",
                        Doctor.class
                )
                .setParameter("doctorNumber", doctorNumber);

        try {
            Doctor doctor = query.getSingleResult();
            return Optional.of(doctor);
        } catch (jakarta.persistence.NoResultException ex) {
            // Aucun docteur trouvé pour ce numéro → Optional vide
            return Optional.empty();
        }
    }

    @Override
    public List<Doctor> findBySpecialization(String specialization) {
        TypedQuery<Doctor> query = getEntityManager()
                .createQuery("SELECT d FROM Doctor d WHERE d.specialization = :specialization",Doctor.class)
                .setParameter("specialization", specialization);
        return query.getResultList();
    }

    @Override
    public List<Doctor> findByDepartment(String department) {
        TypedQuery<Doctor> query = getEntityManager()
                .createQuery("SELECT d FROM Doctor d WHERE d.department = :department",Doctor.class)
                .setParameter("department", department);
        return query.getResultList();
    }
}