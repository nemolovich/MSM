/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bean.view.periodSelection;

import bean.facade.abstracts.AbstractEmbeddedDataList;
import bean.view.struct.EmbeddedDataListView;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

/**
 *
 * @author Brian GOHIER
 */
public abstract class EmbeddedEntityPeriodView<C,O,F extends AbstractEmbeddedDataList<C,O>> extends EmbeddedDataListView<C, O, F>
{
    public static final int YEAR=0;
    public static final int MONTH=1;
    public static final int DAY=2;
    private Date startDate=this.getDay(this.getFirstDay(Calendar.getInstance().getTime()));
    private Date endDate=this.addDate(this.startDate, EmbeddedEntityPeriodView.MONTH);

    public EmbeddedEntityPeriodView()
    {
    }
    
    public EmbeddedEntityPeriodView(Class<O> entityClass, String webFolder, Method setReferenceMethod)
    {
        super(entityClass, webFolder, setReferenceMethod);
    }
    
    protected boolean verifDate(Date startDate, Date endDate)
    {
        return startDate.before(endDate)||startDate.equals(endDate);
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate)
    {
        if(!this.verifDate(startDate, this.endDate))
        {
            FacesMessage msg=new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Date de début incorrecte", "La date de début doit être antérieure"
                    + " à celle de fin");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
        else
        {
            this.startDate = startDate;
        }
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate)
    {
        if(!this.verifDate(this.startDate, endDate))
        {
            FacesMessage msg=new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Date de fin incorrecte", "La date de fin doit être postérieure"
                    + " à celle de fin");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
        else
        {
            this.endDate = endDate;
        }
    }
    
    private Date getDay(Date date)
    {
        Calendar cal=Calendar.getInstance();
        cal.setTime(date);
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        return cal.getTime();
    }
    
    private Date getFirstDay(Date date)
    {
        Calendar cal=Calendar.getInstance();
        cal.setTime(date);
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), 1);
        return cal.getTime();
    }
    
    private Date addDate(Date startDate, int field, int amount)
    {
        Calendar cal=Calendar.getInstance();
        cal.setTime(startDate);
        cal.add(field, amount);
        return cal.getTime();
    }
    
    private Date addDate(Date startDate, int elmt)
    {
        Calendar cal=Calendar.getInstance();
        cal.setTime(startDate);
        if(elmt==EmbeddedEntityPeriodView.DAY)
        {
            return this.addDate(startDate, Calendar.MONTH, 1);
        }
        else if(elmt==EmbeddedEntityPeriodView.MONTH)
        {
            int days=cal.getActualMaximum(Calendar.DAY_OF_MONTH);
            return this.addDate(startDate, Calendar.DAY_OF_YEAR, days-1);
        }
        else if(elmt==EmbeddedEntityPeriodView.YEAR)
        {
            return this.addDate(startDate, Calendar.YEAR, 1);
        }
        return startDate;
    }
    
    // <editor-fold defaultstate="collapsed" desc="Les methodes abstraites de la super-classe">
    @Override
    public abstract void setFacade();

    @Override
    public abstract String deleteMessages(List<O> entities);

    @Override
    public abstract List<O> getEntries();

    @Override
    public abstract O getEntity();

    @Override
    public abstract void setEntity(O entity);

    @Override
    public abstract String getDeleteMessage(O entity);
    // </editor-fold>
}
