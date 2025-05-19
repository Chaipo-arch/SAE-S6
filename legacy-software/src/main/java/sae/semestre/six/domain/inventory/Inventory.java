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
    }
}
