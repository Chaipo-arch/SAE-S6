package sae.semestre.six.domain.billing.medical_acts;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;
import sae.semestre.six.dao.AbstractHibernateDao;

@Repository
public class MedicalActDaoImpl extends AbstractHibernateDao<MedicalAct, Long> implements MedicalActDao {
    @Override
    public MedicalAct findById(int id) {
        return (MedicalAct) getEntityManager()
                .createQuery("FROM MedicalAct WHERE id = :id")
                .setParameter("id", id)
                .getSingleResult();
    }

    @Override
    public MedicalAct findByName(String name) {
        return (MedicalAct) getEntityManager()
                .createQuery("FROM MedicalAct WHERE name = :name")
                .setParameter("name", name)
                .getSingleResult();
    }

    @Override
    public double findPriceByName(String name) {
        return findByName(name).getPrice();
    }

    @Transactional
    @Override
    public void updatePrice(String name, double price) {
        getEntityManager()
                .createQuery("UPDATE MedicalAct SET price = :price WHERE name = :name")
                .setParameter("price", price)
                .setParameter("name", name)
                .executeUpdate();
    }
}
