/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package listener;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

/**
 *
 * @author Brian GOHIER
 */
public class SessionListener implements HttpSessionListener
{
    private static int sessionCount = 0;

    public int getSessionCount() {
        return sessionCount;
    }
 
    @Override
    public void sessionCreated(HttpSessionEvent event) {
        synchronized (this) {
            sessionCount++;
        }
 
        System.out.println("Session Created: " + event.getSession().getId());
        System.out.println("Total Sessions: " + sessionCount);
    }
 
    @Override
    public void sessionDestroyed(HttpSessionEvent event) {
        synchronized (this) {
            sessionCount--;
        }
        System.out.println("Session Destroyed: " + event.getSession().getId());
        System.out.println("Total Sessions: " + sessionCount);
    }
}
