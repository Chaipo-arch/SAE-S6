package sae.semestre.six.domain.inventory;

import sae.semestre.six.dao.AbstractHibernateDao;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Implementation of the InventoryDao interface for accessing inventory data using Hibernate.
 */
@Repository
public class InventoryDaoImpl extends AbstractHibernateDao<Inventory, Long> implements InventoryDao {
    /**
     * Finds an inventory item by its unique item code.
     *
     * @param itemCode The unique code of the inventory item
     * @return The Inventory entity, or null if not found
     */
    @Override
    public Inventory findByItemCode(String itemCode) {
        return (Inventory) getEntityManager()
                .createQuery("FROM Inventory WHERE itemCode = :itemCode")
                .setParameter("itemCode", itemCode)
                .getSingleResult();
    }

    /**
     * Retrieves all inventory items that need restocking.
     *
     * @return List of Inventory entities needing restock
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<Inventory> findNeedingRestock() {
        return getEntityManager()
                .createQuery("FROM Inventory i WHERE i.quantity <= i.reorderLevel")
                .getResultList();
    }

    /**
     * Deletes all supplier invoice details referencing the given inventory item.
     *
     * @param inventory The Inventory entity
     */
    @Override
    public void deleteSupplierInvoiceDetailsByInventory(Inventory inventory) {
        getEntityManager()
            .createQuery("DELETE FROM SupplierInvoiceDetail d WHERE d.inventory = :inventory")
            .setParameter("inventory", inventory)
            .executeUpdate();
    }
}
