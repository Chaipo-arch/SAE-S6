package sae.semestre.six.domain.billing.medical_acts;

import sae.semestre.six.dao.GenericDao;

import java.util.List;

public interface MedicalActDao extends GenericDao<MedicalAct, Long> {
    List<MedicalAct> findAll();
    MedicalAct findById(int id);
    MedicalAct findByName(String name);
    double findPriceByName(String name);
    void updatePrice(String name, double price);
}
