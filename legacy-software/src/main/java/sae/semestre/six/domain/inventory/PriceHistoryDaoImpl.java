package sae.semestre.six.domain.inventory;

import org.springframework.stereotype.Repository;
import sae.semestre.six.dao.AbstractHibernateDao;
import java.util.List;

/**
 * Implementation of the PriceHistoryDao interface.
 * Provides database operations for PriceHistory entities using Hibernate.
 */
@Repository
public class PriceHistoryDaoImpl extends AbstractHibernateDao<PriceHistory, Long> implements PriceHistoryDao {
    /**
     * Retrieves all price history records for a given inventory item.
     *
     * @param inventory The inventory item
     * @return List of PriceHistory records for the inventory item
     */
    @Override
    public List<PriceHistory> findByInventory(Inventory inventory) {
        return getEntityManager()
            .createQuery("FROM PriceHistory WHERE inventory = :inventory", PriceHistory.class)
            .setParameter("inventory", inventory)
            .getResultList();
    }
}
