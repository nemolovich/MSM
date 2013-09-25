/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bean;

import bean.facade.UsersFacade;
import entity.Users;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;

/**
 *
 * @author nemo
 */
public class ConnectedUser implements Serializable
{
    /**
     * List of connected users
     */
    private static List<Users> USERS_LIST = new ArrayList<Users>();
    private static int USERS_LIST_SIZE = 0;
    private Users user;
    @EJB
    private UsersFacade usersFacade;

    public ConnectedUser() {
    }

    public static List<Users> USERS_LIST() {
        return USERS_LIST;
    }

    public static int USERS_LIST_SIZE() {
        return USERS_LIST_SIZE;
    }
    
    /**
     * Add an user to users list
     * @param user {@link Users} - User to add in list
     */
    public static synchronized void addUserConnexion(Users user)
    {
        USERS_LIST.add(user);
        USERS_LIST_SIZE++;
    }
    
    /**
     * Remove an user from users list
     * @param user {@link Users} - User to remove from list
     */
    public static synchronized void deleteUserConnexion(Users user)
    {
        USERS_LIST.remove(user);
        USERS_LIST_SIZE--;
    }
    
    public List<Users> getAllUsers()
    {
        return this.usersFacade.findAll();
    }

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }
}
