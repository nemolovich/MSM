/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bean.view.struct;

import bean.Utils;
import bean.facade.abstracts.AbstractFacade;
import bean.log.ApplicationLogger;
import bean.view.filteredSelection.EntitySleepingSelection;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

/**
 *
 * @author Brian GOHIER
 */
public abstract class EntityView<C,F extends AbstractFacade<C>> extends EntitySleepingSelection<C>
{
    private static final long serialVersionUID = 1L;
    private C entity;
    private Class<C> entityClass;
    private String webFolder=null;
    private F entityFacade;
    private boolean creating=false;
    private boolean editing=false;
    
    public EntityView()
    {
    }
    
    public EntityView(Class<C> entityClass,String webFolder)
    {
        super(entityClass);
        this.webFolder="/restricted/admin/data/"+webFolder+"/";
        this.entityClass=entityClass;
    }
    
    public Class<C> getEntityClass()
    {
        return this.entityClass;
    }
    
    @Override
    public List<C> getFullList()
    {
        return this.findAll();
    }

    public boolean isEditing()
    {
        return editing;
    }

    public void setEditing(boolean editing)
    {
        this.editing = editing;
    }
    
    public boolean isCreating()
    {
        return this.creating;
    }

    public void setCreating(boolean creating)
    {
        this.creating = creating;
    }
    
    protected void setWebFolder(String webFolder)
    {
        this.webFolder=webFolder;
    }
    
    protected String getWebFolder()
    {
        return this.webFolder;
    }
    
    public String create()
    {
        return this.createSilent(false);
    }
    
    public String createSilent(boolean silent)
    {
        this.creating=false;
        this.editing=false;
        this.setFacade();
        this.entityFacade.createSilent(this.entity, silent);
        return this.webFolder+"list";
    }
    
    public String delete()
    {
        return this.deleteSilent(false);
    }
    
    public String deleteSilent(boolean silent)
    {
        this.creating=false;
        this.editing=false;
        this.setFacade();
        this.entityFacade.removeSilent(this.entity, silent);
        this.entity=null;
        return this.webFolder+"list";
    }
    
    public String update()
    {
        return this.updateSilent(false);
    }
    
    public String updateSilent(boolean silent)
    {
        this.creating=false;
        this.editing=false;
        this.setFacade();
        this.entityFacade.editSilent(this.entity, silent);
        return this.webFolder+"list";
    }
    
    public String cancelCreate()
    {
        this.creating = false;
        this.editing = false;
        this.entity = null;
        return this.webFolder+"view";
    }
    
    public String cancelUpdate()
    {
        this.creating = false;
        this.editing = false;
        return this.webFolder+"view";
    }
    
    public C checkSingle(C[] entities)
    {
        if(entities!=null&&entities.length!=1)
        {
            FacesMessage message=new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Sélection invalide",
                    "Vous devez sélectionner un et un seul élément "
                    + "pour effectuer cette tâche");
            FacesContext.getCurrentInstance().addMessage(null, message);
            return null;
        }
        return entities[0];
    }

    public String entityView(C entity)
    {
        System.err.println("VIEW");
        this.creating = false;
        this.editing = false;
        this.entity = entity;
        return this.webFolder+"view";
    }
    
    public String entityUpdate(C entity)
    {
        System.err.println("UPDATE");
        this.creating = false;
        this.editing = true;
        this.entity = entity;
        return this.webFolder+"update";
    }
    
    public String entityParameter(C entity)
    {
        this.creating = false;
        this.editing = true;
        this.entity = entity;
        return this.webFolder+"parameters";
    }
    
    public String entityDelete(C entity)
    {
        return this.entitySilentDelete(entity, false);
    }
    
    public String entitySilentDelete(C entity, boolean silent)
    {
        this.creating = false;
        this.editing = false;
        this.entity = entity;
        this.setFacade();
        this.entityFacade.removeSilent(entity, silent);
        this.entity = null;
        return this.webFolder+"list";
    }
    
    public String entitySleep(C entity)
    {
        return this.entitySilentSleep(entity, false);
    }
    
    public String entitySilentSleep(C entity, boolean silent)
    {
        try
        {
            Method m=entity.getClass().getMethod("setSleeping", Boolean.class);
            Utils.callMethod(m, "mise en veille de la donnée", entity, true);
        }
        catch (NoSuchMethodException ex)
        {
            ApplicationLogger.writeError("La méthode \"setSleeping\" n'a pas"+
                    " été trouvée pour la classe \""+this.entityClass.getName()+"\"", ex);
        }
        this.entityFacade.editSilent(entity, silent);
        return this.webFolder+"list";
    }
    
    public String entityWake(C entity)
    {
        return this.entitySilentWake(entity, false);
    }
    
    public String entitySilentWake(C entity, boolean silent)
    {
        try
        {
            Method m=entity.getClass().getMethod("setSleeping", Boolean.class);
            Utils.callMethod(m, "réactivation de la donnée", entity, false);
        }
        catch (NoSuchMethodException ex)
        {
            ApplicationLogger.writeError("La méthode \"setSleeping\" n'a pas"+
                    " été trouvée pour la classe \""+this.entityClass.getName()+"\"", ex);
        }
        this.entityFacade.editSilent(entity, silent);
        return this.webFolder+"list";
    }
    
    public String entityCreate()
    {
        this.creating = true;
        this.editing = false;
        String message="Création d'une entité de la classe \""+this.entityClass.getName()+"\"";
        ApplicationLogger.writeInfo(message);
        try
        {
            this.entity = this.entityClass.newInstance();
        }
        catch (InstantiationException ex)
        {
            ApplicationLogger.writeError("Impossible d'instancier un objet de la"
                    + " classe \""+this.entityClass.getName()+"\"", ex);
        }
        catch (IllegalAccessException ex)
        {
            ApplicationLogger.writeError("Droits refusés pour l'instanciation"
                    + " d'un objet de la classe \""+this.entityClass.getName()+"\"", ex);
        }
        return this.webFolder+"create";
    }
    
    public String setInstance(C entity)
    {
        this.entity = entity;
        return null;
    }
    
    public C getInstance()
    {
        return this.entity;
    }

    public void setEntityFacade(F entityFacade)
    {
        this.entityFacade=entityFacade;
    }

    protected F getEntityFacade()
    {
        return this.entityFacade;
    }
    
    public List<C> findAll()
    {
        this.setFacade();
        List<C> result=this.entityFacade.findAll();
        if(result==null)
        {
            result=new ArrayList<C>();
        }
        return result;
    }
    
    /**
     * Toutes les entités sont censées avoir un champs 'Id', on peut donc
     * utiliser la réflexion pour récupérer ce que renvoi cette methode sur
     * l'entité sur laquelle on travaille
     * @return {@link Integer} - L'identifiant de l'entité
     */
    public Integer getId()
    {
        Integer id = -1;
        try
        {
            id=(Integer) Utils.callMethod(this.entityClass.getMethod("getId"),
                    "récupération de l'identifiant", this.entity);
        }
        catch (NoSuchMethodException ex)
        {
            ApplicationLogger.writeError("La méthode de récupération de"
                    + " l'identifiant pour la class \""+entityClass.getName()+"\""
                    + " n'a pas été trouvée", ex);
        }
        catch (SecurityException ex)
        {
            ApplicationLogger.writeError("La méthode de récupération de"
                    + " l'identifiant pour la class \""+entityClass.getName()+"\""
                    + " n'est pas accessible", ex);
        }
        return id;
    }
    
    // <editor-fold defaultstate="collapsed" desc="Les methodes abstraites">
    /**
     * Insérer le code:
     * <code>
     * super.entityFacade=this.[VotreEJBFacade];
     * </code>
     */
    public abstract void setFacade();
    /**
     * Insérer le code:
     * <code>
     * return super.findAll();
     * </code>
     */
    public abstract List<C> getEntries();
    /**
     * Insérer le code:
     * <code>
     * return super.getInstance();
     * </code>
     */
    public abstract C getEntity();
    /**
     * Insérer le code:
     * <code>
     * super.setInstance(C entity);
     * </code>
     * @param entity 
     */
    public abstract void setEntity(C entity);
    /**
     * Renvoi le message d'avertissement avant la suppression
     * de cette entité
     * @param entity {@link C} - L'entité à supprimer
     * @return {@link String}, Le message d'avertissement
     */
    public abstract String getDeleteMessage(C entity);
    
    // </editor-fold>
}
