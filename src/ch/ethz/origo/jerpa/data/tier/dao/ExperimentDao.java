package ch.ethz.origo.jerpa.data.tier.dao;

import ch.ethz.origo.jerpa.data.tier.HibernateUtil;
import ch.ethz.origo.jerpa.data.tier.pojo.Experiment;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Projections;

import java.util.List;

/**
 * @author Petr Miko
 *         <p/>
 *         DAO for Experiment type manipulation.
 */
public class ExperimentDao extends GenericDao<Experiment, Integer> {

    public ExperimentDao() {
        super(Experiment.class);
    }

    @SuppressWarnings("unchecked")
    public List<Experiment> getAll() {

        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction transaction = session.beginTransaction();
        List<Experiment> allRecords = session.createCriteria(Experiment.class).setFetchMode("scenario", FetchMode.JOIN).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
        transaction.commit();
        return allRecords;
    }

    /**
     * Getter of highest actual identifier + 1
     *
     * @return next primary key value
     */
    public int getNextAvailableId() {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        return (Integer) session.createCriteria(Experiment.class).setProjection(Projections.max("experimentId")).uniqueResult() + 1;
    }

}
