/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package validator.unique.struct;

import bean.facade.abstracts.AbstractFacade;
import bean.log.ApplicationLogger;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import validator.EmailPatternValidator;

/**
 *
 * @author Brian GOHIER
 */
@ManagedBean
@RequestScoped
public abstract class UniqueDependantFieldValidator<O, C, F extends AbstractFacade<C>> implements Validator
{
    private Class<C> entityClass;
    protected F entityFacade;
    private Class<O> parentClass;

    public UniqueDependantFieldValidator()
    {
    }
    
    public UniqueDependantFieldValidator(Class<O> parentClass, Class<C> entityClass)
    {
        this.entityClass = entityClass;
        this.parentClass = parentClass;
    }
    
    @Override
    public void validate(FacesContext context, UIComponent component, Object value)
            throws ValidatorException
    {
        String field = (String) value;
        
        String fieldName=(String) component.getAttributes().get("fieldName");
        Integer update_ID=(Integer) component.getAttributes().get("update_id");
        
        if(fieldName==null || fieldName.isEmpty()
            || update_ID==null )
        {
            ApplicationLogger.writeWarning("Dans le validateur \""+this.getClass().getName()+
                    "\". Vous devez spécifier un attribut \"fieldName\" pour ce validateur ainsi qu'un attribut \"update_id\". "
                    + "Exemple: <f:attribute name=\"fieldName\" value=\"Mail\" /> pour un champs \"Mail\" et "
                    + "<f:attribute name=\"update_id\" value=\"1\" /> pour la mise à jour de l'entité ayant "
                    + "pour identifiant 1 (-1 pour la création).");
            return;
        }

        if (field == null || field.isEmpty())
        {
            return;
        }
        
        List<C> all;
        all = this.entityFacade.findAll();
        Method m, getId;
        try
        {
            m=this.entityClass.getMethod("get"+fieldName);
            getId=this.entityClass.getMethod("getId");
        }
        catch (NoSuchMethodException ex)
        {
            ApplicationLogger.writeError("La méthode \"get"+fieldName+"\" n'a pas "
                    + "été trouvé pour la classe \""+this.entityClass+"\"", ex);
            return;
        }
        catch (SecurityException ex)
        {
            ApplicationLogger.writeError("L'accès à la méthode \"get"+fieldName+
                    "\" est restreint pour la classe \""+this.entityClass+"\"", ex);
            return;
        }
        Method getParent,getList;
        try
        {
            getParent=this.entityClass.getMethod("getId"+this.parentClass.getSimpleName());
        }
        catch (NoSuchMethodException ex)
        {
            ApplicationLogger.writeError("La méthode \"getId"+
                    this.parentClass.getSimpleName()+"\" n'a pas "
                    + "été trouvé pour la classe \""+this.entityClass+"\"", ex);
            return;
        }
        catch (SecurityException ex)
        {
            ApplicationLogger.writeError("L'accès à la méthode \"getId"+
                    this.parentClass.getSimpleName()+
                    "\" est restreint pour la classe \""+this.entityClass+"\"", ex);
            return;
        }
        try
        {
            getList=this.parentClass.getMethod("get"+this.entityClass.getSimpleName()+"List");
        }
        catch (NoSuchMethodException ex)
        {
            ApplicationLogger.writeError("La méthode \"get"+
                    this.entityClass.getSimpleName()+"List\" n'a pas "
                    + "été trouvé pour la classe \""+this.entityClass+"\"", ex);
            return;
        }
        catch (SecurityException ex)
        {
            ApplicationLogger.writeError("L'accès à la méthode \"get"+
                    this.entityClass.getSimpleName()+"List"+
                    "\" est restreint pour la classe \""+this.entityClass+"\"", ex);
            return;
        }
        C update_entity=null;
        for(C elm:all)
        {
            try
            {
                if(elm!=null&&((Integer)getId.invoke(elm))==(update_ID))
                {
                    update_entity=elm;
                    break;
                }
            }
            catch (IllegalAccessException ex)
            {
                ApplicationLogger.writeError("Accès interdit à la méthode de "
                        + "récupération de l'identifiant pour la classe \""+
                        this.entityClass+"\"", ex);
                return;
            }
            catch (IllegalArgumentException ex)
            {
                ApplicationLogger.writeError("Arguments invalides pour la méthode de "
                        + "récupération de l'identifiant pour la classe \""+
                        this.entityClass+"\"", ex);
                return;
            }
            catch (InvocationTargetException ex)
            {
                ApplicationLogger.writeError("Impossible d'appeler la méthode de "
                        + "récupération de l'identifiant pour la classe \""+
                        this.entityClass+"\"", ex);
                return;
            }
        }
        if(update_entity==null)
        {
            ApplicationLogger.writeError("Impossible de localiser l'entité de "+
                    "la classe \""+this.entityClass+"\""+
                    "ayant pour identifiant "+update_ID, null);
            return;
        }
        List<C> entities;
        try
        {
            O parent=(O) getParent.invoke(update_entity);
            entities=(List<C>) getList.invoke(parent);
        }
        catch (IllegalAccessException ex)
        {
            ApplicationLogger.writeError("Accès interdit aux méthodes "
                    + "d'association des classes \""+this.entityClass+"\""
                    + " et \""+this.parentClass+"\"", ex);
            return;
        }
        catch (IllegalArgumentException ex)
        {
            ApplicationLogger.writeError("Arguments invalides pour les méthodes "
                    + "d'association des classes \""+this.entityClass+"\""
                    + " et \""+this.parentClass+"\"", ex);
            return;
        }
        catch (InvocationTargetException ex)
        {
            ApplicationLogger.writeError("Impossible d'appeler les méthodes "
                    + "d'association des classes \""+this.entityClass+"\""
                    + " et \""+this.parentClass+"\"", ex);
            return;
        }
        for(C entity:entities)
        {
            try
            {
                boolean equals=false;
                if(m.getReturnType()==String.class)
                {
                    equals=((String)m.invoke(entity)).equalsIgnoreCase(field);
                }
                if((equals||m.invoke(entity).equals(field))&&((Integer)getId.invoke(entity))!=update_ID)
                {
                    FacesMessage message=new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Champs '"+fieldName+"' incorrect",
                            "La valeur '"+field+"' pour le champs '"+fieldName+
                            "' est déjà utilisé par une autre entrée");
                    throw new ValidatorException(message);
                }
            }
            catch (IllegalAccessException ex)
            {
                ApplicationLogger.writeError("Accès interdit à la méthode de "
                        + "récupération de l'identifiant pour la classe \""+
                        this.entityClass+"\"", ex);
                return;
            }
            catch (IllegalArgumentException ex)
            {
                ApplicationLogger.writeError("Arguments invalides pour la méthode de "
                        + "récupération de l'identifiant pour la classe \""+
                        this.entityClass+"\"", ex);
                return;
            }
            catch (InvocationTargetException ex)
            {
                ApplicationLogger.writeError("Impossible d'appeler la méthode de "
                        + "récupération de l'identifiant pour la classe \""+
                        this.entityClass+"\"", ex);
                return;
            }
        }
        if(fieldName.equalsIgnoreCase("Mail"))
        {
            EmailPatternValidator mailValidator = new EmailPatternValidator();
            mailValidator.validate(context, component, value);
        }
    }
}