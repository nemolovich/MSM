/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bean;

import bean.licence.LicenseReader;
import bean.log.ApplicationLogger;
import entity.Users;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.context.ApplicationScoped;
import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.jms.Message;
import javax.servlet.ServletContext;

/**
 *
 * @author Brian GOHIER
 */
@Named(value = "utils")
@Singleton
@Startup
@ApplicationScoped
public class Utils
{
    /**
     * Nom de l'application
     */
    public static final String APPLICATION_NAME="MSM";
    /**
     * Nom complet de l'application
     */
    public static final String APPLICATION_FULLNAME="Manage and Share your Media";
    /**
     * Version de l'application
     */
    public static final double APPLICATION_VERSION=1.0;
    /**
     * Auteurs de l'application
     */
    public static final String APPLICATION_AUTHORS="Maël BARBIN & Brian GOHIER";
    /**
     * Date de déploiement de l'application
     */
    public static final Date APPLICATION_DATE=Calendar.getInstance().getTime();
    /**
     * Répertoire des ressources sur le serveur
     */
    private static final String RESOURCES_PATH="resources"+File.separator;
    /**
     * Répertoire des fichiers uploadés sur le serveur
     */
    private static final String UPLOADS_PATH="uploads"+File.separator+"admin"+File.separator;
    /**
     * Le nombre maximum de ligne dans une liste de données
     */
    private final int maxDataRows = 10;
    /**
     * Droits de l'application
     */
    public static final String UNKNOWN_RIGHTS = "UNKNOWN";
    public static final String USER_RIGHTS = "USER";
    public static final String ADMIN_RIGHTS = "ADMIN";
    private static final List<String> RIGHTS = Arrays.asList(
            Utils.UNKNOWN_RIGHTS,
            Utils.USER_RIGHTS,
            Utils.ADMIN_RIGHTS);
    /**
     * Formats de date en francais
     */
    private static final DateFormat formatFull = new SimpleDateFormat(
            "EEEEE dd MMMMM yyyy à HH:mm:ss",
            Locale.FRANCE);
    private static final SimpleDateFormat formatMedium = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    private static final SimpleDateFormat formatSmall = new SimpleDateFormat("dd/MM/yyyy");
    private static final SimpleDateFormat formatDay = new SimpleDateFormat("EEEEE dd MMMMM yyyy");

    public Utils()
    {
    }

    public static String APPLICATION_NAME()
    {
        return APPLICATION_NAME;
    }

    public static double APPLICATION_VERSION()
    {
        return APPLICATION_VERSION;
    }

    public static String APPLICATION_AUTHORS() {
        return APPLICATION_AUTHORS;
    }
    
    public static String APPLICATION_FULLNAME() {
        return APPLICATION_FULLNAME;
    }

    public static Date APPLICATION_DATE() {
        return APPLICATION_DATE;
    }
    
    /**
     * Affiche une popup d'informations
     * @param severity - {@link Severity} - Gravité de l'information
     * @param title - {@link title} - Titre du message
     * @param message {@link Message} - Message à afficher
     */
    public static void displayMessage(Severity severity, String title,
            String message)
    {
        FacesMessage msg=new FacesMessage(severity, title, message);
        Utils.displayMessage(msg);
    }
    
    /**
     * Affiche une popup d'information
     * @param msg {@link FacesMessage} - Les message à afficher
     */
    public static void displayMessage(FacesMessage msg)
    {
        try
        {
            FacesContext.getCurrentInstance().addMessage(null,msg);
        }
        catch(NullPointerException ex)
        {}
    }
    
    /**
     * Renvoi le contexte courant de l'application
     * @return {@link ServletContext} - Contexte courant
     */
    public static ServletContext getServletContext()
    {
        return (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
    }
    
    /**
     * Renvoi le répertoire des ressources de l'application
     * @return {@link String} - Répertoire des ressources
     */
    public static String getResourcesPath()
    {
        File resources=new File(Utils.RESOURCES_PATH);
        if(!resources.exists())
        {
            resources.mkdirs();
        }
        return Utils.RESOURCES_PATH;
    }
    
    /**
     * Renvoi le répertoire courant de l'application
     * @return {@link String} - Répertoire courant
     */
    public static String getRealPath()
    {
        return Utils.getServletContext().getRealPath("") + File.separator;
    }
    
    /**
     * Renvoi le répertoire où sont stockés les fichiers uploadés sur le serveur
     * @return {@link String} - Répertoire des téléchargements
     */
    public static String getUploadsPath()
    {
        return Utils.UPLOADS_PATH;
    }
    
    /**
     * Renvoi les droits connus par l'application
     * @return {@link List}<{@link String}> - Liste des droits
     */
    public static List<String> getEnumRights()
    {
        return Utils.RIGHTS;
    }
    
    /**
     * Renvoi le format texte d'une durée depuis sa valeur décimale sous
     * forme <code>HH h mm mins</code>
     * @param time {@link Double double} - La durée en décimal
     * @return {@link String} - Le forat texte de la durée
     */
    public static String getTimeFormat(double time)
    {
        int hours=(int)(time/1);
        int mins=(int)((time-hours)*60);
        return String.format("%02d h %02d mins", hours, mins);
    }
    
    /**
     * Renvoi la date en chaîne de caractères en français
     * @param date {@link java.util.Date} - La date à afficher
     * @return {@link String} - La date en texte <code>[EEEEE dd MMMMM yyyy à HH:mm:ss]</code>
     */
    public static String fullDateFormat(Date date)
    {
        return date==null?"":formatFull.format(date);
    }
    
    /**
     * Renvoi la date en chaîne de caractères en français
     * @param date {@link java.util.Date} - La date à afficher
     * @return {@link String} - La date en texte <code>[dd/MM/yyyy HH:mm:ss]</code>
     */
    public static String dateFormat(Date date)
    {
        return date==null?"":formatMedium.format(date);
    }
    
    /**
     * Renvoi la date en chaîne de caractères en français
     * @param date {@link java.util.Date} - La date à afficher
     * @return {@link String} - La date en texte <code>[dd/MM/yyyy]</code>
     */
    public static String smallDateFormat(Date date)
    {
        return date==null?"":formatSmall.format(date);
    }
    
    /**
     * Renvoi la date en chaîne de caractères en français
     * @param date {@link java.util.Date} - La date à afficher
     * @return {@link String} - La date en texte <code>[EEEEE dd MMMMM yyyy]</code>
     */
    public static String dayDateFormat(Date date)
    {
        return date==null?"":formatDay.format(date);
    }
    
    /**
     * Parse une date suivant le format donné et renvoi la date trouvée
     * @param dateString {@link String} - Date en format texte
     * @param format {@link DateFormat} - Le format à utiliser pour parser
     * @return {@link Date} - La date déduite du parsing
     */
    private static Date dateParseFormat(String dateString, DateFormat format)
    {
        try
        {
            return format.parse(dateString);
        }
        catch (ParseException ex)
        {
//            displayError("Impossible de parser la date '"+dateString+"'", null);
            return null;
        }
    }
    
    /**
     * Renvoi une date depuis un format texte complet <code>[EEEEE dd MMMMM yyyy]</code>
     * @param dateString {@link String} - La date à parser
     * @return {@link Date} - La date trouvée
     */
    public static Date parseFullDate(String dateString)
    {
        return dateParseFormat(dateString, formatFull);
    }
    
    /**
     * Renvoi une date depuis un format texte <code>[dd/MM/yyyy HH:mm:ss]</code>
     * @param dateString {@link String} - La date à parser
     * @return {@link Date} - La date trouvée
     */
    public static Date parseDate(String dateString)
    {
        return dateParseFormat(dateString, formatMedium);
    }
    
    /**
     * Renvoi une date depuis un format texte réduit <code>[dd/MM/yyyy]</code>
     * @param dateString {@link String} - La date à parser
     * @return {@link Date} - La date trouvée
     */
    public static Date parseSmallDate(String dateString)
    {
        return dateParseFormat(dateString, formatSmall);
    }
    
    /**
     * Permet de trier des dates par ordre croissant depuis des dates
     * en format texte <code>[dd/MM/yyyy]</code>
     * @param date1 {@link String} - Première date en format texte
     * @param date2 {@link String} - Deuxième date en format texte
     * @return {@link Integer int} - Retourne:<ul><li> La valeur <code>0</code> si les deux dates sont égales;</li>
     *          <li>En dessous de <code>0</code> Si la première date est antérieure à la seconde;</li>
     *          <li>Au dessus de <code>0</code> Si la première date est postérieure à la seconde.</li></ul>
     */
    public static int sortByDate(Object date1, Object date2)
    {
        if(date1 instanceof String&&date2 instanceof String)
        {
            return ((Utils.parseSmallDate((String)date1)).compareTo(
                    Utils.parseSmallDate((String)date2)));
        }
        else if(date1 instanceof Date&&date2 instanceof Date)
        {
            return ((Date)date1).compareTo((Date)date2);
        }
        return 0;
    }

    /**
     * Vérifie si la première date est antérieure à la seconde
     * @param startDate {@link Date} - Première date
     * @param endDate {@link Date} - Seconde date
     * @return {@link Boolean boolean} - Vrai si la première date est antérieure
     * ou égale à la seconde
     */
    public static boolean verifDate(Date startDate, Date endDate)
    {
        return startDate.before(endDate)||startDate.equals(endDate);
    }
    
    /**
     * Renvoi le nombre max de lignes dans une liste de données
     * @return {@link Integer int} - Nombre de lignes
     */
    public int getMaxDataRows()
    {
        return this.maxDataRows;
    }
    
    /**
     * Remplace chaque caractère d'un texte par une étoile
     * @param text {@link String} - Le texte à convertir
     * @return {@link String} - Le texte contenant les étoiles
     */
    public static String getHiddenString(String text)
    {
        return text.replaceAll(".", "•");
    }
    
    /**
     * Raccourci un texte vers la taille donnée
     * @param text {@link String} - Le texte à raccourcir
     * @param maxSize {@link Integer int} - La taille max
     * @return {@link String} - Le texte plus court
     */
    public static String getShortString(String text,int maxSize)
    {
        return (text!=null&&!text.isEmpty()&&text.length()>maxSize)?text.substring(0, maxSize)+"...":text;
    }
    
    /**
     * Remplace les retours à la ligne d'un texte java par des retours à la
     * ligne en HTML
     * @param text {@link String} - Le texte à convertir
     * @return {@link String} - Le texte HTML
     */
    public static String getBreakLinesString(String text)
    {
        return text.replaceAll("\n", "<br/>").replaceAll(""+(char)13, "");
    }
    
    /**
     * Renvoi la valeur monétaire en chaîne de caractères depuis une
     * valeur réelle
     * @param value {@link Double double float} - La valeur réelle
     * @return {@link String} - La valeur monétaire
     */
    public static String getMonetaryValue(Double value)
    {
        return String.format("%.02f €", value).replace(',', '.');
    }
    
    /**
     * Appelle un méthode d'une classe sur un objet passé en paramètre
     * avec les arguments donnés
     * @param m {@link Method} - La méthode à invoquer
     * @param desc {@link String} - La description de la méthode
     * @param target {@link Object} - L'ojet sur lequel invoquer la méthode
     * @param args {@link Object}[] - La liste des arguments
     * @return {@link Object} - Ce que retourne la méthode
     */
    public static Object callMethod(Method m, String desc, Object target,
            Object... args)
    {
        String description=(desc==null?"":("("+desc+") "));
        try
        {
            String call="Appel de la méthode \""+m.getName()+"\" "+description+" sur "
                    + "l'objet de la classe \""+target.getClass().getName()+"\" "
                    + "avec les paramètres: "+Arrays.toString(args);
//            ApplicationLogger.writeInfo(call);
            ApplicationLogger.displayInfo(call);
            return m.invoke(target, args);
        }
        catch (IllegalAccessException ex)
        {
            ApplicationLogger.writeError("La méthode \""+m.getName()+"\" est"+
                    " interdite d'accès pour la classe \""+target.getClass().getName()
                    +"\"", ex);
        }
        catch (IllegalArgumentException ex)
        {
           ApplicationLogger.writeError("Les arguments pour la méthode \""+
                    m.getName()+"\" sont incorrects pour la classe \""+
                    target.getClass().getName()+"\"", ex);
        }
        catch (InvocationTargetException ex)
        {
            ApplicationLogger.writeError("La méthode \""+m.getName()+"\" n'est"+
                    " pas applicable pour la classe \""+target.getClass().getName()+
                    "\"", ex);
        }
        return null;
    }
    
    /**
     * Permet de récupérer les informations complètes pour une classe qui possède
     * une méthode <code>"getFullString"</code>
     * @param entity {@link Object} - L'objet sur lequel appeler la méthode 
     * <code>"getFullString"</code>
     * @return {@link String} - La description complète de l'objet
     */
    public static String getFullString(Object entity)
    {
        try
        {
            Method getFullString=entity.getClass().getMethod("getFullString");
            String fullString=(String) getFullString.invoke(entity);
            String spacer="\t";
            for(int i=0;i<entity.getClass().getName().length()*2+5;i++)
            {
                spacer+=" ";
            }
            fullString=fullString.replaceAll(", ", 
                                    ",\n"+spacer)
                    .replaceAll("}", "\n"+spacer+"}");
            return fullString;
        }
        catch (NoSuchMethodException ex)
        {
            ApplicationLogger.displayError("Impossible de trouver la méthode \""+
                    "getFullString\" pour la classe \""+entity.getClass().getName()+
                    "\"", ex);
        }
        catch (SecurityException ex)
        {
            ApplicationLogger.displayError("Accès refusé à la méthode \""+
                    "getFullString\" pour la classe \""+entity.getClass().getName()+
                    "\"", ex);
        }
        catch (IllegalAccessException ex)
        {
            ApplicationLogger.displayError("Impossible d'accéder à la méthode \""+
                    "getFullString\" pour la classe \""+entity.getClass().getName()+
                    "\"", ex);
        }
        catch (IllegalArgumentException ex)
        {
            ApplicationLogger.displayError("Arguments invalides pour la méthode \""+
                    "getFullString\" pour la classe \""+entity.getClass().getName()+
                    "\"", ex);
        }
        catch (InvocationTargetException ex)
        {
            ApplicationLogger.displayError("Impossible d'appeler à la méthode \""+
                    "getFullString\" pour la classe \""+entity.getClass().getName()+
                    "\"", ex);
        }
        return null;
    }
    
    /**
     * Renvoi la durée sous forme <code>"HH:mm"</code> pour une durée sous 
     * la forme <code>"HH h mm mins"</code>
     * @param value {@link String} - Durée sous la forme <code>"HH h mm mins"</code>
     * @return {@link String} - Durée sous la forme <code>"HH:mm"</code>
     */
    public static String getDurationString(Object value)
    {
        String duration=((String)value).substring(0, 2)+":"+((String)value).substring(5, 7);
        return duration;
    }
    
    /**
     * Renvoi la durée sous forme <code>"HH h mm mins"</code> pour une durée 
     * sous la forme <code>"HH:mm"</code>
     * @param value {@link String} - Durée sous la forme <code>"HH:mm"</code>
     * @return {@link String} - Durée sous la forme <code>"HH h mm mins"</code>
     */
    public static Object getDurationObject(String value)
    {
        String duration=value;
        return duration.substring(0, 2)+" h "+duration.substring(3, 5)+" mins";
    }
}
