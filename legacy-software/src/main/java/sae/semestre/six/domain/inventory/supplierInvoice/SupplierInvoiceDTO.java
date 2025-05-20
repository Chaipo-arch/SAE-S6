package sae.semestre.six.domain.inventory.supplierInvoice;

import java.util.Date;

/**
 * Data Transfer Object for SupplierInvoice.
 * Represents the data structure for transferring supplier invoice information.
 */
public record SupplierInvoiceDTO(String invoiceNumber,
                                 String supplierName,
                                 Date invoiceDate,
                                 SupplierInvoiceDetailDTO[] details,
                                 Double totalAmount) {
    /**
     * Validates the supplier invoice by checking the validity of its details
     * and ensuring the total amount matches the sum of the details' prices.
     *
     * @return true if the invoice is valid, false otherwise.
     */
    public boolean checkValidity() {
        double totalAmountReceived = 0;
        boolean isOk = true;
        if (details.length == 0) {
            return false;
        }
        for (SupplierInvoiceDetailDTO detail : details) {
            isOk = isOk && detail.checkValidity();
            totalAmountReceived += detail.calculateTotalPrice();
        }
        return totalAmountReceived == totalAmount && isOk;
    }
}
