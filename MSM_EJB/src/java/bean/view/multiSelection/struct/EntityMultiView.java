/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bean.view.multiSelection.struct;

import bean.view.filteredSelection.EntitySleepingSelection;
import java.util.ArrayList;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

/**
 *
 * @author Brian GOHIER
 */
public class EntityMultiView<C> extends EntitySleepingSelection<C>
{
    private static final long serialVersionUID = 1L;
    private C[] multiSelection;
    private C singleSelection;
    private Class<C> entityClass;
    private EntityDataModel<C> multiDataModel;
    private List<C> fullList;

    public EntityMultiView()
    {
    }    

    public EntityMultiView(Class<C> entityClass)
    {
        super(entityClass);
        this.entityClass = entityClass;
    }

    @Override
    public List<C> getFullList()
    {
        return this.fullList!=null?this.fullList:new ArrayList<C>();
    }
    
    public EntityDataModel<C> getData(List<C> list)
    {
        this.setMultiDataModel(list);
        return this.getMultiDataModel();
    }

    public EntityDataModel<C> getMultiDataModel()
    {
        return this.multiDataModel;
    }
    
    public boolean isSingleSelected()
    {
        return this.multiSelection!=null&&this.multiSelection.length==1;
    }
    
    public boolean isEmptySelection()
    {
        if(this.multiSelection==null||this.multiSelection.length==0)
        {
            return true;
        }
        return false;
    }
    
    public boolean checkEmptySelection()
    {
        if(this.isEmptySelection())
        {
            FacesMessage message=new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Sélection invalide",
                    "Vous devez sélectionner au moins un élément "
                    + "pour effectuer cette tâche");
            FacesContext.getCurrentInstance().addMessage(null, message);
            return true;
        }
        return false;
    }

    public void setMultiDataModel(List<C> list)
    {
        this.fullList=list;
        this.multiDataModel = new EntityDataModel<C>(list,this.entityClass){};
    }

    public C[] getMultiSelection()
    {
        return multiSelection;
    }

    @SuppressWarnings("unchecked")
    public List<C> getMultiSelectionCast()
    {
        List<C> list=new ArrayList<C>();
        if(this.multiSelection!=null)
        {
            for(Object o:this.multiSelection)
            {
                list.add((C)o);
            }
        }
        return list;
    }

    public void setMultiSelection(C[] multiSelection)
    {
        if(multiSelection.length>0)
        {
            this.singleSelection = multiSelection[0];
        }
        this.multiSelection = multiSelection;
    }

    public C getSingleSelection()
    {
        return singleSelection;
    }

    public void setSingleSelection(C singleSelection)
    {
        this.singleSelection = singleSelection;
    }
}
