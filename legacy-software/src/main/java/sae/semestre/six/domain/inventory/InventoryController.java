package sae.semestre.six.domain.inventory;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sae.semestre.six.domain.inventory.supplierInvoice.SupplierInvoiceDTO;

import java.util.List;
import java.util.Map;

/**
 * REST controller for inventory management endpoints.
 * Handles CRUD operations, price history, supplier invoices, and stock management.
 */
@RestController
@AllArgsConstructor
@RequestMapping("/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    /**
     * Adds a new inventory item.
     *
     * @param dto The inventory data transfer object
     * @return The created InventoryDTO or error message
     */
    @PostMapping
    public ResponseEntity<?> addInventory(@RequestBody InventoryDTO dto) {
        try {
            InventoryDTO created = inventoryService.addInventory(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Retrieves all inventory items.
     *
     * @return List of InventoryDTOs
     */
    @GetMapping
    public List<InventoryDTO> getAllInventory() {
        return inventoryService.getAllInventory();
    }

    /**
     * Retrieves a specific inventory item by item code.
     *
     * @param itemCode The unique item code
     * @return The InventoryDTO or error message
     */
    @GetMapping("/{itemCode}")
    public ResponseEntity<?> getInventory(@PathVariable String itemCode) {
        InventoryDTO dto = inventoryService.getInventory(itemCode);
        if (dto == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Item not found"));
        }
        return ResponseEntity.ok(dto);
    }

    /**
     * Updates an inventory item.
     *
     * @param itemCode The unique item code
     * @param dto The inventory data transfer object
     * @return The updated InventoryDTO or error message
     */
    @PutMapping("/{itemCode}")
    public ResponseEntity<?> updateInventory(@PathVariable String itemCode, @RequestBody InventoryDTO dto) {
        try {
            InventoryDTO updated = inventoryService.updateInventory(itemCode, dto);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Deletes an inventory item by item code.
     *
     * @param itemCode The unique item code
     * @return Success or error message
     */
    @DeleteMapping("/{itemCode}")
    public ResponseEntity<?> deleteInventory(@PathVariable String itemCode) {
        try {
            inventoryService.deleteInventory(itemCode);
            return ResponseEntity.ok(Map.of("message", "Item deleted"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Retrieves the price history for a specific inventory item.
     *
     * @param itemCode The unique item code
     * @return List of PriceHistoryDTOs or error message
     */
    @GetMapping("/PriceHistory/{itemCode}")
    public ResponseEntity<?> getPriceHistory(@PathVariable String itemCode) {
        try {
            return ResponseEntity.ok(inventoryService.getPriceHistory(itemCode));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Processes a supplier invoice and updates inventory.
     *
     * @param invoice The supplier invoice DTO
     * @return Success or error message
     */
    @PostMapping("/supplier-invoice")
    public ResponseEntity<?> processSupplierInvoice(@RequestBody SupplierInvoiceDTO invoice) {
        try {
            inventoryService.processSupplierInvoice(invoice);
            return ResponseEntity.ok(Map.of("message", "Supplier invoice processed successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Retrieves inventory items that are low in stock.
     *
     * @return List of InventoryDTOs or error message
     */
    @GetMapping("/low-stock")
    public ResponseEntity<?> getLowStockItems() {
        try {
            return ResponseEntity.ok(inventoryService.getLowStockItems().stream()
                    .map(InventoryDTO::new)
                    .toList());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Sends reorder requests for low stock items.
     *
     * @return Status message
     */
    @PostMapping("/reorder")
    public String reorderItems() {
        return inventoryService.reorderItems();
    }
}
