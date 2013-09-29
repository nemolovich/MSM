/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package validator.required;

import bean.log.ApplicationLogger;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

/**
 *
 * @author Brian GOHIER
 */
@FacesValidator("notNullFieldValidator")
@ManagedBean
@RequestScoped
public class NotNullFieldValidator implements Validator
{

    @Override
    public void validate(FacesContext context, UIComponent component,
        Object value) throws ValidatorException
    {
        String requiredTitle=(String) component.getAttributes().get("requiredTitle");
        String requiredMessage=(String) component.getAttributes().get("requiredMessage");
        
        if(requiredTitle==null || requiredTitle.isEmpty()
            || requiredMessage==null || requiredMessage.isEmpty() )
        {
            ApplicationLogger.writeWarning("Dans le validateur \""+this.getClass().getName()+
                    "\". Vous devez spécifier un attribut \"requiredTitle\" pour "
                    + "ce validateur ainsi qu'un attribut \"requiredMessage\". "
                    + "Exemple: <f:attribute name=\"requiredTitle\" value=\"Champs invalide\" />"
                    + "pour titre \"Champs invalide\" et "
                    + "<f:attribute name=\"requiredMessage\" value=\"Le champs est vide\" /> "
                    + "message \"Le champs est vide\".");
            return;
        }
        
        if(value==null || (value.getClass()==String.class && ((String)value).isEmpty()))
        {
        ((UIInput)component).setValid(false);
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    requiredTitle,
                    requiredMessage);
            throw new ValidatorException(message);
        }
    }
    
}
