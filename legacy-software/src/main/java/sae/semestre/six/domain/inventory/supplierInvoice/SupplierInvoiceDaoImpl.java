package sae.semestre.six.domain.inventory.supplierInvoice;

import org.springframework.stereotype.Repository;
import sae.semestre.six.dao.AbstractHibernateDao;

/**
 * Implementation of the SupplierInvoiceDao interface.
 * Provides database operations for SupplierInvoice entities using Hibernate.
 */
@Repository
public class SupplierInvoiceDaoImpl extends AbstractHibernateDao<SupplierInvoice, Long> implements SupplierInvoiceDao {
    // No additional methods; inherits CRUD from AbstractHibernateDao
}
