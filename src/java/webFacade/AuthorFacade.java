/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webFacade;

import data.Author;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Susanne Otto
 */
@Stateless
public class AuthorFacade extends AbstractFacade<Author> {
    @PersistenceContext(unitName = "Buchbox-webPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    /**
     * Standardkonstruktor
     */
    public AuthorFacade() {
        super(Author.class);
    }
    
}
