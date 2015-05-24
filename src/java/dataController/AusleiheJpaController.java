package dataController;

import data.Ausleihe;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import data.Exemplar;
import data.Kunde;
import dataController.exceptions.NonexistentEntityException;
import dataController.exceptions.RollbackFailureException;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

/**
 *
 * @author Susanne Otto
 */
public class AusleiheJpaController implements Serializable {

    public AusleiheJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Ausleihe ausleihe) throws RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Exemplar exemplarId = ausleihe.getExemplarId();
            if (exemplarId != null) {
                exemplarId = em.getReference(exemplarId.getClass(), exemplarId.getExemplarId());
                ausleihe.setExemplarId(exemplarId);
            }
            Kunde kundeId = ausleihe.getKundeId();
            if (kundeId != null) {
                kundeId = em.getReference(kundeId.getClass(), kundeId.getKundeId());
                ausleihe.setKundeId(kundeId);
            }
            em.persist(ausleihe);
            if (exemplarId != null) {
                exemplarId.getAusleiheList().add(ausleihe);
                exemplarId = em.merge(exemplarId);
            }
            if (kundeId != null) {
                kundeId.getAusleiheList().add(ausleihe);
                kundeId = em.merge(kundeId);
            }
            utx.commit();
        } catch (NotSupportedException | SystemException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException ex) {
            try {
                utx.rollback();
            } catch (IllegalStateException | SecurityException | SystemException re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Ausleihe ausleihe) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Ausleihe persistentAusleihe = em.find(Ausleihe.class, ausleihe.getLeihId());
            Exemplar exemplarIdOld = persistentAusleihe.getExemplarId();
            Exemplar exemplarIdNew = ausleihe.getExemplarId();
            Kunde kundeIdOld = persistentAusleihe.getKundeId();
            Kunde kundeIdNew = ausleihe.getKundeId();
            if (exemplarIdNew != null) {
                exemplarIdNew = em.getReference(exemplarIdNew.getClass(), exemplarIdNew.getExemplarId());
                ausleihe.setExemplarId(exemplarIdNew);
            }
            if (kundeIdNew != null) {
                kundeIdNew = em.getReference(kundeIdNew.getClass(), kundeIdNew.getKundeId());
                ausleihe.setKundeId(kundeIdNew);
            }
            ausleihe = em.merge(ausleihe);
            if (exemplarIdOld != null && !exemplarIdOld.equals(exemplarIdNew)) {
                exemplarIdOld.getAusleiheList().remove(ausleihe);
                exemplarIdOld = em.merge(exemplarIdOld);
            }
            if (exemplarIdNew != null && !exemplarIdNew.equals(exemplarIdOld)) {
                exemplarIdNew.getAusleiheList().add(ausleihe);
                exemplarIdNew = em.merge(exemplarIdNew);
            }
            if (kundeIdOld != null && !kundeIdOld.equals(kundeIdNew)) {
                kundeIdOld.getAusleiheList().remove(ausleihe);
                kundeIdOld = em.merge(kundeIdOld);
            }
            if (kundeIdNew != null && !kundeIdNew.equals(kundeIdOld)) {
                kundeIdNew.getAusleiheList().add(ausleihe);
                kundeIdNew = em.merge(kundeIdNew);
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = ausleihe.getLeihId();
                if (findAusleihe(id) == null) {
                    throw new NonexistentEntityException("The ausleihe with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Ausleihe ausleihe;
            try {
                ausleihe = em.getReference(Ausleihe.class, id);
                ausleihe.getLeihId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The ausleihe with id " + id + " no longer exists.", enfe);
            }
            Exemplar exemplarId = ausleihe.getExemplarId();
            if (exemplarId != null) {
                exemplarId.getAusleiheList().remove(ausleihe);
                exemplarId = em.merge(exemplarId);
            }
            Kunde kundeId = ausleihe.getKundeId();
            if (kundeId != null) {
                kundeId.getAusleiheList().remove(ausleihe);
                kundeId = em.merge(kundeId);
            }
            em.remove(ausleihe);
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Ausleihe> findAusleiheEntities() {
        return findAusleiheEntities(true, -1, -1);
    }

    public List<Ausleihe> findAusleiheEntities(int maxResults, int firstResult) {
        return findAusleiheEntities(false, maxResults, firstResult);
    }

    private List<Ausleihe> findAusleiheEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Ausleihe.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Ausleihe findAusleihe(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Ausleihe.class, id);
        } finally {
            em.close();
        }
    }

    public int getAusleiheCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Ausleihe> rt = cq.from(Ausleihe.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
