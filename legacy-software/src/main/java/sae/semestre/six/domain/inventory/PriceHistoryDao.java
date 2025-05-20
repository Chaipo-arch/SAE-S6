package sae.semestre.six.domain.inventory;

import sae.semestre.six.dao.GenericDao;
import java.util.List;

/**
 * DAO interface for PriceHistory entity.
 * Provides database operations for price history records.
 */
public interface PriceHistoryDao extends GenericDao<PriceHistory, Long> {
    /**
     * Retrieves all price history records for a given inventory item.
     *
     * @param inventory The inventory item
     * @return List of PriceHistory records for the inventory item
     */
    List<PriceHistory> findByInventory(Inventory inventory);
}
