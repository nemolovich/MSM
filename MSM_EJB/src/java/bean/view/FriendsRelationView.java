/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bean.view;

import bean.facade.FriendsRelationFacade;
import bean.view.struct.EntityView;
import entity.FriendsRelation;
import java.util.List;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

/**
 *
 * @author Brian GOHIER
 */
@Named(value = "friendsRelationView")
@SessionScoped
public class FriendsRelationView extends EntityView<FriendsRelation, FriendsRelationFacade>
{
    private static final long serialVersionUID = 1L;
    @EJB
    private FriendsRelationFacade FriendsRelationFacade;

    public FriendsRelationView()
    {
        super(FriendsRelation.class,"relations/friends");
    }
    
    @Override
    public String getDeleteMessage(FriendsRelation entity)
    {
        return "Vous êtes sur le point de supprimer définitivement"
                + " cette relation entre les utilisateurs "+entity.getFirstId()+" "
                + " et "+entity.getSecondId()+". Cette action est irreversible,"
                + " êtes-vous certain(e) de vouloir continuer?";
    }
    
    @Override
    public List<FriendsRelation> getEntries()
    {
        return super.findAll();
    }

    @Override
    public void setFacade()
    {
        super.setEntityFacade(this.FriendsRelationFacade);
    }

    @Override
    public FriendsRelation getEntity()
    {
        return super.getInstance();
    }

    @Override
    public void setEntity(FriendsRelation entity)
    {
        super.setInstance(entity);
    }
}
