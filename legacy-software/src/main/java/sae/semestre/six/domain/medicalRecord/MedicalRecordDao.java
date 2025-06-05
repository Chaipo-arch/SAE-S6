package sae.semestre.six.domain.medicalRecord;

import sae.semestre.six.dao.GenericDao;

import java.util.Date;
import java.util.List;

public interface MedicalRecordDao extends GenericDao<MedicalRecord, Long> {
    MedicalRecord findByRecordNumber(String recordNumber);
    List<MedicalRecord> findByPatientId(Long patientId);
    List<MedicalRecord> findByDoctorId(Long doctorId);
    List<MedicalRecord> findByDateRange(Date startDate, Date endDate);
    List<MedicalRecord> findByDiagnosis(String diagnosis);
} 