/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bean.log;

import java.util.Date;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.ScheduleExpression;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Timeout;
import javax.ejb.TimerService;
import javax.inject.Named;

/**
 *
 * @author Brian GOHIER
 */
@Named(value="logSchedule")
@Singleton
@Startup
public class LogSchedule
{
    @Resource
    private TimerService service;
    private long start;
    
    public LogSchedule()
    {
        System.out.println("Le timer pour le journal est lancé");
    }
    
    @PostConstruct
    public void init()
    {
        this.start=(new Date()).getTime();
        ScheduleExpression exp=new ScheduleExpression();
        exp.dayOfMonth("*").hour("*").minute("*/5").second("0");
        service.createCalendarTimer(exp);
    }
    
    @Timeout
    public void timeOut()
    {
        long act=(new Date()).getTime();
        System.out.println("Vérification de l'état du journal: "+((act-this.start)/1000)+" secondes");
        this.start=act;
        ApplicationLogger.archive(true);
    }
    
}
