package sae.semestre.six.domain.prescription;

import sae.semestre.six.dao.AbstractHibernateDao;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class PrescriptionDaoImpl extends AbstractHibernateDao<Prescription, Long> implements PrescriptionDao {
    
    @Override
    @SuppressWarnings("unchecked")
    public List<Prescription> findByPatientId(Long patientId) {
        return getEntityManager()
                .createQuery("FROM Prescription WHERE patient.id = :patientId")
                .setParameter("patientId", patientId)
                .getResultList();
    }

    @Override
    public int getNumberOfPrescriptions() {
        return getSize();
    }
} 