/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bean.facade;

import bean.facade.abstracts.AbstractFacade;
import entity.Users;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Brian GOHIER
 */
@Stateless
public class UsersFacade extends AbstractFacade<Users>
{
    @PersistenceContext(unitName = "MSM_EJBPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager()
    {
        return this.em;
    }
    
    public UsersFacade()
    {
        super(Users.class);
    }
    
    public Users update(Users user)
    {  
        super.edit(user);
        return user;
    }
  
    public void persist(Object object)
    {  
        super.create((Users)object);  
    }
}
