package sae.semestre.six.domain.inventory;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.Date;

/**
 * Entity representing a price change history record for an inventory item.
 * Stores the old price, new price, and the date of the change for auditing and tracking purposes.
 */
@Getter
@Setter
@Entity
@Table(name = "price_history")
public class PriceHistory {
    /**
     * Unique identifier for the price history record.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The inventory item associated with this price change.
     */
    @ManyToOne
    @JoinColumn(name = "inventory_id", nullable = false)
    private Inventory inventory;

    /**
     * The previous price before the change.
     */
    @Column(name = "old_price")
    private Double oldPrice;

    /**
     * The new price after the change.
     */
    @Column(name = "new_price")
    private Double newPrice;

    /**
     * The date and time when the price change occurred.
     */
    @Column(name = "change_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date changeDate = new Date();
}

