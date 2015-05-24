package dataController;

import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import data.Verlagbuch;
import data.Ausleihe;
import data.Exemplar;
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
public class ExemplarJpaController implements Serializable {

    public ExemplarJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Exemplar exemplar) throws RollbackFailureException, Exception {
        if (exemplar.getAusleiheList() == null) {
            exemplar.setAusleiheList(new ArrayList<Ausleihe>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Verlagbuch verlagbuchId = exemplar.getVerlagbuchId();
            if (verlagbuchId != null) {
                verlagbuchId = em.getReference(verlagbuchId.getClass(), verlagbuchId.getVerlagbuchId());
                exemplar.setVerlagbuchId(verlagbuchId);
            }
            List<Ausleihe> attachedAusleiheList = new ArrayList<>();
            for (Ausleihe ausleiheListAusleiheToAttach : exemplar.getAusleiheList()) {
                ausleiheListAusleiheToAttach = em.getReference(ausleiheListAusleiheToAttach.getClass(), ausleiheListAusleiheToAttach.getLeihId());
                attachedAusleiheList.add(ausleiheListAusleiheToAttach);
            }
            exemplar.setAusleiheList(attachedAusleiheList);
            em.persist(exemplar);
            if (verlagbuchId != null) {
                verlagbuchId.getExemplarList().add(exemplar);
                verlagbuchId = em.merge(verlagbuchId);
            }
            for (Ausleihe ausleiheListAusleihe : exemplar.getAusleiheList()) {
                Exemplar oldExemplarIdOfAusleiheListAusleihe = ausleiheListAusleihe.getExemplarId();
                ausleiheListAusleihe.setExemplarId(exemplar);
                ausleiheListAusleihe = em.merge(ausleiheListAusleihe);
                if (oldExemplarIdOfAusleiheListAusleihe != null) {
                    oldExemplarIdOfAusleiheListAusleihe.getAusleiheList().remove(ausleiheListAusleihe);
                    oldExemplarIdOfAusleiheListAusleihe = em.merge(oldExemplarIdOfAusleiheListAusleihe);
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

    public void edit(Exemplar exemplar) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Exemplar persistentExemplar = em.find(Exemplar.class, exemplar.getExemplarId());
            Verlagbuch verlagbuchIdOld = persistentExemplar.getVerlagbuchId();
            Verlagbuch verlagbuchIdNew = exemplar.getVerlagbuchId();
            List<Ausleihe> ausleiheListOld = persistentExemplar.getAusleiheList();
            List<Ausleihe> ausleiheListNew = exemplar.getAusleiheList();
            List<String> illegalOrphanMessages = null;
            for (Ausleihe ausleiheListOldAusleihe : ausleiheListOld) {
                if (!ausleiheListNew.contains(ausleiheListOldAusleihe)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<>();
                    }
                    illegalOrphanMessages.add("You must retain Ausleihe " + ausleiheListOldAusleihe + " since its exemplarId field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (verlagbuchIdNew != null) {
                verlagbuchIdNew = em.getReference(verlagbuchIdNew.getClass(), verlagbuchIdNew.getVerlagbuchId());
                exemplar.setVerlagbuchId(verlagbuchIdNew);
            }
            List<Ausleihe> attachedAusleiheListNew = new ArrayList<>();
            for (Ausleihe ausleiheListNewAusleiheToAttach : ausleiheListNew) {
                ausleiheListNewAusleiheToAttach = em.getReference(ausleiheListNewAusleiheToAttach.getClass(), ausleiheListNewAusleiheToAttach.getLeihId());
                attachedAusleiheListNew.add(ausleiheListNewAusleiheToAttach);
            }
            ausleiheListNew = attachedAusleiheListNew;
            exemplar.setAusleiheList(ausleiheListNew);
            exemplar = em.merge(exemplar);
            if (verlagbuchIdOld != null && !verlagbuchIdOld.equals(verlagbuchIdNew)) {
                verlagbuchIdOld.getExemplarList().remove(exemplar);
                verlagbuchIdOld = em.merge(verlagbuchIdOld);
            }
            if (verlagbuchIdNew != null && !verlagbuchIdNew.equals(verlagbuchIdOld)) {
                verlagbuchIdNew.getExemplarList().add(exemplar);
                verlagbuchIdNew = em.merge(verlagbuchIdNew);
            }
            for (Ausleihe ausleiheListNewAusleihe : ausleiheListNew) {
                if (!ausleiheListOld.contains(ausleiheListNewAusleihe)) {
                    Exemplar oldExemplarIdOfAusleiheListNewAusleihe = ausleiheListNewAusleihe.getExemplarId();
                    ausleiheListNewAusleihe.setExemplarId(exemplar);
                    ausleiheListNewAusleihe = em.merge(ausleiheListNewAusleihe);
                    if (oldExemplarIdOfAusleiheListNewAusleihe != null && !oldExemplarIdOfAusleiheListNewAusleihe.equals(exemplar)) {
                        oldExemplarIdOfAusleiheListNewAusleihe.getAusleiheList().remove(ausleiheListNewAusleihe);
                        oldExemplarIdOfAusleiheListNewAusleihe = em.merge(oldExemplarIdOfAusleiheListNewAusleihe);
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
                Integer id = exemplar.getExemplarId();
                if (findExemplar(id) == null) {
                    throw new NonexistentEntityException("The exemplar with id " + id + " no longer exists.");
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
            Exemplar exemplar;
            try {
                exemplar = em.getReference(Exemplar.class, id);
                exemplar.getExemplarId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The exemplar with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Ausleihe> ausleiheListOrphanCheck = exemplar.getAusleiheList();
            for (Ausleihe ausleiheListOrphanCheckAusleihe : ausleiheListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<>();
                }
                illegalOrphanMessages.add("This Exemplar (" + exemplar + ") cannot be destroyed since the Ausleihe " + ausleiheListOrphanCheckAusleihe + " in its ausleiheList field has a non-nullable exemplarId field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Verlagbuch verlagbuchId = exemplar.getVerlagbuchId();
            if (verlagbuchId != null) {
                verlagbuchId.getExemplarList().remove(exemplar);
                verlagbuchId = em.merge(verlagbuchId);
            }
            em.remove(exemplar);
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

    public List<Exemplar> findExemplarEntities() {
        return findExemplarEntities(true, -1, -1);
    }

    public List<Exemplar> findExemplarEntities(int maxResults, int firstResult) {
        return findExemplarEntities(false, maxResults, firstResult);
    }

    private List<Exemplar> findExemplarEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Exemplar.class));
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

    public Exemplar findExemplar(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Exemplar.class, id);
        } finally {
            em.close();
        }
    }

    public int getExemplarCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Exemplar> rt = cq.from(Exemplar.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
