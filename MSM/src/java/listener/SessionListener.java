/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package listener;

import bean.UserLogin;
import javax.inject.Inject;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

/**
 *
 * @author Brian GOHIER
 */
@WebListener()
public class SessionListener implements HttpSessionListener
{
//    @Inject
//    private UserLogin userLogin;
    
    @Override
    public void sessionCreated(HttpSessionEvent event)
    {
        System.out.println("Session Created: " + event.getSession().getId());
    }
 
    @Override
    public void sessionDestroyed(HttpSessionEvent event)
    {
        System.out.println("Session Destroyed: " + event.getSession().getId());
    }
}
