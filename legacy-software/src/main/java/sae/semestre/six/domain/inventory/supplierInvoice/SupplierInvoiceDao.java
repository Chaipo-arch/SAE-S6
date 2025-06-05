package sae.semestre.six.domain.inventory.supplierInvoice;

import sae.semestre.six.dao.GenericDao;

/**
 * DAO interface for SupplierInvoice.
 * Defines database operations for SupplierInvoice entities.
 */
public interface SupplierInvoiceDao extends GenericDao<SupplierInvoice, Long> {
    // No additional methods; inherits CRUD from GenericDao
}
