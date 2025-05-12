package sae.semestre.six.domain.inventory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sae.semestre.six.domain.inventory.supplierInvoice.SupplierInvoice;
import sae.semestre.six.domain.inventory.supplierInvoice.SupplierInvoiceDetail;
import sae.semestre.six.file.FileHandler;
import sae.semestre.six.mail.EmailService;
import java.util.*;
import java.util.stream.Collectors;
import java.io.FileWriter;
import java.io.IOException;

@RestController
@RequestMapping("/inventory")
public class InventoryController {
    
    @Autowired
    private InventoryDao inventoryDao;
    
    private final EmailService emailService ;
    private final FileHandler fileHandler;

    public InventoryController(EmailService emailService, FileHandler fileHandler) {
        this.emailService = emailService;
        this.fileHandler = fileHandler;
    }


    @PostMapping("/supplier-invoice")
    public String processSupplierInvoice(@RequestBody SupplierInvoice invoice) {
        try {
            
            for (SupplierInvoiceDetail detail : invoice.getDetails()) {
                Inventory inventory = detail.getInventory();
                
                
                inventory.setQuantity(inventory.getQuantity() + detail.getQuantity());
                inventory.setUnitPrice(detail.getUnitPrice());
                inventory.setLastRestocked(new Date());
                
                
                inventoryDao.update(inventory);
            }
            
            return "Supplier invoice processed successfully";
        } catch (Exception e) {
            
            return "Error: " + e.getMessage();
        }
    }
    
    
    @GetMapping("/low-stock")
    public List<Inventory> getLowStockItems() {
        return inventoryDao.findAll().stream()
            .filter(Inventory::needsRestock)
            .collect(Collectors.toList());
    }
    
    
    @PostMapping("/reorder")
    public String reorderItems() {
        List<Inventory> lowStockItems = inventoryDao.findNeedingRestock();
        
        for (Inventory item : lowStockItems) {
            
            int reorderQuantity = item.getReorderLevel() * 2;


            fileHandler.writeToFile("C:\\hospital\\orders.txt",
                    "REORDER: " + item.getItemCode() + ", Quantity: " + reorderQuantity + "\n");
            
            
            emailService.sendEmail(
                    EmailService.EMAIL_SOURCE.SUPPLIER.getEmail(),
                "Reorder Request",
                "Please restock " + item.getName() + " (Quantity: " + reorderQuantity + ")"
            );
        }
        
        return "Reorder requests sent for " + lowStockItems.size() + " items";
    }

    @PostMapping("/refill")
    public String refillStock(
            @RequestParam String stock,
            @RequestParam int quantity) {

        Inventory inventory = inventoryDao.findByItemCode(stock);
        inventory.setQuantity(quantity+ inventory.getQuantity());
        return "Refilled " + stock;
    }

    public List<Inventory> getAllInventory() {
        return inventoryDao.findAll();
    }
} 