package sae.semestre.six.domain.inventory.supplierInvoice;

import lombok.Getter;
import lombok.Setter;
import sae.semestre.six.domain.inventory.Inventory;

import jakarta.persistence.*;

/**
 * Entity representing the details of a supplier invoice.
 * Each detail corresponds to an item in the invoice, including quantity and unit price.
 */
@Entity
@Getter
@Table(name = "supplier_invoice_details")
public class SupplierInvoiceDetail {

    public SupplierInvoiceDetail() {
    }

    /**
     * Constructs a SupplierInvoiceDetail from a DTO.
     *
     * @param detail the DTO containing the detail information.
     */
    public SupplierInvoiceDetail(SupplierInvoiceDetailDTO detail) {
        this.quantity = detail.quantity();
        this.unitPrice = detail.unitPrice();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "unit_price")
    private Double unitPrice;

    @Setter
    @ManyToOne()
    @JoinColumn(name = "invoice_id", nullable = false)
    private SupplierInvoice supplierInvoice;

    @Setter
    @ManyToOne
    @JoinColumn(name = "inventory_id", nullable = false)
    private Inventory inventory;
}

