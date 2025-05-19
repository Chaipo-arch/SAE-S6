package sae.semestre.six.domain.inventory;

import sae.semestre.six.dao.GenericDao;
import java.util.List;

/**
 * DAO interface for inventory-related database operations.
 */
public interface InventoryDao extends GenericDao<Inventory, Long> {
    /**
     * Finds an inventory item by its unique item code.
     *
     * @param itemCode The unique code of the inventory item
     * @return The Inventory entity, or null if not found
     */
    Inventory findByItemCode(String itemCode);

    /**
     * Retrieves all inventory items that need restocking.
     *
     * @return List of Inventory entities needing restock
     */
    List<Inventory> findNeedingRestock();

    /**
     * Updates the given inventory entity in the database.
     *
     * @param inventory The Inventory entity to update
     */
    void update(Inventory inventory);

    /**
     * Retrieves all inventory items.
     *
     * @return List of all Inventory entities
     */
    List<Inventory> findAll();

    /**
     * Deletes all supplier invoice details referencing the given inventory item.
     *
     * @param inventory The Inventory entity
     */
    void deleteSupplierInvoiceDetailsByInventory(Inventory inventory);
}
