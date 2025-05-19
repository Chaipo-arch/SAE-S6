package sae.semestre.six.domain.inventory.supplierInvoice;

import jakarta.persistence.*;
import java.util.Date;
import java.util.Set;
import java.util.HashSet;

import lombok.Getter;

/**
 * Entity representing a supplier invoice.
 * Contains information about the supplier, invoice details, and total amount.
 */
@Entity
@Getter
@Table(name = "supplier_invoices")
public class SupplierInvoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "invoice_number", unique = true)
    private String invoiceNumber;

    @Column(name = "supplier_name")
    private String supplierName;

    @Column(name = "invoice_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date invoiceDate = new Date();

    @OneToMany(mappedBy = "supplierInvoice", cascade = {CascadeType.ALL}, orphanRemoval = true)
    private Set<SupplierInvoiceDetail> details = new HashSet<>();

    @Column(name = "total_amount")
    private Double totalAmount = 0.0;

    /**
     * Constructs a SupplierInvoice from a DTO.
     *
     * @param invoice the DTO containing the invoice information.
     */
    public SupplierInvoice(SupplierInvoiceDTO invoice) {
        this.invoiceNumber = invoice.invoiceNumber();
        this.supplierName = invoice.supplierName();
        this.invoiceDate = invoice.invoiceDate() == null ? new Date() : invoice.invoiceDate();
        this.totalAmount = invoice.totalAmount();
    }

    /**
     * Default constructor for JPA.
     */
    public SupplierInvoice() {

    }

    /**
     * Adds a detail to the invoice and sets the relationship.
     *
     * @param supplierInvoiceDetail the detail to add.
     */
    public void addDetails(SupplierInvoiceDetail supplierInvoiceDetail) {
        if (supplierInvoiceDetail != null) {
            details.add(supplierInvoiceDetail);
            supplierInvoiceDetail.setSupplierInvoice(this);
        }
    }
}
