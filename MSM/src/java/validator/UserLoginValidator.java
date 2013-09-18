/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package validator;

import bean.UserLogin;
import bean.facade.UsersFacade;
import bean.log.ApplicationLogger;
import entity.Users;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;

/**
 *
 * @author Brian GOHIER
 */
@FacesValidator("userLoginValidator")
@ManagedBean
@RequestScoped
public class UserLoginValidator implements Validator
{
    @EJB
    private UsersFacade usersFacade;
    @Inject
    private UserLogin userLogin;

    public UserLoginValidator()
    {
    }
    
    @Override
    public void validate(FacesContext context, UIComponent component, Object value)
            throws ValidatorException
    {
        String mail = (String) value;
        UIInput passwordComponent = (UIInput) component.getAttributes().get("password");
        String password = (String)passwordComponent.getSubmittedValue();

        if (mail == null || mail.isEmpty())
        {
            FacesMessage message=new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Champs 'Mail' vide","Veuillez entrer votre adresse mail");
            throw new ValidatorException(message);
        }
        if (password == null || password.isEmpty())
        {
            passwordComponent.setValid(false);
            FacesMessage message=new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Champs 'Mot de passe' vide","Veuillez entrer votre mot de passe");
            throw new ValidatorException(message);
        }
        
        List<Users> users;
        users = this.usersFacade.findAll();
        for(Users user:users)
        {
            if(user.getMail().equals(mail))
            {
                if(this.userLogin.isBlocked())
                {
                    String timeToWait="";
                    int remainingTime=this.userLogin.getWaitTime();
                    if(remainingTime>=0)
                    {
                        if(remainingTime>0)
                        {
                            timeToWait=remainingTime+" minutes";
                        }
                        else if(remainingTime==0)
                        {
                            timeToWait="moins d'une minute";                        
                        }
                        FacesMessage message=new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                "Session bloquée",
                                "Votre session est bloquée. Prochain essai dans: "+timeToWait);
                        throw new ValidatorException(message);
                    }
                    else
                    {
                        this.userLogin.unLock();
                    }
                }
                MessageDigest md;
                try
                {
                    md = MessageDigest.getInstance("MD5");
                }
                catch (NoSuchAlgorithmException ex)
                {
                    ApplicationLogger.writeError("Cryptage du mot de passe imposible", ex);
                    return;
                }
                md.update(password.getBytes(/*"UTF-8"*/));
                byte[] md5 = md.digest();
                String passwordEncrypted=new String(md5/*,"UTF-8"*/);
                
                if(!user.getPassword().equals(passwordEncrypted))
                {
                    this.userLogin.setLoginTry(this.userLogin.getLoginTry()+1);
                    if(this.userLogin.getLoginTry()>=3)
                    {
                        int blockTime=0;
                        if(this.userLogin.getLoginTry()==3)
                        {
                            blockTime=5;
                        }
                        else if(this.userLogin.getLoginTry()==4)
                        {
                            blockTime=10;
                        }
                        else if(this.userLogin.getLoginTry()>=5)
                        {
                            blockTime=15;                            
                        }
                        this.userLogin.sessionBlockFor(blockTime);
                        FacesMessage message=new FacesMessage(FacesMessage.SEVERITY_FATAL,
                                "Session bloquée",
                                "Votre mot de passe est incorrect, "
                                + "votre session sera bloquée pendant "+blockTime+" minutes");
                        throw new ValidatorException(message);
                    }
                    passwordComponent.setValid(false);
                    FacesMessage moreDetails=null;
                    if(this.userLogin.getLoginTry()==2)
                    {
                        moreDetails=new FacesMessage(FacesMessage.SEVERITY_WARN,
                                "Erreur d'autentification",
                                "Attention il ne vous reste qu'un essai avant de "
                                + "bloquer votre session");
                    }
                    if(moreDetails!=null)
                    {
                        FacesContext.getCurrentInstance().addMessage(null,
                                moreDetails);
                    }
                    FacesMessage message=new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Erreur d'autentification",
                            "Votre mot de passe est incorrect");
                    throw new ValidatorException(message);
                }
                
                this.userLogin.setLoginTry(0);
                this.userLogin.setUser(user);
                FacesMessage message=new FacesMessage(FacesMessage.SEVERITY_INFO,
                        "Autentification réussie",
                        "Bienvenue "+this.userLogin.getFirstname()+
                        " "+this.userLogin.getName());
                context.getExternalContext().getFlash().setKeepMessages(true);
                context.addMessage(null, message);
                return;
            }
        }
        FacesMessage message=new FacesMessage(FacesMessage.SEVERITY_ERROR,
                "Login inconnu",
                "Votre adresse mail n'a pas été reconnue");
        throw new ValidatorException(message);
    }
}