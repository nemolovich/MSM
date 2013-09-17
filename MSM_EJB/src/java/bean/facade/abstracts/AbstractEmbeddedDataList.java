/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bean.facade.abstracts;

import bean.Utils;
import bean.log.ApplicationLogger;
import java.lang.reflect.Method;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Brian GOHIER
 */
public abstract class AbstractEmbeddedDataList<C,O> extends AbstractFacade<O>
{
    @PersistenceContext(unitName = "MSM_EJBPU")
    private EntityManager em;
    private Method getDataListMethod;
    private Method setDataListMethod;
    
    public AbstractEmbeddedDataList(Class<O> objectClass,
            Method getDataListMethod, Method setDataListMethod)
    {
        super(objectClass);
        this.getDataListMethod = getDataListMethod;
        this.setDataListMethod = setDataListMethod;
    }
    
    public void addToDataList(C entity, O instance)
    {
        this.addSilentToDataList(entity, instance, false);
    }
    
    @SuppressWarnings("unchecked")
    public void addSilentToDataList(C entity, O instance, boolean silent)
    {
        List<O> list=(List<O>) Utils.callMethod(this.getDataListMethod,
                "méthode de récupération des données",entity);
        list.add(instance);
        Utils.callMethod(this.setDataListMethod,
                "méthode de paramétrage des données",entity,list);
        this.em.persist(instance);
        this.em.merge(entity);
        
        String entity_details=Utils.getFullString(entity);
        entity_details=entity_details!=null?entity_details:entity.toString();
        String instance_details=Utils.getFullString(instance);
        instance_details=instance_details!=null?instance_details:instance.toString();
        ApplicationLogger.writeWarning("Ajout de l'entité de la classe \""+
                instance.getClass().getName()+"\" réussie");
        ApplicationLogger.write("\tObjet: \""+instance.getClass().getName()+"\": \""+
                instance_details+"\"");
        ApplicationLogger.write("\t[INSIDE] Dans la liste de l'objet:\n"+
                "\tObjet: \""+entity.getClass().getName()+"\": \""+
                entity_details+"\"");
        ApplicationLogger.addSmallSep();
        if(!silent)
        {
            FacesMessage message=new FacesMessage(FacesMessage.SEVERITY_INFO,
                    "Ajout de la donnée réussi",
                    "La donnée a bien été ajoutée dans la liste");
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
    }
    
    public void updateToDataList(C entity, O instance)
    {
        this.updateSilentToDataList(entity, instance, false);
    }
    
    public void updateSilentToDataList(C entity, O instance, boolean silent)
    {
//        List<O> list=(List<O>) Utils.callMethod(this.getDataListMethod,
//                "méthode de récupération des données",entity);
//        list.add(instance);
//        Utils.callMethod(this.setDataListMethod,
//                "méthode de paramétrage des données",entity,list);
        this.em.merge(instance);
        this.em.merge(entity);
        String entity_details=Utils.getFullString(entity);
        entity_details=entity_details!=null?entity_details:entity.toString();
        String instance_details=Utils.getFullString(instance);
        instance_details=instance_details!=null?instance_details:instance.toString();
        ApplicationLogger.writeWarning("Modification de l'entité de la classe \""+
                instance.getClass().getName()+"\" réussie");
        ApplicationLogger.write("\tObjet: \""+instance.getClass().getName()+"\": \""+
                instance_details+"\"");
        ApplicationLogger.write("\t[INSIDE] Dans la liste de l'objet:\n"+
                "\tObjet: \""+entity.getClass().getName()+"\": \""+
                entity_details+"\"");
        ApplicationLogger.addSmallSep();
        if(!silent)
        {
            FacesMessage message=new FacesMessage(FacesMessage.SEVERITY_INFO,
                    "Modification de la donnée réussi",
                    "La donnée a bien été modifiée dans la liste");
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
    }
    
    public void removeToDataList(C entity, O... instances)
    {
        this.removeSilentToDataList(entity, false, instances);
    }
    
    @SuppressWarnings("unchecked")
    public void removeSilentToDataList(C entity, boolean silent, O... instances)
    {
        if(instances==null || instances.length==0)
        {
            return;
        }
        List<O> list=(List<O>) Utils.callMethod(this.getDataListMethod,
                "méthode de récupération des données",entity);
        ApplicationLogger.writeWarning("Suppression des entités de la classe \""+
                instances[0].getClass().getName()+"\"...");
        for(O instance:instances)
        {
            list.remove(instance);
            String instance_details=Utils.getFullString(instance);
            instance_details=instance_details!=null?instance_details:instance.toString();
            ApplicationLogger.write("\t[DELETE...]");
            ApplicationLogger.write("\tObjet: \""+instance.getClass().getName()+"\": \""+
                    instance_details+"\"");
            ApplicationLogger.write("\t[DELETED] Réussie");
        }
        String entity_details=Utils.getFullString(entity);
        entity_details=entity_details!=null?entity_details:entity.toString();
        ApplicationLogger.write("\t[INSIDE] Dans la liste de l'objet:\n"+
                "\tObjet: \""+entity.getClass().getName()+"\": \""+
                entity_details+"\"");
        ApplicationLogger.addSmallSep();
        Utils.callMethod(this.setDataListMethod,
                "méthode de paramétrage des données",entity,list);
        boolean unique = instances.length==1;
        for(O instance:instances)
        {
            this.em.remove(this.em.merge(instance));
        }
        String s=unique?"":"s";
        if(!silent)
        {
            FacesMessage message=new FacesMessage(FacesMessage.SEVERITY_INFO,
                    "Suppression "+(unique?"de la":"des")+" donnée"+s+" réussie",
                    (unique?"La":"Les")+" donnée"+s+" "+(unique?"a":"ont")+
                    " bien été supprimée"+s+" de la liste");
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
        this.em.merge(entity);
    }
}