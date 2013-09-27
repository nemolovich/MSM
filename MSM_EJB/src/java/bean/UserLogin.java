/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bean;

import bean.chat.Chat;
import bean.facade.UsersFacade;
import bean.log.ApplicationLogger;
import entity.Users;
import java.io.Serializable;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import org.primefaces.event.ResizeEvent; 
import org.primefaces.event.ToggleEvent;
import org.primefaces.model.Visibility;
import org.primefaces.push.PushContext;
import org.primefaces.push.PushContextFactory;

/**
 *
 * @author Brian GOHIER
 */
@Named(value="userLogin")
@SessionScoped
//@Stateless
public class UserLogin implements Serializable
{
    private static final long serialVersionUID = 1L;
    
    private Users user;
    @EJB
    private UsersFacade usersFacade;
    private String loginMail="";
    private String template="unknown";
    private int loginTry=0;
    private long nextTryTime;
    private boolean blocked=false;
    // ************************************************
    // Code konami: UP UP DOWN DOWN LEFT RIGHT LEFT RIGHT B A
    // ************************************************
    private static final String konamiCode="uuddlrlrba";
    private String konami="";
    private long previousHit=Calendar.getInstance().getTimeInMillis();
    // ************************************************
    // Stateful infos pour les menus
    // ************************************************
    /**
     * Identifiant du menu de gauche
     */
    private static final String menuLeftID="menu";
    /**
     * Identifiant du menu de droite
     */
    private static final String menuRightID="menu_right";
    /**
     * Identifiant de l'en-tête
     */
    private static final String headerID="header";
    /**
     * Taille du menu de droite
     */
    private int menuRightSize=370;
    /**
     * Taille du menu de gauche
     */
    private int menuLeftSize=230;
    /**
     * Taille de l'en-tête
     */
    private int headerSize=92;
    /**
     * Définit si le menu de gauche est caché
     */
    private boolean menuLeftClosed=false;
    /**
     * Définit si le menu de droite est caché
     */
    private boolean menuRightClosed=false;
    /**
     * Définit si l'en-tête est caché
     */
    private boolean headerClosed=false;
    /**
     * URL demandée
     */
    private String askedURL=null;
    /**
     * Chat with other users
     */
    private Chat chat;
    /**
     * The queue messages
     */
    private Map<String,Object> queue=new HashMap<String,Object>();
    /**
     * Ids to update
     */
    private String scripts;
    
    public UserLogin()
    {
    }

    public Chat getChat() {
        return chat;
    }

    public void setChat(Chat chat) {
        this.chat = chat;
    }

    public String getScripts() {
        return scripts;
    }

    public void setScripts(String updates) {
        this.scripts = updates;
    }
    
    public String getMenuLeftID()
    {
        return UserLogin.menuLeftID;
    }

    public String getMenuRightID()
    {
        return UserLogin.menuRightID;
    }

    public String getHeaderID()
    {
        return UserLogin.headerID;
    }
    
    public void broadCast(String key, Object content)
    {
        if(this.user==null)
        {
            return;
        }
        for(UserLogin ul:ConnectedUser.USERS_LIST())
        {
            if(!ul.equals(this))
            {
                Map<String, Object> map=new HashMap<String, Object>();
                map.put(key, content);
                ul.addQueue(map);
                PushContext pushContext = PushContextFactory.getDefault().getPushContext();
                pushContext.push("/queue"+ul.getUser().getId(),"newQueue");
            }
        }
    }
    
    public String useQueue()
    {
        if(this.queue.isEmpty())
        {
            return "";
        }
        for(String elmt:this.queue.keySet())
        {
            if(elmt.equals("user")&&this.queue.containsKey("message"))
            {
                Users u=(Users) this.queue.get(elmt);
                Utils.displayMessage(
                        FacesMessage.SEVERITY_INFO,
                        "You got a message",u.getFirstname()+" "+
                        u.getName()+" sent you a message");
                break;
            }
            else if(elmt.equals("message"))
            {
            }
            else if(elmt.equals("connexion"))
            {
                Users u=(Users) this.queue.get(elmt);
                Utils.displayMessage(
                        FacesMessage.SEVERITY_INFO,
                        "User connected",u.getFirstname()+" "+
                        u.getName()+" is now connected");
                break;
            }
            else
            {
                Utils.displayMessage(
                        FacesMessage.SEVERITY_INFO,
                        "You got a '"+elmt+"'",
                        this.queue.get(elmt).toString());
            }
        }
        this.queue.clear();
        return "";
    }
    
    public void addQueue(Map<String,Object> elmts)
    {
        for(String elmt:elmts.keySet())
        {
            this.queue.put(elmt,elmts.get(elmt));
        }
    }
    
    public Map<String,Object> getQueue()
    {
        return this.queue;
    }
    
    public boolean isEmptyQueue()
    {
        return this.queue.isEmpty();
    }
    
    public void newMessage(Users user,String message)
    {
        Map<String,Object> map=new HashMap<String, Object>();
        map.put("user", user);
        map.put("message", message);
        this.addQueue(map);
    }
    
    /**
     * Action de cacher ou afficher un menu
     * @param event {@link ToogleEvent} - Evénement déclencheur
     */
    public void handleToggle(ToggleEvent event)
    {
        if(event.getComponent().getAttributes().get("id").equals(UserLogin.menuLeftID))
        {
            this.menuLeftClosed=event.getVisibility().equals(Visibility.HIDDEN);
        }
        else if(event.getComponent().getAttributes().get("id").equals(UserLogin.menuRightID))
        {
            this.menuRightClosed=event.getVisibility().equals(Visibility.HIDDEN);
        }
    }

    /**
     * Action au redimensionnement d'un menu
     * @param event {@link ResizeEvent} - Evénement déclencheur
     */
    public void handleResize(ResizeEvent event)
    {
        try
        {
            if(event.getComponent().getAttributes().get("id").equals(UserLogin.menuLeftID))
            {
                this.menuLeftSize=Integer.valueOf((String)event.getComponent().getAttributes().get("size"))+5;
            }
            else if(event.getComponent().getAttributes().get("id").equals(UserLogin.menuRightID))
            {
                this.menuRightSize=Integer.valueOf((String)event.getComponent().getAttributes().get("size"))+5;
            }
            else if(event.getComponent().getAttributes().get("id").equals(UserLogin.headerID))
            {
                this.headerSize=Integer.valueOf((String)event.getComponent().getAttributes().get("size"))+5;
                this.headerClosed=this.headerSize<10;
            }
        }
        catch(NumberFormatException nfe)
        {
            // Si la taille est à "auto"
        }
    }

    public String getAskedURL()
    {
        return askedURL;
    }

    public void setAskedURL(String askedURL)
    {
        this.askedURL = askedURL;
    }

    public int getMenuLeftSize()
    {
        return menuLeftSize;
    }

    public void setMenuLeftSize(int menuLeftSize)
    {
        this.menuLeftSize = menuLeftSize;
    }

    public int getMenuRightSize()
    {
        return menuRightSize;
    }

    public void setMenuRightSize(int menuRightSize)
    {
        this.menuRightSize = menuRightSize;
    }

    public int getHeaderSize()
    {
        return headerSize;
    }

    public void setHeaderSize(int headerSize)
    {
        this.headerSize = headerSize;
    }

    public boolean isMenuLeftClosed()
    {
        return this.menuLeftClosed;
    }

    public void setMenuLeftClosed(boolean menuLeftClosed)
    {
        this.menuLeftClosed = menuLeftClosed;
    }

    public boolean isMenuRightClosed()
    {
        return this.menuRightClosed;
    }

    public void setMenuRightClosed(boolean menuRightClosed)
    {
        this.menuRightClosed = menuRightClosed;
    }

    public boolean isHeaderClosed()
    {
        return this.headerClosed;
    }

    public void setHeaderClosed(boolean headerClosed)
    {
        this.headerClosed = headerClosed;
    }
    
    public String getKonami()
    {
        return this.konami;
    }
    
    public void addKonami(char c)
    {
        long currentTime=Calendar.getInstance().getTimeInMillis();
        if(!this.konami.isEmpty()&&currentTime-this.previousHit>500)
        {
            this.konami="";
        }
        this.previousHit=currentTime;
        this.konami+=c;
        if(!UserLogin.konamiCode.startsWith(this.konami))
        {
            this.konami="";
        }
        else if(UserLogin.konamiCode.equals(this.konami))
        {
            FacesContext context=FacesContext.getCurrentInstance();
            context.getExternalContext().getFlash().setKeepMessages(true);
            FacesMessage message=new FacesMessage("Code Konami");
            if(this.blocked)
            {
                this.unLock();
                message.setSeverity(FacesMessage.SEVERITY_INFO);
                message.setDetail("Votre session a été débloquée");
            }
            else
            {
                if(this.getLogged())
                {
                    this.logout();
                }
                this.sessionBlockFor(60);
                message.setSeverity(FacesMessage.SEVERITY_FATAL);
                message.setDetail("Vous avez bloqué votre session pour une heure!!!");
            }
            context.addMessage(null, message);
            this.konami="";
        }
    }
    
    public void setLoginTry(int tryNumber)
    {
        this.loginTry = tryNumber;
    }
    
    public int getLoginTry()
    {
        return this.loginTry;
    }
    
    public void sessionBlockFor(int minutes)
    {
        ApplicationLogger.writeWarning("La session pour l'utilisateur: "+
                this.getFirstname()+" "+this.getName()+
                " ["+this.getRights()+"] a été bloquée pour "+minutes+" minutes");
        long currentTime=Calendar.getInstance().getTimeInMillis();
        this.nextTryTime=currentTime+60000*minutes;
        this.blocked=true;
    }
    
    public int getWaitTime()
    {
        long currentTime=Calendar.getInstance().getTimeInMillis();
        long remainingTime=this.nextTryTime-currentTime;
        if(remainingTime>0)
        {
            return (int)remainingTime/60000;
        }
        else
        {
            this.blocked=false;
            return -1;
        }
    }
    
    public boolean isBlocked()
    {
        return this.blocked;
    }
    
    public void unLock()
    {
        ApplicationLogger.writeWarning("La session pour l'utilisateur: "+
                this.getFirstname()+" "+this.getName()+
                " ["+this.getRights()+"] a été débloquée");
        this.blocked=false;
    }
    
    public void setTemplate(String template)
    {
        this.template = template;
    }
    
    public String getTemplate()
    {
        return this.template;
    }
    
    public void setLoginMail(String loginMail)
    {
        this.loginMail = loginMail;
    }
    
    public String getLoginMail()
    {
        return this.loginMail==null?"":this.loginMail;
    }
    
    public Integer getId()
    {
        return this.user==null?-1:this.user.getId();
    }

    public void setId(Integer id)
    {
        this.user.setId(id);
    }

    public String getMail()
    {
        return this.user==null?"":this.user.getMail();
    }

    public void setMail(String mail)
    {
        this.user.setMail(mail);
    }

    public String getName()
    {
        return this.user==null?"":this.user.getName();
    }

    public void setName(String name)
    {
        this.user.setName(name);
    }

    public String getFirstname()
    {
        return this.user==null?"":this.user.getFirstname();
    }

    public void setFirstname(String firstname)
    {
        this.user.setFirstname(firstname);
    }

    public String getPassword()
    {
        return this.user==null?"":this.user.getPassword();
    }
    
    public void setPassword(String password)
    {
        this.user.setPassword(password);
    }
    
    public String getRights()
    {
        return this.user==null?Utils.UNKNOWN_RIGHTS:this.user.getRights();
    }
    
    public void setRights(String rights)
    {
        this.user.setRights(rights);
    }
    
    public void setUser(Users user)
    {
        ApplicationLogger.writeInfo("Connexion de l'utilisateur: "+
                user.toString());
        if(this.user!=null)
        {
//            ConnectedUser.deleteUserConnexion(this);
        }
        if(this.chat!=null)
        {
            this.chat.clear();
        }
        this.user=user;
        this.chat=new Chat(this.user);
        this.broadCast("connexion", user);
        ConnectedUser.addUserConnexion(this);
    }
    
    public Users getUser()
    {
        for(Users u:this.usersFacade.findAll())
        {
            if(u.equals(this.user))
            {
                return u;
            }
        }
        ApplicationLogger.writeWarning("Utilisateur non trouvé");
        return null;
    }
    
    public String login()
    {
        this.setTemplate(this.user.getRights().toLowerCase());
        if(this.askedURL!=null&&!this.getRights().equals(Utils.UNKNOWN_RIGHTS))
        {
            return this.askedURL;
        }
        return (this.user==null?null:
                (this.user.getRights().equals(Utils.ADMIN_RIGHTS)?"/restricted/admin/index":
                (this.user.getRights().equals(Utils.USER_RIGHTS)?"/restricted/user/index":
                "/restricted/login")));
    }
    
    public String saveAccount()
    {
        this.usersFacade.persist(this.user);
        return null;
    }
    
    public boolean getLogged()
    {
        return (this.user!=null&&this.user.getId()!=null&&this.user.getName()!=null);
    }
    
    public String logout()
    {
        ApplicationLogger.writeInfo("Déconnexion de l'utilisateur: "+
                this.user.toString());
        ConnectedUser.deleteUserConnexion(this);
        if(this.chat!=null)
        {
            this.chat.clear();
        }
        this.user=null;
        this.askedURL=null;
        this.template="unknown";
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        FacesMessage message=new FacesMessage(FacesMessage.SEVERITY_INFO,
                "Déconnecté",
                "Vous avez été déconnecté de votre session");
        FacesContext.getCurrentInstance().addMessage(null,message);
                
        return "/restricted/login";
    }
}