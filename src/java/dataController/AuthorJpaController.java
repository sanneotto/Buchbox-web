package dataController;

import data.Author;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import data.Genre;
import data.Buchauthor;
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
public class AuthorJpaController implements Serializable {

    public AuthorJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Author author) throws RollbackFailureException, Exception {
        if (author.getBuchauthorList() == null) {
            author.setBuchauthorList(new ArrayList<Buchauthor>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Genre authorGenre = author.getAuthorGenre();
            if (authorGenre != null) {
                authorGenre = em.getReference(authorGenre.getClass(), authorGenre.getGenreId());
                author.setAuthorGenre(authorGenre);
            }
            List<Buchauthor> attachedBuchauthorList = new ArrayList<>();
            for (Buchauthor buchauthorListBuchauthorToAttach : author.getBuchauthorList()) {
                buchauthorListBuchauthorToAttach = em.getReference(buchauthorListBuchauthorToAttach.getClass(), buchauthorListBuchauthorToAttach.getBuchauthorId());
                attachedBuchauthorList.add(buchauthorListBuchauthorToAttach);
            }
            author.setBuchauthorList(attachedBuchauthorList);
            em.persist(author);
            if (authorGenre != null) {
                authorGenre.getAuthorList().add(author);
                authorGenre = em.merge(authorGenre);
            }
            for (Buchauthor buchauthorListBuchauthor : author.getBuchauthorList()) {
                Author oldAuthorIdOfBuchauthorListBuchauthor = buchauthorListBuchauthor.getAuthorId();
                buchauthorListBuchauthor.setAuthorId(author);
                buchauthorListBuchauthor = em.merge(buchauthorListBuchauthor);
                if (oldAuthorIdOfBuchauthorListBuchauthor != null) {
                    oldAuthorIdOfBuchauthorListBuchauthor.getBuchauthorList().remove(buchauthorListBuchauthor);
                    oldAuthorIdOfBuchauthorListBuchauthor = em.merge(oldAuthorIdOfBuchauthorListBuchauthor);
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

    public void edit(Author author) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Author persistentAuthor = em.find(Author.class, author.getAuthorId());
            Genre authorGenreOld = persistentAuthor.getAuthorGenre();
            Genre authorGenreNew = author.getAuthorGenre();
            List<Buchauthor> buchauthorListOld = persistentAuthor.getBuchauthorList();
            List<Buchauthor> buchauthorListNew = author.getBuchauthorList();
            List<String> illegalOrphanMessages = null;
            for (Buchauthor buchauthorListOldBuchauthor : buchauthorListOld) {
                if (!buchauthorListNew.contains(buchauthorListOldBuchauthor)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<>();
                    }
                    illegalOrphanMessages.add("You must retain Buchauthor " + buchauthorListOldBuchauthor + " since its authorId field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (authorGenreNew != null) {
                authorGenreNew = em.getReference(authorGenreNew.getClass(), authorGenreNew.getGenreId());
                author.setAuthorGenre(authorGenreNew);
            }
            List<Buchauthor> attachedBuchauthorListNew = new ArrayList<>();
            for (Buchauthor buchauthorListNewBuchauthorToAttach : buchauthorListNew) {
                buchauthorListNewBuchauthorToAttach = em.getReference(buchauthorListNewBuchauthorToAttach.getClass(), buchauthorListNewBuchauthorToAttach.getBuchauthorId());
                attachedBuchauthorListNew.add(buchauthorListNewBuchauthorToAttach);
            }
            buchauthorListNew = attachedBuchauthorListNew;
            author.setBuchauthorList(buchauthorListNew);
            author = em.merge(author);
            if (authorGenreOld != null && !authorGenreOld.equals(authorGenreNew)) {
                authorGenreOld.getAuthorList().remove(author);
                authorGenreOld = em.merge(authorGenreOld);
            }
            if (authorGenreNew != null && !authorGenreNew.equals(authorGenreOld)) {
                authorGenreNew.getAuthorList().add(author);
                authorGenreNew = em.merge(authorGenreNew);
            }
            for (Buchauthor buchauthorListNewBuchauthor : buchauthorListNew) {
                if (!buchauthorListOld.contains(buchauthorListNewBuchauthor)) {
                    Author oldAuthorIdOfBuchauthorListNewBuchauthor = buchauthorListNewBuchauthor.getAuthorId();
                    buchauthorListNewBuchauthor.setAuthorId(author);
                    buchauthorListNewBuchauthor = em.merge(buchauthorListNewBuchauthor);
                    if (oldAuthorIdOfBuchauthorListNewBuchauthor != null && !oldAuthorIdOfBuchauthorListNewBuchauthor.equals(author)) {
                        oldAuthorIdOfBuchauthorListNewBuchauthor.getBuchauthorList().remove(buchauthorListNewBuchauthor);
                        oldAuthorIdOfBuchauthorListNewBuchauthor = em.merge(oldAuthorIdOfBuchauthorListNewBuchauthor);
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
                Integer id = author.getAuthorId();
                if (findAuthor(id) == null) {
                    throw new NonexistentEntityException("The author with id " + id + " no longer exists.");
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
            Author author;
            try {
                author = em.getReference(Author.class, id);
                author.getAuthorId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The author with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Buchauthor> buchauthorListOrphanCheck = author.getBuchauthorList();
            for (Buchauthor buchauthorListOrphanCheckBuchauthor : buchauthorListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<>();
                }
                illegalOrphanMessages.add("This Author (" + author + ") cannot be destroyed since the Buchauthor " + buchauthorListOrphanCheckBuchauthor + " in its buchauthorList field has a non-nullable authorId field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Genre authorGenre = author.getAuthorGenre();
            if (authorGenre != null) {
                authorGenre.getAuthorList().remove(author);
                authorGenre = em.merge(authorGenre);
            }
            em.remove(author);
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

    public List<Author> findAuthorEntities() {
        return findAuthorEntities(true, -1, -1);
    }

    public List<Author> findAuthorEntities(int maxResults, int firstResult) {
        return findAuthorEntities(false, maxResults, firstResult);
    }

    private List<Author> findAuthorEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Author.class));
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

    public Author findAuthor(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Author.class, id);
        } finally {
            em.close();
        }
    }

    public int getAuthorCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Author> rt = cq.from(Author.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
