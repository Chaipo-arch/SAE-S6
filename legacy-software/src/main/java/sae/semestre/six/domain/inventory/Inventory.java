package sae.semestre.six.domain.inventory;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

/**
 * Entity representing an inventory item in the hospital system.
 * Contains information about the item code, name, quantity, price, reorder level, restock date, and price history.
 */
@Getter
@Entity
@NoArgsConstructor
@Table(name = "inventory")
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Unique code identifying the inventory item.
     */
    @Setter
    @Column(name = "item_code", unique = true)
    private String itemCode;

    /**
     * Name of the inventory item.
     */
    @Setter
    @Column(name = "name")
    private String name;

    /**
     * Current quantity in stock.
     */
    @Setter
    @Column(name = "quantity")
    private Integer quantity = 0;

    /**
     * Price per unit of the item.
     */
    @Setter
    @Column(name = "unit_price")
    private Double unitPrice;

    /**
     * The stock level at which a reorder should be triggered.
     */
    @Setter
    @Column(name = "reorder_level")
    private Integer reorderLevel;

    /**
     * The date when the item was last restocked.
     */
    @Setter
    @Column(name = "last_restocked")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastRestocked;

    /**
     * List of price history records for this inventory item.
     */
    @Setter
    @OneToMany(mappedBy = "inventory", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PriceHistory> priceHistories;

    /**
     * Constructs an Inventory entity from a DTO.
     *
     * @param dto The InventoryDTO containing item details.
     */
    public Inventory(InventoryDTO dto) {
        this.itemCode = dto.itemCode();
        this.name = dto.name();
        this.quantity = dto.quantity();
        this.unitPrice = dto.unitPrice();
        this.reorderLevel = dto.reorderLevel();
        this.lastRestocked = new Date();
        validateInvariants();
    }

    // --- DDD: Méthodes métier et encapsulation ---

    public void restock(int amount) {
        if (amount <= 0) throw new IllegalArgumentException("Restock amount must be positive");
        this.quantity += amount;
        this.lastRestocked = new Date();
        validateInvariants();
    }

    public void decrementStock(int amount) {
        if (amount <= 0) throw new IllegalArgumentException("Decrement amount must be positive");
        if (amount > this.quantity) throw new IllegalArgumentException("Not enough stock");
        this.quantity -= amount;
        validateInvariants();
    }

    /**
     * Change the price of the item and return a PriceHistory if changed.
     */
    public PriceHistory changePrice(Double newPrice) {
        if (newPrice == null || newPrice < 0) throw new IllegalArgumentException("Unit price cannot be negative");
        if (this.unitPrice != null && this.unitPrice.equals(newPrice)) return null;
        PriceHistory ph = new PriceHistory();
        ph.setInventory(this);
        ph.setOldPrice(this.unitPrice);
        ph.setNewPrice(newPrice);
        ph.setChangeDate(new Date());
        this.unitPrice = newPrice;
        return ph;
    }

    public void changeReorderLevel(Integer newLevel) {
        if (newLevel == null || newLevel < 0) throw new IllegalArgumentException("Reorder level cannot be negative");
        this.reorderLevel = newLevel;
        validateInvariants();
    }

    public void changeLastRestocked(Date date) {
        if (date == null) throw new IllegalArgumentException("Last restocked date cannot be null");
        this.lastRestocked = date;
    }

    public boolean isBelowReorderLevel() {
        return reorderLevel != null && quantity != null && quantity <= reorderLevel;
    }

    private void validateInvariants() {
        if (quantity != null && quantity < 0) throw new IllegalStateException("Quantity cannot be negative");
        if (reorderLevel != null && reorderLevel < 0) throw new IllegalStateException("Reorder level cannot be negative");
    }

}
