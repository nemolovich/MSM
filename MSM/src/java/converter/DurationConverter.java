/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package converter;

import bean.Utils;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

/**
 *
 * @author Brian GOHIER
 */
@ManagedBean
@RequestScoped
@FacesConverter("durationConverter")
public class DurationConverter implements Converter
{

    @Override
    public Object getAsObject(FacesContext context, UIComponent component,
    String value)
    {
        return Utils.getDurationObject(value);
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component,
    Object value)
    {
        return Utils.getDurationString(value);
    }
    
}
