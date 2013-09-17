/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bean.view.multiSelection.struct;

import bean.log.ApplicationLogger;
import bean.Utils;
import java.lang.reflect.Method;
import java.util.List;
import javax.faces.model.ListDataModel;
import org.primefaces.model.SelectableDataModel;

/**
 *
 * @author Brian GOHIER
 */
public abstract class EntityDataModel<C> extends ListDataModel<C>
    implements SelectableDataModel<C>
{
    
    private Method getId;
    
    public EntityDataModel(Class<C> entityClass)
    {
        this(null,entityClass);
    }

    public EntityDataModel(List<C> list, Class<C> entityClass)
    {
        super(list);
        try
        {
            this.getId=entityClass.getMethod("getId");
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
    }
    
    @Override
    public Object getRowKey(C c)
    {
        Integer id=(Integer) Utils.callMethod(getId, "récupération de l'identifiant", c);
        return id;
    }

    @Override
    @SuppressWarnings("unchecked")
    public C getRowData(String rowKey)
    {
        Integer rowId=Integer.parseInt(rowKey);
        for(C c:(List<C>)this.getWrappedData())
        {
            if(rowId==Utils.callMethod(getId, "récupération de l'identifiant", c))
            {
                return c;
            }
        }
        return null;
    }
}
