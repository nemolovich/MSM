/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bean.view.periodSelection;

import bean.Utils;
import bean.facade.abstracts.AbstractFacade;
import bean.view.struct.EntityView;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

/**
 *
 * @author Brian GOHIER
 */
public abstract class EntityPeriodView<C,F extends AbstractFacade<C>> extends EntityView<C,F>
{
    public static final int YEAR=0;
    public static final int MONTH=1;
    public static final int DAY=2;
    private Date startDate=this.getDay(this.getFirstDay(Calendar.getInstance().getTime()));
    private Date endDate=this.addDate(this.startDate, EmbeddedEntityPeriodView.MONTH);

    public EntityPeriodView()
    {
    }

    public EntityPeriodView(Class<C> entityClass, String webFolder)
    {
        super(entityClass, webFolder);
    }
    
    protected boolean verifDate(Date startDate, Date endDate)
    {
        return Utils.verifDate(startDate,endDate);
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
    public abstract List<C> getEntries();

    @Override
    public abstract C getEntity();

    @Override
    public abstract void setEntity(C entity);

    @Override
    public abstract String getDeleteMessage(C entity);
    // </editor-fold>
}
