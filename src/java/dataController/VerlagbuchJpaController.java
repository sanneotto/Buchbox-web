package dataController;

import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import data.Verlag;
import data.Exemplar;
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
public class VerlagbuchJpaController implements Serializable {

    public VerlagbuchJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Verlagbuch verlagbuch) throws RollbackFailureException, Exception {
        if (verlagbuch.getExemplarList() == null) {
            verlagbuch.setExemplarList(new ArrayList<Exemplar>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Verlag verlagId = verlagbuch.getVerlagId();
            if (verlagId != null) {
                verlagId = em.getReference(verlagId.getClass(), verlagId.getVerlagId());
                verlagbuch.setVerlagId(verlagId);
            }
            List<Exemplar> attachedExemplarList = new ArrayList<Exemplar>();
            for (Exemplar exemplarListExemplarToAttach : verlagbuch.getExemplarList()) {
                exemplarListExemplarToAttach = em.getReference(exemplarListExemplarToAttach.getClass(), exemplarListExemplarToAttach.getExemplarId());
                attachedExemplarList.add(exemplarListExemplarToAttach);
            }
            verlagbuch.setExemplarList(attachedExemplarList);
            em.persist(verlagbuch);
            if (verlagId != null) {
                verlagId.getVerlagbuchList().add(verlagbuch);
                verlagId = em.merge(verlagId);
            }
            for (Exemplar exemplarListExemplar : verlagbuch.getExemplarList()) {
                Verlagbuch oldVerlagbuchIdOfExemplarListExemplar = exemplarListExemplar.getVerlagbuchId();
                exemplarListExemplar.setVerlagbuchId(verlagbuch);
                exemplarListExemplar = em.merge(exemplarListExemplar);
                if (oldVerlagbuchIdOfExemplarListExemplar != null) {
                    oldVerlagbuchIdOfExemplarListExemplar.getExemplarList().remove(exemplarListExemplar);
                    oldVerlagbuchIdOfExemplarListExemplar = em.merge(oldVerlagbuchIdOfExemplarListExemplar);
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

    public void edit(Verlagbuch verlagbuch) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Verlagbuch persistentVerlagbuch = em.find(Verlagbuch.class, verlagbuch.getVerlagbuchId());
            Verlag verlagIdOld = persistentVerlagbuch.getVerlagId();
            Verlag verlagIdNew = verlagbuch.getVerlagId();
            List<Exemplar> exemplarListOld = persistentVerlagbuch.getExemplarList();
            List<Exemplar> exemplarListNew = verlagbuch.getExemplarList();
            List<String> illegalOrphanMessages = null;
            for (Exemplar exemplarListOldExemplar : exemplarListOld) {
                if (!exemplarListNew.contains(exemplarListOldExemplar)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<>();
                    }
                    illegalOrphanMessages.add("You must retain Exemplar " + exemplarListOldExemplar + " since its verlagbuchId field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (verlagIdNew != null) {
                verlagIdNew = em.getReference(verlagIdNew.getClass(), verlagIdNew.getVerlagId());
                verlagbuch.setVerlagId(verlagIdNew);
            }
            List<Exemplar> attachedExemplarListNew = new ArrayList<>();
            for (Exemplar exemplarListNewExemplarToAttach : exemplarListNew) {
                exemplarListNewExemplarToAttach = em.getReference(exemplarListNewExemplarToAttach.getClass(), exemplarListNewExemplarToAttach.getExemplarId());
                attachedExemplarListNew.add(exemplarListNewExemplarToAttach);
            }
            exemplarListNew = attachedExemplarListNew;
            verlagbuch.setExemplarList(exemplarListNew);
            verlagbuch = em.merge(verlagbuch);
            if (verlagIdOld != null && !verlagIdOld.equals(verlagIdNew)) {
                verlagIdOld.getVerlagbuchList().remove(verlagbuch);
                verlagIdOld = em.merge(verlagIdOld);
            }
            if (verlagIdNew != null && !verlagIdNew.equals(verlagIdOld)) {
                verlagIdNew.getVerlagbuchList().add(verlagbuch);
                verlagIdNew = em.merge(verlagIdNew);
            }
            for (Exemplar exemplarListNewExemplar : exemplarListNew) {
                if (!exemplarListOld.contains(exemplarListNewExemplar)) {
                    Verlagbuch oldVerlagbuchIdOfExemplarListNewExemplar = exemplarListNewExemplar.getVerlagbuchId();
                    exemplarListNewExemplar.setVerlagbuchId(verlagbuch);
                    exemplarListNewExemplar = em.merge(exemplarListNewExemplar);
                    if (oldVerlagbuchIdOfExemplarListNewExemplar != null && !oldVerlagbuchIdOfExemplarListNewExemplar.equals(verlagbuch)) {
                        oldVerlagbuchIdOfExemplarListNewExemplar.getExemplarList().remove(exemplarListNewExemplar);
                        oldVerlagbuchIdOfExemplarListNewExemplar = em.merge(oldVerlagbuchIdOfExemplarListNewExemplar);
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
                Integer id = verlagbuch.getVerlagbuchId();
                if (findVerlagbuch(id) == null) {
                    throw new NonexistentEntityException("The verlagbuch with id " + id + " no longer exists.");
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
            Verlagbuch verlagbuch;
            try {
                verlagbuch = em.getReference(Verlagbuch.class, id);
                verlagbuch.getVerlagbuchId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The verlagbuch with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Exemplar> exemplarListOrphanCheck = verlagbuch.getExemplarList();
            for (Exemplar exemplarListOrphanCheckExemplar : exemplarListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<>();
                }
                illegalOrphanMessages.add("This Verlagbuch (" + verlagbuch + ") cannot be destroyed since the Exemplar " + exemplarListOrphanCheckExemplar + " in its exemplarList field has a non-nullable verlagbuchId field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Verlag verlagId = verlagbuch.getVerlagId();
            if (verlagId != null) {
                verlagId.getVerlagbuchList().remove(verlagbuch);
                verlagId = em.merge(verlagId);
            }
            em.remove(verlagbuch);
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

    public List<Verlagbuch> findVerlagbuchEntities() {
        return findVerlagbuchEntities(true, -1, -1);
    }

    public List<Verlagbuch> findVerlagbuchEntities(int maxResults, int firstResult) {
        return findVerlagbuchEntities(false, maxResults, firstResult);
    }

    private List<Verlagbuch> findVerlagbuchEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Verlagbuch.class));
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

    public Verlagbuch findVerlagbuch(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Verlagbuch.class, id);
        } finally {
            em.close();
        }
    }

    public int getVerlagbuchCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Verlagbuch> rt = cq.from(Verlagbuch.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
