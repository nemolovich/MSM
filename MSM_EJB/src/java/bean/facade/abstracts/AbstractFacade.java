/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bean.facade.abstracts;

import bean.Utils;
import bean.log.ApplicationLogger;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;

/**
 *
 * @author Brian GOHIER
 */
public abstract class AbstractFacade<T>
{
    private Class<T> entityClass;

    public AbstractFacade(Class<T> entityClass)
    {
        this.entityClass = entityClass;
    }

    protected abstract EntityManager getEntityManager();

    public void create(T entity)
    {
        this.createSilent(entity, false);
    }

    public void createSilent(T entity, boolean silent)
    {
        getEntityManager().persist(entity);
        String details=Utils.getFullString(entity);
        details=details!=null?details:entity.toString();
        ApplicationLogger.addSmallSep();
        ApplicationLogger.writeWarning("Création de l'entité de la classe \""+
                this.entityClass.getName()+"\" réussie");
        ApplicationLogger.write("\tObjet: \""+this.entityClass.getName()+"\": \""+
                details+"\"");
        ApplicationLogger.addSmallSep();
        if(!silent)
        {
            FacesMessage message=new FacesMessage(FacesMessage.SEVERITY_INFO,
                    "Ajout de la donnée réussie",
                    "L'enregistrement s'est correctement déroulé");
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
    }

    public void edit(T entity)
    {
        this.editSilent(entity, false);
    }

    public void editSilent(T entity, boolean silent)
    {
        getEntityManager().merge(entity);
        String details=Utils.getFullString(entity);
        details=details!=null?details:entity.toString();
        ApplicationLogger.addSmallSep();
        ApplicationLogger.writeWarning("Modification de l'entité de la classe \""+
                this.entityClass.getName()+"\" réussie");
        ApplicationLogger.write("\tObjet: \""+this.entityClass.getName()+"\": \""+
                details+"\"");
        ApplicationLogger.addSmallSep();
        if(!silent)
        {
            FacesMessage message=new FacesMessage(FacesMessage.SEVERITY_INFO,
                    "Modification de la donnée réussie",
                    "La mise-à-jour s'est correctement déroulée");
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
    }

    public void remove(T entity)
    {
        this.removeSilent(entity, false);
    }

    public void removeSilent(T entity, boolean silent)
    {
        T temp=entity;
        String details=Utils.getFullString(temp);
        details=details!=null?details:temp.toString();
        getEntityManager().remove(getEntityManager().merge(entity));
        ApplicationLogger.addSmallSep();
        ApplicationLogger.writeWarning("Suppression de l'entité de la classe \""+
                this.entityClass.getName()+"\" réussie");
        ApplicationLogger.write("\tObjet: \""+this.entityClass.getName()+"\": \""+
                details+"\"\r");
        ApplicationLogger.addSmallSep();
        if(!silent)
        {
            FacesMessage message=new FacesMessage(FacesMessage.SEVERITY_INFO,
                    "Supression de la donnée réussie",
                    "La suppression s'est correctement déroulée");
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
    }

    public T find(Object id)
    {
        return getEntityManager().find(entityClass, id);
    }

    @SuppressWarnings({"unchecked","rawtypes"})
    public List<T> findAll()
    {
        javax.persistence.criteria.CriteriaQuery cq;
        cq = getEntityManager().getCriteriaBuilder().createQuery();
        cq.select(cq.from(entityClass));
        return getEntityManager().createQuery(cq).getResultList();
    }

    @SuppressWarnings({"unchecked","rawtypes"})
    public List<T> findRange(int[] range)
    {
        javax.persistence.criteria.CriteriaQuery cq;
        cq = getEntityManager().getCriteriaBuilder().createQuery();
        cq.select(cq.from(entityClass));
        javax.persistence.Query q = getEntityManager().createQuery(cq);
        q.setMaxResults(range[1] - range[0]);
        q.setFirstResult(range[0]);
        return q.getResultList();
    }
    
    @SuppressWarnings({"unchecked","rawtypes"})
    public int count()
    {
        javax.persistence.criteria.CriteriaQuery cq;
        cq = getEntityManager().getCriteriaBuilder().createQuery();
        javax.persistence.criteria.Root<T> rt = cq.from(entityClass);
        cq.select(getEntityManager().getCriteriaBuilder().count(rt));
        javax.persistence.Query q = getEntityManager().createQuery(cq);
        return ((Long) q.getSingleResult()).intValue();
    }
    
}
