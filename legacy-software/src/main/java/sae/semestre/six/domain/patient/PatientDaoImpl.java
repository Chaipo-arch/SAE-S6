package sae.semestre.six.domain.patient;

import sae.semestre.six.dao.AbstractHibernateDao;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class PatientDaoImpl extends AbstractHibernateDao<Patient, Long> implements PatientDao {
    
    @Override
    public Patient findByPatientNumber(String patientNumber) {
        return getEntityManager()
                .createQuery("FROM Patient WHERE patientNumber = :patientNumber",Patient.class)
                .setParameter("patientNumber", patientNumber)
                .getSingleResult();
    }
    
    @Override
    public List<Patient> findByLastName(String lastName) {
        return getEntityManager()
                .createQuery("FROM Patient WHERE lastName LIKE :lastName",Patient.class)
                .setParameter("lastName", lastName + "%")
                .getResultList();
    }
} 