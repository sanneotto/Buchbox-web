package dataController;

import data.Verlag;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import data.Verlagbuch;
import dataController.exceptions.IllegalOrphanException;
import dataController.exceptions.NonexistentEntityException;
import dataController.exceptions.RollbackFailureException;
import java.util.ArrayList;
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
public class VerlagJpaController implements Serializable {

    public VerlagJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Verlag verlag) throws RollbackFailureException, Exception {
        if (verlag.getVerlagbuchList() == null) {
            verlag.setVerlagbuchList(new ArrayList<Verlagbuch>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            List<Verlagbuch> attachedVerlagbuchList = new ArrayList<>();
            for (Verlagbuch verlagbuchListVerlagbuchToAttach : verlag.getVerlagbuchList()) {
                verlagbuchListVerlagbuchToAttach = em.getReference(verlagbuchListVerlagbuchToAttach.getClass(), verlagbuchListVerlagbuchToAttach.getVerlagbuchId());
                attachedVerlagbuchList.add(verlagbuchListVerlagbuchToAttach);
            }
            verlag.setVerlagbuchList(attachedVerlagbuchList);
            em.persist(verlag);
            for (Verlagbuch verlagbuchListVerlagbuch : verlag.getVerlagbuchList()) {
                Verlag oldVerlagIdOfVerlagbuchListVerlagbuch = verlagbuchListVerlagbuch.getVerlagId();
                verlagbuchListVerlagbuch.setVerlagId(verlag);
                verlagbuchListVerlagbuch = em.merge(verlagbuchListVerlagbuch);
                if (oldVerlagIdOfVerlagbuchListVerlagbuch != null) {
                    oldVerlagIdOfVerlagbuchListVerlagbuch.getVerlagbuchList().remove(verlagbuchListVerlagbuch);
                    oldVerlagIdOfVerlagbuchListVerlagbuch = em.merge(oldVerlagIdOfVerlagbuchListVerlagbuch);
                }
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

    public void edit(Verlag verlag) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Verlag persistentVerlag = em.find(Verlag.class, verlag.getVerlagId());
            List<Verlagbuch> verlagbuchListOld = persistentVerlag.getVerlagbuchList();
            List<Verlagbuch> verlagbuchListNew = verlag.getVerlagbuchList();
            List<String> illegalOrphanMessages = null;
            for (Verlagbuch verlagbuchListOldVerlagbuch : verlagbuchListOld) {
                if (!verlagbuchListNew.contains(verlagbuchListOldVerlagbuch)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<>();
                    }
                    illegalOrphanMessages.add("You must retain Verlagbuch " + verlagbuchListOldVerlagbuch + " since its verlagId field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<Verlagbuch> attachedVerlagbuchListNew = new ArrayList<>();
            for (Verlagbuch verlagbuchListNewVerlagbuchToAttach : verlagbuchListNew) {
                verlagbuchListNewVerlagbuchToAttach = em.getReference(verlagbuchListNewVerlagbuchToAttach.getClass(), verlagbuchListNewVerlagbuchToAttach.getVerlagbuchId());
                attachedVerlagbuchListNew.add(verlagbuchListNewVerlagbuchToAttach);
            }
            verlagbuchListNew = attachedVerlagbuchListNew;
            verlag.setVerlagbuchList(verlagbuchListNew);
            verlag = em.merge(verlag);
            for (Verlagbuch verlagbuchListNewVerlagbuch : verlagbuchListNew) {
                if (!verlagbuchListOld.contains(verlagbuchListNewVerlagbuch)) {
                    Verlag oldVerlagIdOfVerlagbuchListNewVerlagbuch = verlagbuchListNewVerlagbuch.getVerlagId();
                    verlagbuchListNewVerlagbuch.setVerlagId(verlag);
                    verlagbuchListNewVerlagbuch = em.merge(verlagbuchListNewVerlagbuch);
                    if (oldVerlagIdOfVerlagbuchListNewVerlagbuch != null && !oldVerlagIdOfVerlagbuchListNewVerlagbuch.equals(verlag)) {
                        oldVerlagIdOfVerlagbuchListNewVerlagbuch.getVerlagbuchList().remove(verlagbuchListNewVerlagbuch);
                        oldVerlagIdOfVerlagbuchListNewVerlagbuch = em.merge(oldVerlagIdOfVerlagbuchListNewVerlagbuch);
                    }
                }
            }
            utx.commit();
        } catch (NotSupportedException | SystemException | IllegalOrphanException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException ex) {
            try {
                utx.rollback();
            } catch (IllegalStateException | SecurityException | SystemException re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = verlag.getVerlagId();
                if (findVerlag(id) == null) {
                    throw new NonexistentEntityException("The verlag with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Verlag verlag;
            try {
                verlag = em.getReference(Verlag.class, id);
                verlag.getVerlagId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The verlag with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Verlagbuch> verlagbuchListOrphanCheck = verlag.getVerlagbuchList();
            for (Verlagbuch verlagbuchListOrphanCheckVerlagbuch : verlagbuchListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<>();
                }
                illegalOrphanMessages.add("This Verlag (" + verlag + ") cannot be destroyed since the Verlagbuch " + verlagbuchListOrphanCheckVerlagbuch + " in its verlagbuchList field has a non-nullable verlagId field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(verlag);
            utx.commit();
        } catch (NotSupportedException | SystemException | NonexistentEntityException | IllegalOrphanException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException ex) {
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

    public List<Verlag> findVerlagEntities() {
        return findVerlagEntities(true, -1, -1);
    }

    public List<Verlag> findVerlagEntities(int maxResults, int firstResult) {
        return findVerlagEntities(false, maxResults, firstResult);
    }

    private List<Verlag> findVerlagEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Verlag.class));
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

    public Verlag findVerlag(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Verlag.class, id);
        } finally {
            em.close();
        }
    }

    public int getVerlagCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Verlag> rt = cq.from(Verlag.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
