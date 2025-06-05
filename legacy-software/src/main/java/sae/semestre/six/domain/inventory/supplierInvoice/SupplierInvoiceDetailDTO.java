package sae.semestre.six.domain.inventory.supplierInvoice;

/**
 * Data Transfer Object for SupplierInvoiceDetail.
 * Represents the data structure for transferring supplier invoice detail information.
 */
public record SupplierInvoiceDetailDTO(Integer quantity,
                                       Double unitPrice,
                                       String itemCode) {
    /**
     * Validates the supplier invoice detail by checking the quantity and unit price.
     *
     * @return true if the detail is valid, false otherwise.
     */
    public boolean checkValidity() {
        return quantity != null && unitPrice != null && quantity > 0 && unitPrice > 0;
    }

    /**
     * Calculates the total price for the detail.
     *
     * @return the total price (quantity * unit price).
     */
    public double calculateTotalPrice() {
        return quantity * unitPrice;
    }
}
