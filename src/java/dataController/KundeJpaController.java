package dataController;

import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import data.Ausleihe;
import data.Kunde;
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
public class KundeJpaController implements Serializable {

    public KundeJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Kunde kunde) throws RollbackFailureException, Exception {
        if (kunde.getAusleiheList() == null) {
            kunde.setAusleiheList(new ArrayList<Ausleihe>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            List<Ausleihe> attachedAusleiheList = new ArrayList<>();
            for (Ausleihe ausleiheListAusleiheToAttach : kunde.getAusleiheList()) {
                ausleiheListAusleiheToAttach = em.getReference(ausleiheListAusleiheToAttach.getClass(), ausleiheListAusleiheToAttach.getLeihId());
                attachedAusleiheList.add(ausleiheListAusleiheToAttach);
            }
            kunde.setAusleiheList(attachedAusleiheList);
            em.persist(kunde);
            for (Ausleihe ausleiheListAusleihe : kunde.getAusleiheList()) {
                Kunde oldKundeIdOfAusleiheListAusleihe = ausleiheListAusleihe.getKundeId();
                ausleiheListAusleihe.setKundeId(kunde);
                ausleiheListAusleihe = em.merge(ausleiheListAusleihe);
                if (oldKundeIdOfAusleiheListAusleihe != null) {
                    oldKundeIdOfAusleiheListAusleihe.getAusleiheList().remove(ausleiheListAusleihe);
                    oldKundeIdOfAusleiheListAusleihe = em.merge(oldKundeIdOfAusleiheListAusleihe);
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

    public void edit(Kunde kunde) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Kunde persistentKunde = em.find(Kunde.class, kunde.getKundeId());
            List<Ausleihe> ausleiheListOld = persistentKunde.getAusleiheList();
            List<Ausleihe> ausleiheListNew = kunde.getAusleiheList();
            List<String> illegalOrphanMessages = null;
            for (Ausleihe ausleiheListOldAusleihe : ausleiheListOld) {
                if (!ausleiheListNew.contains(ausleiheListOldAusleihe)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<>();
                    }
                    illegalOrphanMessages.add("You must retain Ausleihe " + ausleiheListOldAusleihe + " since its kundeId field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<Ausleihe> attachedAusleiheListNew = new ArrayList<>();
            for (Ausleihe ausleiheListNewAusleiheToAttach : ausleiheListNew) {
                ausleiheListNewAusleiheToAttach = em.getReference(ausleiheListNewAusleiheToAttach.getClass(), ausleiheListNewAusleiheToAttach.getLeihId());
                attachedAusleiheListNew.add(ausleiheListNewAusleiheToAttach);
            }
            ausleiheListNew = attachedAusleiheListNew;
            kunde.setAusleiheList(ausleiheListNew);
            kunde = em.merge(kunde);
            for (Ausleihe ausleiheListNewAusleihe : ausleiheListNew) {
                if (!ausleiheListOld.contains(ausleiheListNewAusleihe)) {
                    Kunde oldKundeIdOfAusleiheListNewAusleihe = ausleiheListNewAusleihe.getKundeId();
                    ausleiheListNewAusleihe.setKundeId(kunde);
                    ausleiheListNewAusleihe = em.merge(ausleiheListNewAusleihe);
                    if (oldKundeIdOfAusleiheListNewAusleihe != null && !oldKundeIdOfAusleiheListNewAusleihe.equals(kunde)) {
                        oldKundeIdOfAusleiheListNewAusleihe.getAusleiheList().remove(ausleiheListNewAusleihe);
                        oldKundeIdOfAusleiheListNewAusleihe = em.merge(oldKundeIdOfAusleiheListNewAusleihe);
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
                Integer id = kunde.getKundeId();
                if (findKunde(id) == null) {
                    throw new NonexistentEntityException("The kunde with id " + id + " no longer exists.");
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
            Kunde kunde;
            try {
                kunde = em.getReference(Kunde.class, id);
                kunde.getKundeId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The kunde with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Ausleihe> ausleiheListOrphanCheck = kunde.getAusleiheList();
            for (Ausleihe ausleiheListOrphanCheckAusleihe : ausleiheListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<>();
                }
                illegalOrphanMessages.add("This Kunde (" + kunde + ") cannot be destroyed since the Ausleihe " + ausleiheListOrphanCheckAusleihe + " in its ausleiheList field has a non-nullable kundeId field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(kunde);
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

    public List<Kunde> findKundeEntities() {
        return findKundeEntities(true, -1, -1);
    }

    public List<Kunde> findKundeEntities(int maxResults, int firstResult) {
        return findKundeEntities(false, maxResults, firstResult);
    }

    private List<Kunde> findKundeEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Kunde.class));
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

    public Kunde findKunde(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Kunde.class, id);
        } finally {
            em.close();
        }
    }

    public int getKundeCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Kunde> rt = cq.from(Kunde.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
