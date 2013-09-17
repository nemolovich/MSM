package converter.struct;

import bean.log.ApplicationLogger;
import java.io.File;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

/**
 *
 * @author Brian GOHIER
 */
public abstract class FileConverter implements Converter
{
    private List<File> list;

    public FileConverter()
    {
    }

    public FileConverter(List<File> list)
    {
        this.list = list;
    }

    public List<File> getList()
    {
        return list;
    }

    public void setList(List<File> list)
    {
        this.list = list;
    }
    
    @Override
    public Object getAsObject(FacesContext context, UIComponent component,
        String value)
    {
        for(File file: this.list)
        {
            if(value!=null&&file.getName().equals(value))
            {
                return file;
            }
        }
        return null;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component,
        Object value)
    {
        for(File file: this.list)
        {
            if(value!=null&&file.equals(value))
            {
                return file.getName();
            }
        }
        return value==null?"":null;
    }
    
}