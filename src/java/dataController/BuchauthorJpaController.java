package dataController;

import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import data.Author;
import data.Buchauthor;
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
public class BuchauthorJpaController implements Serializable {

    public BuchauthorJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Buchauthor buchauthor) throws RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Author authorId = buchauthor.getAuthorId();
            if (authorId != null) {
                authorId = em.getReference(authorId.getClass(), authorId.getAuthorId());
                buchauthor.setAuthorId(authorId);
            }
            em.persist(buchauthor);
            if (authorId != null) {
                authorId.getBuchauthorList().add(buchauthor);
                authorId = em.merge(authorId);
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

    public void edit(Buchauthor buchauthor) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Buchauthor persistentBuchauthor = em.find(Buchauthor.class, buchauthor.getBuchauthorId());
            Author authorIdOld = persistentBuchauthor.getAuthorId();
            Author authorIdNew = buchauthor.getAuthorId();
            if (authorIdNew != null) {
                authorIdNew = em.getReference(authorIdNew.getClass(), authorIdNew.getAuthorId());
                buchauthor.setAuthorId(authorIdNew);
            }
            buchauthor = em.merge(buchauthor);
            if (authorIdOld != null && !authorIdOld.equals(authorIdNew)) {
                authorIdOld.getBuchauthorList().remove(buchauthor);
                authorIdOld = em.merge(authorIdOld);
            }
            if (authorIdNew != null && !authorIdNew.equals(authorIdOld)) {
                authorIdNew.getBuchauthorList().add(buchauthor);
                authorIdNew = em.merge(authorIdNew);
            }
            utx.commit();
        } catch (NotSupportedException | SystemException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException ex) {
            try {
                utx.rollback();
            } catch (IllegalStateException | SecurityException | SystemException re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = buchauthor.getBuchauthorId();
                if (findBuchauthor(id) == null) {
                    throw new NonexistentEntityException("The buchauthor with id " + id + " no longer exists.");
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
            Buchauthor buchauthor;
            try {
                buchauthor = em.getReference(Buchauthor.class, id);
                buchauthor.getBuchauthorId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The buchauthor with id " + id + " no longer exists.", enfe);
            }
            Author authorId = buchauthor.getAuthorId();
            if (authorId != null) {
                authorId.getBuchauthorList().remove(buchauthor);
                authorId = em.merge(authorId);
            }
            em.remove(buchauthor);
            utx.commit();
        } catch (NotSupportedException | SystemException | NonexistentEntityException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException ex) {
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

    public List<Buchauthor> findBuchauthorEntities() {
        return findBuchauthorEntities(true, -1, -1);
    }

    public List<Buchauthor> findBuchauthorEntities(int maxResults, int firstResult) {
        return findBuchauthorEntities(false, maxResults, firstResult);
    }

    private List<Buchauthor> findBuchauthorEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Buchauthor.class));
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

    public Buchauthor findBuchauthor(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Buchauthor.class, id);
        } finally {
            em.close();
        }
    }

    public int getBuchauthorCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Buchauthor> rt = cq.from(Buchauthor.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
