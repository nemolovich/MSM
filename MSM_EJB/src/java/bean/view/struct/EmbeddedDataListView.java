/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bean.view.struct;

import bean.Utils;
import bean.facade.abstracts.AbstractEmbeddedDataList;
import bean.log.ApplicationLogger;
import java.lang.reflect.Method;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

/**
 *
 * @author Brian GOHIER
 */
public abstract class EmbeddedDataListView<C,O,F extends AbstractEmbeddedDataList<C,O>> extends EntityView<O,F>
{
    private Method setReferenceMethod;

    public EmbeddedDataListView()
    {
    }    

    public EmbeddedDataListView(Class<O> entityClass,String webFolder,
            Method setReferenceMethod)
    {
        super(entityClass, webFolder);
        this.setReferenceMethod = setReferenceMethod;
    }
    
    public String entityCreate(C entity)
    {
        super.entityCreate();
        Utils.callMethod(this.setReferenceMethod,
                "méthode de définition de la référence",this.getInstance(),
                entity);
        this.setEditing(false);
        this.setCreating(true);
        return "create";
    }
    
    public String entityDelete(C entity, O instance)
    {
        return this.entitySilentDelete(entity, instance, false);
    }
    
    @SuppressWarnings("unchecked")
    public String entitySilentDelete(C entity, O instance, boolean silent)
    {
        this.setFacade();
        super.setInstance(instance);
        super.getEntityFacade().removeSilentToDataList(entity,false,instance);
        this.setEditing(false);
        this.setCreating(false);
        return "list";
    }
    
    public String create(C entity)
    {
        return this.createSilent(entity, false);
    }
    
    public String createSilent(C entity, boolean silent)
    {
        this.setFacade();
        super.getEntityFacade().addSilentToDataList(entity,this.getInstance(), silent);
        this.setEditing(false);
        this.setCreating(false);
        return "list";
    }
    
    public String update(C entity)
    {
        return this.updateSilent(entity, false);
    }
    
    public String updateSilent(C entity, boolean silent)
    {
        this.setFacade();
        super.getEntityFacade().updateSilentToDataList(entity, this.getInstance(), silent);
        this.setEditing(false);
        this.setCreating(false);
        return "list";
    }
    
    public String entitySleep(C entity, O instance)
    {
        return this.entitySilentSleep(entity, instance, false);
    }
    
    public String entitySilentSleep(C entity, O instance, boolean silent)
    {
        this.setFacade();
        try
        {
            Method m=instance.getClass().getMethod("setSleeping", Boolean.class);
            Utils.callMethod(m, "mise en veille de la donnée", instance, true);
        }
        catch (NoSuchMethodException ex)
        {
            ApplicationLogger.writeError("La méthode \"setSleeping\" n'a pas"+
                    " été trouvée pour la classe \""+instance.getClass().getName()+"\"", ex);
        }
        super.getEntityFacade().updateSilentToDataList(entity, instance, silent);
        return "list";
    }
    
    public String entityWake(C entity, O instance)
    {
        return this.entitySilentWake(entity, instance, false);
    }
    
    public String entitySilentWake(C entity, O instance, boolean silent)
    {
        this.setFacade();
        try
        {
            Method m=instance.getClass().getMethod("setSleeping", Boolean.class);
            Utils.callMethod(m, "réactivation de la donnée", instance, false);
        }
        catch (NoSuchMethodException ex)
        {
            ApplicationLogger.writeError("La méthode \"setSleeping\" n'a pas"+
                    " été trouvée pour la classe \""+instance.getClass().getName()+"\"", ex);
        }
        super.getEntityFacade().updateSilentToDataList(entity, instance, silent);
        return "list";
    }
    
    public String remove(C entity, O... instances)
    {
        return this.removeSilent(entity, false, instances);
    }
    
    public String removeSilent(C entity, boolean silent, O... instances)
    {
        if(instances==null)
        {
            FacesMessage message=new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Sélection invalide",
                    "Vous devez sélectionner au moins un élément "
                    + "pour effectuer cette tâche");
            FacesContext.getCurrentInstance().addMessage(null, message);
            return null;
        }
        this.setFacade();
        super.getEntityFacade().removeSilentToDataList(entity, silent, instances);
        for(O instance:instances)
        {
            super.setInstance(instance);
        }
        this.setEditing(false);
        this.setCreating(false);
        return "list";
    }
    
    // <editor-fold defaultstate="collapsed" desc="Les methodes abstraites">
    /**
     * Insérer le code:
     * <code>
     * super.entityFacade=this.[VotreEJBFacade];
     * </code>
     */
    @Override
    public abstract void setFacade();
    /**
     * Renvoi le message d'avertissement avant la suppression
     * de toutes ces entités sélectionnées
     * @return {@link String}, Le message d'avertissement
     */
    public abstract String deleteMessages(List<O> entities);
    
    // </editor-fold>
}
