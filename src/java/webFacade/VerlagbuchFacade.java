/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webFacade;

import data.Verlagbuch;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Susanne Otto
 */
@Stateless
public class VerlagbuchFacade extends AbstractFacade<Verlagbuch> {
    @PersistenceContext(unitName = "Buchbox-webPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    /**
     * Standardkonstruktor
     */
    public VerlagbuchFacade() {
        super(Verlagbuch.class);
    }
    
}
