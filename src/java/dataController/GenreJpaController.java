package dataController;

import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import data.Author;
import java.util.ArrayList;
import java.util.List;
import data.Genre;
import dataController.exceptions.IllegalOrphanException;
import dataController.exceptions.NonexistentEntityException;
import dataController.exceptions.RollbackFailureException;
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
public class GenreJpaController implements Serializable {

    public GenreJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Genre genre) throws RollbackFailureException, Exception {
        if (genre.getAuthorList() == null) {
            genre.setAuthorList(new ArrayList<Author>());
        }

        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            List<Author> attachedAuthorList = new ArrayList<>();
            for (Author authorListAuthorToAttach : genre.getAuthorList()) {
                authorListAuthorToAttach = em.getReference(authorListAuthorToAttach.getClass(), authorListAuthorToAttach.getAuthorId());
                attachedAuthorList.add(authorListAuthorToAttach);
            }
            genre.setAuthorList(attachedAuthorList);

            em.persist(genre);
            for (Author authorListAuthor : genre.getAuthorList()) {
                Genre oldAuthorGenreOfAuthorListAuthor = authorListAuthor.getAuthorGenre();
                authorListAuthor.setAuthorGenre(genre);
                authorListAuthor = em.merge(authorListAuthor);
                if (oldAuthorGenreOfAuthorListAuthor != null) {
                    oldAuthorGenreOfAuthorListAuthor.getAuthorList().remove(authorListAuthor);
                    oldAuthorGenreOfAuthorListAuthor = em.merge(oldAuthorGenreOfAuthorListAuthor);
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

    public void edit(Genre genre) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Genre persistentGenre = em.find(Genre.class, genre.getGenreId());
            List<Author> authorListOld = persistentGenre.getAuthorList();
            List<Author> authorListNew = genre.getAuthorList();

            List<String> illegalOrphanMessages = null;
            for (Author authorListOldAuthor : authorListOld) {
                if (!authorListNew.contains(authorListOldAuthor)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<>();
                    }
                    illegalOrphanMessages.add("You must retain Author " + authorListOldAuthor + " since its authorGenre field is not nullable.");
                }
            }

            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<Author> attachedAuthorListNew = new ArrayList<>();
            for (Author authorListNewAuthorToAttach : authorListNew) {
                authorListNewAuthorToAttach = em.getReference(authorListNewAuthorToAttach.getClass(), authorListNewAuthorToAttach.getAuthorId());
                attachedAuthorListNew.add(authorListNewAuthorToAttach);
            }
            authorListNew = attachedAuthorListNew;
            genre.setAuthorList(authorListNew);

            genre = em.merge(genre);
            for (Author authorListNewAuthor : authorListNew) {
                if (!authorListOld.contains(authorListNewAuthor)) {
                    Genre oldAuthorGenreOfAuthorListNewAuthor = authorListNewAuthor.getAuthorGenre();
                    authorListNewAuthor.setAuthorGenre(genre);
                    authorListNewAuthor = em.merge(authorListNewAuthor);
                    if (oldAuthorGenreOfAuthorListNewAuthor != null && !oldAuthorGenreOfAuthorListNewAuthor.equals(genre)) {
                        oldAuthorGenreOfAuthorListNewAuthor.getAuthorList().remove(authorListNewAuthor);
                        oldAuthorGenreOfAuthorListNewAuthor = em.merge(oldAuthorGenreOfAuthorListNewAuthor);
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
                Integer id = genre.getGenreId();
                if (findGenre(id) == null) {
                    throw new NonexistentEntityException("The genre with id " + id + " no longer exists.");
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
            Genre genre;
            try {
                genre = em.getReference(Genre.class, id);
                genre.getGenreId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The genre with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Author> authorListOrphanCheck = genre.getAuthorList();
            for (Author authorListOrphanCheckAuthor : authorListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<>();
                }
                illegalOrphanMessages.add("This Genre (" + genre + ") cannot be destroyed since the Author " + authorListOrphanCheckAuthor + " in its authorList field has a non-nullable authorGenre field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(genre);
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

    public List<Genre> findGenreEntities() {
        return findGenreEntities(true, -1, -1);
    }

    public List<Genre> findGenreEntities(int maxResults, int firstResult) {
        return findGenreEntities(false, maxResults, firstResult);
    }

    private List<Genre> findGenreEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Genre.class));
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

    public Genre findGenre(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Genre.class, id);
        } finally {
            em.close();
        }
    }

    public int getGenreCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Genre> rt = cq.from(Genre.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
