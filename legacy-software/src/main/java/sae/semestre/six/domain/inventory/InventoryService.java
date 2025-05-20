package sae.semestre.six.domain.inventory;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sae.semestre.six.domain.inventory.supplierInvoice.*;
import sae.semestre.six.mail.EmailService;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for inventory management operations.
 * Handles inventory CRUD, price history, supplier invoices, and reorder logic.
 */
@Service
@AllArgsConstructor
public class InventoryService {

    private final InventoryDao inventoryDao;
    private final SupplierInvoiceDao supplierInvoiceDao;
    private final PriceHistoryDao priceHistoryDao;
    private final EmailService emailService;

    /**
     * Adds a new inventory item to the system.
     *
     * @param dto The inventory data transfer object
     * @return The created InventoryDTO
     * @throws IllegalArgumentException if itemCode is missing or already exists
     */
    @Transactional
    public InventoryDTO addInventory(InventoryDTO dto) {
        if (dto.itemCode() == null || dto.itemCode().isEmpty()) {
            throw new IllegalArgumentException("itemCode is required");
        }
        Inventory existing = null;
        try {
            existing = inventoryDao.findByItemCode(dto.itemCode());
        } catch (Exception ignored) {}
        if (existing != null) {
            throw new IllegalArgumentException("Item already exists");
        }
        Inventory inv = new Inventory(dto);
        inventoryDao.save(inv);
        return new InventoryDTO(inv);
    }

    /**
     * Retrieves all inventory items.
     *
     * @return List of InventoryDTOs
     */
    @Transactional(readOnly = true)
    public List<InventoryDTO> getAllInventory() {
        return inventoryDao.findAll().stream().map(InventoryDTO::new).collect(Collectors.toList());
    }

    /**
     * Retrieves a specific inventory item by item code.
     *
     * @param itemCode The unique item code
     * @return The InventoryDTO, or null if not found
     */
    @Transactional(readOnly = true)
    public InventoryDTO getInventory(String itemCode) {
        try {
            Inventory inv = inventoryDao.findByItemCode(itemCode);
            return new InventoryDTO(inv);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Updates an existing inventory item.
     *
     * @param itemCode The unique item code
     * @param dto The inventory data transfer object
     * @return The updated InventoryDTO
     * @throws IllegalArgumentException if invalid data is provided
     */
    @Transactional
    public InventoryDTO updateInventory(String itemCode, InventoryDTO dto) {
        Inventory inv = inventoryDao.findByItemCode(itemCode);
        PriceHistory priceHistory = null;
        if (dto.name() != null && !dto.name().isEmpty()) {
            inv.setName(dto.name());
        }
        if (dto.quantity() != null && dto.quantity() >= 0) {
            int diff = dto.quantity() - inv.getQuantity();
            if (diff > 0) {
                inv.restock(diff);
            } else if (diff < 0) {
                inv.decrementStock(-diff);
            }
        } else if (dto.quantity() != null) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }
        if (dto.unitPrice() != null) {
            priceHistory = inv.changePrice(dto.unitPrice());
        }
        if (dto.reorderLevel() != null) {
            inv.changeReorderLevel(dto.reorderLevel());
        }
        if (dto.lastRestocked() != null && !dto.lastRestocked().isEmpty()) {
            try {
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm");
                inv.changeLastRestocked(sdf.parse(dto.lastRestocked()));
            } catch (java.text.ParseException e) {
                throw new IllegalArgumentException("Invalid date format for lastRestocked. Expected dd/MM/yyyy HH:mm");
            }
        }
        inventoryDao.update(inv);
        if (priceHistory != null) {
            priceHistoryDao.save(priceHistory);
        }
        return new InventoryDTO(inv);
    }

    /**
     * Deletes an inventory item by item code.
     *
     * @param itemCode The unique item code
     */
    @Transactional
    public void deleteInventory(String itemCode) {
        Inventory inv = inventoryDao.findByItemCode(itemCode);
        // Remove all SupplierInvoiceDetails referencing this inventory
        inventoryDao.deleteSupplierInvoiceDetailsByInventory(inv);
        inventoryDao.delete(inv);
    }

    /**
     * Retrieves the price history for a specific inventory item.
     *
     * @param itemCode The unique item code
     * @return List of PriceHistoryDTOs
     */
    @Transactional(readOnly = true)
    public List<PriceHistoryDTO> getPriceHistory(String itemCode) {
        Inventory inv = inventoryDao.findByItemCode(itemCode);
        return priceHistoryDao.findByInventory(inv).stream()
                .map(PriceHistoryDTO::new)
                .collect(Collectors.toList());
    }

    /**
     * Processes a supplier invoice and updates inventory accordingly.
     *
     * @param invoice The supplier invoice DTO
     * @throws IllegalArgumentException if the invoice is invalid or duplicate
     */
    @Transactional
    public void processSupplierInvoice(SupplierInvoiceDTO invoice) {
        if (!invoice.checkValidity()) {
            throw new IllegalArgumentException("Supplier invoice is invalid");
        }
        SupplierInvoice supplierInvoice = new SupplierInvoice(invoice);
        for (SupplierInvoiceDetailDTO detail : invoice.details()) {
            SupplierInvoiceDetail supplierInvoiceDetail = new SupplierInvoiceDetail(detail);
            supplierInvoiceDetail.setSupplierInvoice(supplierInvoice);
            supplierInvoiceDetail.setInventory(
                    addNewStock(detail.itemCode(), detail.quantity())
            );
            supplierInvoice.addDetails(supplierInvoiceDetail);
        }
        try {
            supplierInvoiceDao.save(supplierInvoice);
        } catch (Exception e) {
            throw new IllegalArgumentException("Another supplier invoice with the same number already exists");
        }
    }

    /**
     * Adds new stock to an inventory item.
     *
     * @param itemCode The unique item code
     * @param quantity The quantity to add
     * @return The updated Inventory entity
     * @throws IllegalArgumentException if the item is not found
     */
    private Inventory addNewStock(String itemCode, Integer quantity) {
        Inventory inventory;
        try {
            inventory = inventoryDao.findByItemCode(itemCode);
        } catch (Exception e) {
            throw new IllegalArgumentException("Item not found in inventory");
        }
        inventory.restock(quantity);
        inventoryDao.update(inventory);
        return inventory;
    }

    /**
     * Retrieves inventory items that are low in stock.
     *
     * @return List of Inventory entities needing restock
     */
    public List<Inventory> getLowStockItems() {
        return inventoryDao.findNeedingRestock();
    }

    /**
     * Sends reorder requests for low stock items and notifies suppliers.
     *
     * @return Status message indicating the number of reorder requests sent
     */
    public String reorderItems() {
        List<Inventory> lowStockItems = getLowStockItems();

        for (Inventory item : lowStockItems) {
            int reorderQuantity = item.getReorderLevel() * 2;

            try (FileWriter fw = new FileWriter("C:\\hospital\\orders.txt", true)) {
                fw.write("REORDER: " + item.getItemCode() + ", Quantity: " + reorderQuantity + "\n");
            } catch (IOException e) {
                System.err.println("Error writing to file: " + e.getMessage());
            }

            emailService.sendEmail(EmailService.EMAIL_SOURCE.SUPPLIER.getEmail(),
                    "Reorder Request",
                    "Please restock " + item.getName() + " (Quantity: " + reorderQuantity + ")");
        }

        return "Reorder requests sent for " + lowStockItems.size() + " items";
    }
}
