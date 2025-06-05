package sae.semestre.six.domain.medicalRecord;

import sae.semestre.six.dao.AbstractHibernateDao;
import org.springframework.stereotype.Repository;
import java.util.Date;
import java.util.List;

@Repository
public class MedicalRecordDaoImpl extends AbstractHibernateDao<MedicalRecord, Long> implements MedicalRecordDao {
    
    @Override
    public MedicalRecord findByRecordNumber(String recordNumber) {
        return (MedicalRecord) getEntityManager()
                .createQuery("FROM MedicalRecord WHERE recordNumber = :recordNumber")
                .setParameter("recordNumber", recordNumber)
                .getSingleResult();
    }
    
    @Override
    public List<MedicalRecord> findByPatientId(Long patientId) {
        return getEntityManager()
                .createQuery("FROM MedicalRecord WHERE patient.id = :patientId", MedicalRecord.class)
                .setParameter("patientId", patientId)
                .getResultList();
    }
    
    @Override
    public List<MedicalRecord> findByDoctorId(Long doctorId) {
        return getEntityManager()
                .createQuery("FROM MedicalRecord WHERE doctor.id = :doctorId", MedicalRecord.class)
                .setParameter("doctorId", doctorId)
                .getResultList();
    }
    
    @Override
    public List<MedicalRecord> findByDateRange(Date startDate, Date endDate) {
        return getEntityManager()
                .createQuery("FROM MedicalRecord WHERE recordDate BETWEEN :startDate AND :endDate", MedicalRecord.class)
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate)
                .getResultList();
    }
    
    @Override
    public List<MedicalRecord> findByDiagnosis(String diagnosis) {
        return getEntityManager()
                .createQuery("FROM MedicalRecord WHERE diagnosis LIKE :diagnosis", MedicalRecord.class)
                .setParameter("diagnosis", "%" + diagnosis + "%")
                .getResultList();
    }
} 