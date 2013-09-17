/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package converter.struct;

import bean.facade.abstracts.AbstractFacade;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

/**
 *
 * @author Brian GOHIER
 */
@ManagedBean
@RequestScoped
public class EntityConverter<C,F extends AbstractFacade<C>> implements Converter
{
    protected F entityFacade;

    public EntityConverter() {
    }

    @Override
    public Object getAsObject(FacesContext context, UIComponent component,
        String value)
    {
        for(C entity:this.entityFacade.findAll())
        {
            if(value!=null&&entity.toString().equals(value))
            {
                return entity;
            }
        }
        return null;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component,
        Object value)
    {
        for(C entity:this.entityFacade.findAll())
        {
            if(value!=null&&entity.equals(value))
            {
                return entity.toString();
            }
        }
        return value==null?"":null;
    }
    
}
