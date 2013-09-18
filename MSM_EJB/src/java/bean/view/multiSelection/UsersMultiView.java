/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bean.view.multiSelection;

import bean.view.multiSelection.struct.EntityMultiView;
import entity.Users;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

/**
 *
 * @author Brian GOHIER
 */
@Named(value = "usersMultiView")
@SessionScoped
public class UsersMultiView extends EntityMultiView<Users>
{
    private static final long serialVersionUID = 1L;
    
    public UsersMultiView()
    {
        super(Users.class);
    }
}
