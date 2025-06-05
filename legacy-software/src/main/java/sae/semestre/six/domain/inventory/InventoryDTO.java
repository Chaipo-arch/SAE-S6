package sae.semestre.six.domain.inventory;

/**
 * Data Transfer Object (DTO) for Inventory entity.
 * Encapsulates inventory item details for API communication.
 */
public record InventoryDTO(String itemCode,
                           String name,
                           Integer quantity,
                           Double unitPrice,
                           Integer reorderLevel,
                           String lastRestocked) {
    /**
     * Constructs an InventoryDTO from an Inventory entity.
     *
     * @param inventory The Inventory entity
     */
    public InventoryDTO(Inventory inventory) {
        this(inventory.getItemCode(),
                inventory.getName(),
                inventory.getQuantity(),
                inventory.getUnitPrice(),
                inventory.getReorderLevel(),
                inventory.getLastRestocked() != null ? inventory.getLastRestocked().toString() : null);
    }
}
