package sae.semestre.six.domain.patient.history;

import org.springframework.stereotype.Repository;
import sae.semestre.six.dao.AbstractHibernateDao;
import sae.semestre.six.dao.GenericDao;

@Repository
public class LabResultDaoImpl extends AbstractHibernateDao<LabResult,Long> implements LabResultDao {

}
