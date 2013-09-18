/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bean.view;

import bean.UserLogin;
import bean.Utils;
import bean.facade.UsersFacade;
import bean.view.struct.EntityView;
import entity.Users;
import java.util.List;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

/**
 *
 * @author Brian GOHIER
 */
@Named(value = "usersView")
@SessionScoped
public class UsersView extends EntityView<Users, UsersFacade>
{
    private static final long serialVersionUID = 1L;
    @EJB
    private UsersFacade UsersFacade;

    public UsersView()
    {
        super(Users.class,"users");
    }
    
    @Override
    public String entityUpdate(Users entity)
    {
        return super.entityUpdate(entity);
    }
    
    public String entityUpdate(Users entity, UserLogin currenUsers)
    {
        super.entityUpdate(entity);
        if(currenUsers.getRights().equals(Utils.ADMIN_RIGHTS))
        {
            return super.entityUpdate(entity);
        }
        return "/restricted/user/user/update";
    }
    
    public String update(UserLogin currenUsers)
    {
        this.setEntity(this.UsersFacade.update(this.getEntity()));
        // Si on est en mode utilisateur
        if(currenUsers!=null)
        {
            currenUsers.setUser(this.getEntity());
            return "view";
        }
        // Si on est en mode administrateur
        return "list";
    }
    
    public String getAdmin_Rights()
    {
        return Utils.ADMIN_RIGHTS;
    }
    
    public String geUsers_Rights()
    {
        return Utils.USER_RIGHTS;
    }
    
    public String getUnknown_Rights()
    {
        return Utils.UNKNOWN_RIGHTS;
    }
    
    @Override
    public String getDeleteMessage(Users entity)
    {
        return "Vous êtes sur le point de supprimer définitivement"
                + " cet utilisateur ("+entity.getFirstname()+" "+entity.getName()
                + " id="+entity.getId()+"). Cette action est irreversible,"
                + " êtes-vous certain(e) de vouloir continuer?";
    }
    
    @Override
    public List<Users> getEntries()
    {
        return super.findAll();
    }

    @Override
    public void setFacade()
    {
        super.setEntityFacade(this.UsersFacade);
    }

    @Override
    public Users getEntity()
    {
        return super.getInstance();
    }

    @Override
    public void setEntity(Users entity)
    {
        super.setInstance(entity);
    }
}
