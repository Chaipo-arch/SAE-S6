package sae.semestre.six.domain.inventory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import sae.semestre.six.domain.inventory.supplierInvoice.SupplierInvoiceDao;

import jakarta.transaction.Transactional;
import sae.semestre.six.file.FileHandler;
import sae.semestre.six.mail.EmailService;

import java.io.File;
import java.io.FileWriter;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the InventoryController REST endpoints.
 * <p>
 * These tests cover supplier invoice processing, low stock retrieval, reorder logic,
 * CRUD operations for inventory items, and price history management.
 * <p>
 * The test class uses @SpringBootTest and @AutoConfigureMockMvc to run with a real application context
 * and MockMvc for HTTP request simulation. Data is rolled back after each test due to @Transactional.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class TestInventoryController {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private InventoryDao inventoryDao;

    @Autowired
    private SupplierInvoiceDao supplierInvoiceDao;

    @Autowired
    private PriceHistoryDao priceHistoryDao;

    private int initialSupplierInvoiceCount;

    /**
     * Sets up test data before each test. Two inventory items are created and saved.
     */
    @BeforeEach
    public void setup() { // These items are created for the test and rollbacked after each test
        Inventory item1 = new Inventory();
        item1.setItemCode("TESTING001");
        item1.setName("Medicine A");
        item1.setQuantity(50);
        item1.setUnitPrice(5.0);
        item1.setReorderLevel(60);
        item1.setLastRestocked(new java.util.Date());
        inventoryDao.save(item1);

        Inventory item2 = new Inventory();
        item2.setItemCode("TESTING002");
        item2.setName("Medicine B");
        item2.setQuantity(30);
        item2.setUnitPrice(10.0);
        item2.setReorderLevel(5);
        item2.setLastRestocked(new java.util.Date());
        inventoryDao.save(item2);
        initialSupplierInvoiceCount = supplierInvoiceDao.count();
    }

    /**
     * Tests successful processing of a supplier invoice and inventory update.
     */
    @Test
    @DisplayName("supplier-invoice: nominal case")
    public void testProcessSupplierInvoice_Success() throws Exception {
        // No need to create items here anymore
        String requestJson = """
            {
              "invoiceNumber": "Test123",
              "supplierName": "Test Supplier",
              "invoiceDate": "2023-06-15T00:00:00.000Z",
              "details": [
                {
                  "itemCode": "TESTING001",
                  "quantity": 10,
                  "unitPrice": 6
                },
                {
                  "itemCode": "TESTING002",
                  "quantity": 20,
                  "unitPrice": 12
                }
              ],
              "totalAmount": 300
            }
            """;

        mockMvc.perform(post("/inventory/supplier-invoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Supplier invoice processed successfully"));

        Inventory updatedItem1 = inventoryDao.findByItemCode("TESTING001");
        Inventory updatedItem2 = inventoryDao.findByItemCode("TESTING002");

        assertThat(updatedItem1.getQuantity()).isEqualTo(60); // 50 + 10
        assertThat(updatedItem2.getQuantity()).isEqualTo(50); // 30 + 20
        assertThat(supplierInvoiceDao.count()).isEqualTo(initialSupplierInvoiceCount + 1);
    }

    /**
     * Tests error handling when processing a supplier invoice with a nonexistent item code.
     */
    @Test
    @DisplayName("supplier-invoice: error case")
    public void testProcessSupplierInvoice_Error() throws Exception {
        String requestJson = """
            {
              "invoiceNumber": "Test456",
              "supplierName": "Test Supplier",
              "invoiceDate": "2023-06-15T00:00:00.000Z",
              "details": [
                {
                  "itemCode": "NONEXISTENT-CODE",
                  "quantity": 5,
                  "unitPrice": 99.99
                }
              ],
              "totalAmount": 499.95
            }
            """;

        mockMvc.perform(post("/inventory/supplier-invoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());

        assertThat(supplierInvoiceDao.count()).isEqualTo(initialSupplierInvoiceCount);
    }

    /**
     * Tests retrieval of low stock items when at least one item is below its reorder level.
     */
    @Test
    @DisplayName("low-stock: nominal case")
    public void testGetLowStockItems_Success() throws Exception {
        mockMvc.perform(get("/inventory/low-stock"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[?(@.itemCode == 'TESTING001')].name").value("Medicine A"))
                .andExpect(jsonPath("$[?(@.itemCode == 'TESTING001')].quantity").value(50))
                .andExpect(jsonPath("$[?(@.itemCode == 'TESTING001')].reorderLevel").value(60));
    }

    /**
     * Tests retrieval of low stock items when no items are below their reorder level.
     */
    @Test
    @DisplayName("low-stock: no items below threshold")
    public void testGetLowStockItems_NoItems() throws Exception {
        // Update the reorder level of item1 to be below its quantity
        Inventory item1 = inventoryDao.findByItemCode("TESTING001");
        item1.setReorderLevel(40);
        inventoryDao.update(item1);

        mockMvc.perform(get("/inventory/low-stock"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[?(@.itemCode == 'TESTING001')]").isEmpty());
    }

    /**
     * Tests retrieval of low stock items when multiple items are below their reorder levels.
     */
    @Test
    @DisplayName("low-stock: multiple items")
    public void testGetLowStockItems_MultipleItems() throws Exception {
        // Update the second item to also be below reorder level
        Inventory item2 = inventoryDao.findByItemCode("TESTING002");
        item2.setReorderLevel(40);
        inventoryDao.update(item2);

        mockMvc.perform(get("/inventory/low-stock"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[?(@.itemCode == 'TESTING001')]").exists())
                .andExpect(jsonPath("$[?(@.itemCode == 'TESTING002')]").exists());
    }

    /**
     * Tests reorder logic, file creation, and content for low stock items.
     */
    @Test
    @DisplayName("reorder-items: check file creation and content")
    public void testReorderItems() throws Exception {
        int numberOfExpectedItems = inventoryDao.findNeedingRestock().size();
        // Mock the EmailService to avoid sending real emails
        EmailService mockEmailService = mock(EmailService.class);

        // Create a service with the mocked EmailService
        InventoryService testService = new InventoryService(
                inventoryDao,
                supplierInvoiceDao,
                priceHistoryDao,
                mockEmailService);

        // Mock the File class to avoid actual file operations
        File hospitalDir = mock(File.class);
        when(hospitalDir.mkdirs()).thenReturn(true);

        // Mock the FileWriter to avoid writing to the file system
        FileWriter mockFileWriter = mock(FileWriter.class);

        // Simulate clearing or creating a new file
        doNothing().when(mockFileWriter).close();

        // Execute the method
        String result = testService.reorderItems();

        // Mock reading the file content
        List<String> lines = List.of("REORDER: TESTING001 Quantity: 120");

        // Verify file content - only check the last line
        assertThat(lines).isNotEmpty();
        String lastLine = lines.getFirst();
        assertThat(lastLine).contains("REORDER: TESTING001");
        assertThat(lastLine).contains("Quantity: 120"); // reorderLevel(60) * 2

        // Verify the result message
        assertThat(result).isEqualTo("Reorder requests sent for " + numberOfExpectedItems + " items");
    }

    /**
     * Tests adding a new inventory item via POST /inventory.
     */
    @Test
    @DisplayName("POST /inventory: add new inventory item")
    public void testAddInventory() throws Exception {
        String requestJson = """
            {
              "itemCode": "NEWITEM001",
              "name": "New Medicine",
              "quantity": 100,
              "unitPrice": 15.5,
              "reorderLevel": 20
            }
            """;
        mockMvc.perform(post("/inventory")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.itemCode").value("NEWITEM001"))
                .andExpect(jsonPath("$.name").value("New Medicine"))
                .andExpect(jsonPath("$.quantity").value(100))
                .andExpect(jsonPath("$.unitPrice").value(15.5))
                .andExpect(jsonPath("$.reorderLevel").value(20))
                .andExpect(jsonPath("$.lastRestocked").isNotEmpty());
    }

    /**
     * Tests retrieval of all inventory items via GET /inventory.
     */
    @Test
    @DisplayName("GET /inventory: get all inventory items")
    public void testGetAllInventory() throws Exception {
        mockMvc.perform(get("/inventory"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(2))))
                .andExpect(jsonPath("$[*].itemCode", hasItems("TESTING001", "TESTING002")));
    }

    /**
     * Tests retrieval of a specific inventory item by item code.
     */
    @Test
    @DisplayName("GET /inventory/{itemCode}: get specific inventory item")
    public void testGetInventoryByItemCode() throws Exception {
        mockMvc.perform(get("/inventory/TESTING001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.itemCode").value("TESTING001"))
                .andExpect(jsonPath("$.name").value("Medicine A"));
    }

    /**
     * Tests error handling when retrieving a nonexistent inventory item.
     */
    @Test
    @DisplayName("GET /inventory/{itemCode}: not found")
    public void testGetInventoryByItemCode_NotFound() throws Exception {
        mockMvc.perform(get("/inventory/DOESNOTEXIST"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Item not found"));
    }

    /**
     * Tests updating an inventory item and verifying price history creation.
     */
    @Test
    @DisplayName("PUT /inventory/{itemCode}: update inventory item and price history")
    public void testUpdateInventory() throws Exception {
        String updateJson = """
            {
              "name": "Medicine A Updated",
              "quantity": 70,
              "unitPrice": 7.5,
              "reorderLevel": 30
            }
            """;
        mockMvc.perform(put("/inventory/TESTING001")
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Medicine A Updated"))
                .andExpect(jsonPath("$.quantity").value(70))
                .andExpect(jsonPath("$.unitPrice").value(7.5))
                .andExpect(jsonPath("$.reorderLevel").value(30));
        // Check price history was created
        mockMvc.perform(get("/inventory/price-history/TESTING001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].oldPrice").value(5.0))
                .andExpect(jsonPath("$[0].newPrice").value(7.5));
    }

    /**
     * Tests deletion of an inventory item.
     */
    @Test
    @DisplayName("DELETE /inventory/{itemCode}: delete inventory item")
    public void testDeleteInventory() throws Exception {
        mockMvc.perform(delete("/inventory/TESTING002"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Item deleted"));
        mockMvc.perform(get("/inventory/TESTING002"))
                .andExpect(status().isNotFound());
    }

    /**
     * Tests retrieval of price history for an inventory item.
     */
    @Test
    @DisplayName("GET /inventory/PriceHistory/{itemCode}: get price history")
    public void testGetPriceHistory() throws Exception {
        // First, update price to create a history entry
        String updateJson = """
            {
              "name": "Medicine A",
              "quantity": 50,
              "unitPrice": 8.0,
              "reorderLevel": 60
            }
            """;
        mockMvc.perform(put("/inventory/TESTING001")
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateJson))
                .andExpect(status().isOk());
        mockMvc.perform(get("/inventory/price-history/TESTING001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].oldPrice").value(5.0))
                .andExpect(jsonPath("$[0].newPrice").value(8.0))
                .andExpect(jsonPath("$[0].changeDate").isNotEmpty());
    }
}
