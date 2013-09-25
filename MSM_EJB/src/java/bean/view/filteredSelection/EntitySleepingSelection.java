/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bean.view.filteredSelection;

import bean.Utils;
import bean.log.ApplicationLogger;
import bean.view.struct.EntityView;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.List;
import javax.faces.model.SelectItem;

/**
 *
 * @author Brian GOHIER
 */
public abstract class EntitySleepingSelection<C> implements Serializable
{
    private static final long serialVersionUID = 1L;
    private Class<C> entityClass;
    public static final SelectItem[] SLEEPING_STATES={
                    new SelectItem("","Les deux","Affiche les éléments actifs et innactifs"),
                    new SelectItem("false","Actif","Affiche les éléments actifs"),
                    new SelectItem("true","Inactif","Affiche les éléments innactifs")
                };
    private List<C> filteredEntities;
    private List<C> revertFilteredEntities;
    private boolean displaySleepingEntities=false;
    private static boolean DISPLAYED_ERROR=false;

    public EntitySleepingSelection()
    {
    }
    
    public EntitySleepingSelection(Class<C> entityClass)
    {
        this.entityClass = entityClass;
    }

    public abstract List<C> getFullList();
    
    public boolean isDisplaySleepingEntities()
    {
        return this.displaySleepingEntities;
    }

    public void setDisplaySleepingEntities(boolean displaySleepingEntities)
    {
        this.displaySleepingEntities = displaySleepingEntities;
    }
    
    private Boolean isSleepingCall(C entity)
    {
        Method m;
        try
        {
            m=this.entityClass.getMethod("getSleeping");
        }
        catch (NoSuchMethodException ex)
        {
            ApplicationLogger.writeError("La méthode \"getSleeping\" n'a pas"+
                    " été trouvée pour la classe \""+this.entityClass.getName()+"\"", ex);
            return null;
        }
        catch (SecurityException ex)
        {
            ApplicationLogger.writeError("La méthode \"getSleeping\" n'est pas"+
                    " accessible pour la classe \""+this.entityClass.getName()+"\"", ex);
            return null;
        }
        catch (NullPointerException ex)
        {
            ApplicationLogger.writeError("La méthode \"getSleeping\" renvoi \"null\""
                    + " pour la classe '"+this.entityClass.getName()+"'", ex);
            return null;
        }
        return (Boolean)Utils.callMethod(m, "récupération de l'état de veille", entity);
    }
    
    private List<C> getSleepEntities(boolean sleeping)
    {
        List<C> res=new ArrayList<C>();
        for(C c:this.getFullList())
        {
            if(this.isSleepingCall(c)==sleeping)
            {
                res.add(c);
            }
        }
        return res;
    }
  
    public SelectItem[] getSleepingOptions()
    {
        return EntityView.SLEEPING_STATES;
    }
    
    public List<C> getRevertFilteredEntities()
    {
        List<C> temp=this.revertFilteredEntities!=null?
                new ArrayList<C>(this.revertFilteredEntities):
                new ArrayList<C>();
        if(this.revertFilteredEntities!=null)
        {
            try
            {
                for(C c:temp)
                {
                    Boolean sleeping=this.isSleepingCall(c);
                    if(c!=null&&(sleeping==null||!sleeping))
                    {
                        this.revertFilteredEntities.remove(c);
                    }
                }
            }
            catch (ConcurrentModificationException ex)
            {
                ApplicationLogger.writeError("Accès concurrent à la méthode"
                        + " \"getRevertFilteredEntities\" pour la liste des données de"
                        + " la classe \""+this.entityClass.getName()+"\"", ex);
                return new ArrayList<C>();
            }
        }
        if(this.revertFilteredEntities==null&&!DISPLAYED_ERROR)
        {
            return this.getFullList()!=null?this.getFullList():new ArrayList<C>();
        }
        return this.revertFilteredEntities;
    }
    
    public List<C> getFilteredEntities()
    {
        List<C> temp=this.filteredEntities!=null?
                new ArrayList<C>(this.filteredEntities):
                new ArrayList<C>();
        
        if(!this.displaySleepingEntities&&this.filteredEntities!=null)
        {
            try
            {
                for(C c:temp)
                {
                    Boolean sleeping=this.isSleepingCall(c);
                    if(c!=null&&(sleeping!=null&&sleeping))
                    {
                        this.filteredEntities.remove(c);
                    }
                }
                temp.clear();
            }
            catch (ConcurrentModificationException ex)
            {
                ApplicationLogger.writeError("Accès concurrent à la méthode"
                        + " \"getFilteredEntities\" pour la liste des données de"
                        + " la classe \""+this.entityClass.getName()+"\"", ex);
            }
        }
        if(this.filteredEntities==null&&!DISPLAYED_ERROR)
        {
            Exception ex=new NullPointerException("La liste des entitiés de la " +
                    "classe \""+this.entityClass.getName()+"\" filtrée pour le " +
                    "filtre \"Sleeping\" renvoi \"null\"");
//            ApplicationLogger.writeError(ex.getLocalizedMessage(), ex);
            ApplicationLogger.displayError(ex.getLocalizedMessage(), ex);
            DISPLAYED_ERROR=true;
            return this.getFullList()!=null?this.getFullList():new ArrayList<C>();
        }
        return this.filteredEntities;
    }

    public void setFilteredEntities(List<C> filteredEntities)
    {
        this.filteredEntities = filteredEntities;
    }

    public void setRevertFilteredEntities(List<C> revertFilteredEntities)
    {
        this.revertFilteredEntities = revertFilteredEntities;
    }
}
