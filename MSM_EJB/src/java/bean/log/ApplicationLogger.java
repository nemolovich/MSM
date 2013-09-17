/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bean.log;

import bean.Files;
import bean.Utils;
import static bean.log.ApplicationLogger.displayError;
import static bean.log.ApplicationLogger.displayInfo;
import static bean.log.ApplicationLogger.displayWarning;
import static bean.log.ApplicationLogger.startWrite;
import static bean.log.ApplicationLogger.writeInfo;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.context.ApplicationScoped;
import javax.faces.application.FacesMessage;
import javax.faces.event.AjaxBehaviorEvent;
import javax.inject.Named;

/**
 *
 * @author Brian GOHIER
 */
@Named(value = "logger")
@Singleton
@Startup
@ApplicationScoped
public class ApplicationLogger
{
    private static final Logger log=Logger.getLogger(ApplicationLogger.class.getName());
    private static File logFile;
    private static String fileName;
    private static String logPath="log"+File.separator;
    private static File selectedFile;
    private static PrintWriter out;
    private static final String INFO_TAG="INFO";
    private static final String WARNING_TAG="WARNING";
    private static final String ERROR_TAG="ERROR";
    private static final String HUGE_SEPARATOR="###################################################################";
    private static final String BIG_SEPARATOR="##=================================================================";
    private static final String SEPARATOR="##~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~";
    private static final String SMALL_SEPARATOR="##-----------------------------------------------------------------";
    private static String lastLine=null;
    private static String lastStyle=null;
    private static int nbLastLine=0;
    private static Date lastArchived=Calendar.getInstance().getTime();
    private static final long MAX_LOGFILE_SIZE=300000;

    public ApplicationLogger()
    {
        lastArchived=Calendar.getInstance().getTime();
    }

    public File getLogFile()
    {
        return logFile;
    }

    public void setLogFile(File logFile)
    {
        ApplicationLogger.logFile = logFile;
    }

    public File getSelectedFile()
    {
        return selectedFile;
    }

    public void setSelectedFile(File selectedFile)
    {
        ApplicationLogger.selectedFile = selectedFile;
    }
    
    public void ajaxSelect(AjaxBehaviorEvent e)
    {
    }
    
    private static String date()
    {
        return "["+Utils.dateFormat(Calendar.getInstance().getTime())+"]";
    }
    
    private static String info()
    {
        return " "+INFO_TAG+": ";
    }
    
    private static String warning()
    {
        return " "+WARNING_TAG+": ";
    }
    
    private static String error()
    {
        return " "+ERROR_TAG+": ";
    }
    
    private static void addHeader()
    {
        String header=HUGE_SEPARATOR+"\r\n"
                                + "## Fichier journal pour l'application: '"+Utils.APPLICATION_NAME+"'";
        for(int i=Utils.APPLICATION_NAME.length();i<=22;i++)
        {
            header+=" ";
        }
        header+=" ##\r\n";
        header+="## Date de création:        "+date()+"                ##\r\n";
        header+=HUGE_SEPARATOR+"\r\n";
        out.print(header);
        out.flush();
    }
    
    private static String getStyleFrom(String message)
    {
        return getStyleFrom(message, null, null);
    }
    
    private static String getStyleFrom(String message, String style)
    {
        return getStyleFrom(message, style, null);
    }
    
    private static String getStyleFrom(String message, String style, String date)
    {
        String classe="info";
        String div="";
        String spanClass="";
        if(date!=null)
        {
            Date d=Utils.parseDate(date.substring(1, date.length()-1));
            if(d!=null)
            {
                date=Utils.fullDateFormat(d);
                spanClass+=" icon_info\" title=\""+(style!=null?style+": ":"")+date;
            }
        }
        spanClass+="\"/>\n";
        if(style==null&&lastStyle!=null)
        {
            div+=
            "        </ul><!-- Fin pour le style \""+lastStyle+"\" -->\n"+
            "    </div>\n"+
            "</div>\n";
            lastStyle=null;
        }
        else if(style!=null&&style.equals(WARNING_TAG))
        {
            classe="warn";
        }
        else if(style!=null&&style.equals(ERROR_TAG))
        {
            classe="error";
        }
        if(style!=null&&lastStyle!=null&&!lastStyle.equals(classe))
        {
            div+=
            "        </ul>\n"+
            "    </div>\n"+
            "</div><!-- Fermeture du style \""+lastStyle+"\" -->\n";
            div+=
            "<div class=\"ui-messages ui-widget\">\n"+
            "    <div class=\"ui-messages-"+classe+" ui-corner-all\">\n"+
            "        <span class=\"ui-messages-"+classe+"-icon"+spanClass+
            "        <ul>\n";
        }
        else if(style!=null&&lastStyle==null)
        {
            div+=
            "<div class=\"ui-messages ui-widget\">\n"+
            "    <div class=\"ui-messages-"+classe+" ui-corner-all\">\n"+
            "        <span class=\"ui-messages-"+classe+"-icon"+spanClass+
            "        <ul>\n";
        }
        if(style!=null)
        {
            div+=
            "            <li>\n"+
            "                <span class=\"ui-messages-"+classe+"-summary\">\n";
            lastStyle=style;
        }
        div+=message.replaceAll("''", "&apos;")+"\n";
        if(style!=null)
        {
            div+=
            "                </span>\n"+
            "            </li>\n";
        }
        return div;
    }
    
    /**
     * Renvoi le format HTML du journal
     * @return {@link String} - Le format HTML
     */
    public static String getHTMLLog()
    {
        lastStyle=null;             // Redéfinit le dernier style utilisé à null pour le début
        Reader reader = null;       // Permet de lire dans le journal
        boolean inTab=false;        // Si on est dans un tableau
        String logHTML="";          // Le résultat
        String logLine="";          // La ligne en cours
        int details_report_id=0;    // Identifiant d'élément détaillable
        boolean inDetails=false;    // Si on est dans un élément détaillable
        try
        {
            selectedFile=selectedFile==null?logFile:selectedFile;
            FileInputStream fileReader=new FileInputStream(selectedFile);
            reader=new BufferedReader(new InputStreamReader(fileReader,"UTF8"));
            int c;
            while((c=reader.read())!=-1)
            {
                if(c=='\n')
                {
                    if(logLine.equals(BIG_SEPARATOR))
                    {
                        logHTML+=getStyleFrom("\n<hr class=\"big_sep\"/>\n\n",null);
                    }
                    else if(logLine.equals(SEPARATOR))
                    {
                        logHTML+=getStyleFrom("\n<hr class=\"sep\"/>\n\n",null);
                    }
                    else if(logLine.equals(SMALL_SEPARATOR))
                    {
                        /* Si on est dans les détails et qu'on arrive à un SMALL_SEPARATOR
                         * alors on peut fermer les détails
                         */
                        if(inDetails)
                        {
                            logHTML+=
    "                    </span>\n" +
    "                </span>\n" +
    "                <script id=\"details_report_"+details_report_id+"_s\" type=\"text/javascript\">\n" +
    "                    PrimeFaces.cw('Inplace','details_report_"+details_report_id+"_js',\n" +
    "                                {id:'details_report_"+details_report_id+"',\n" +
    "                                    effect:'slide',\n" +
    "                                    effectSpeed:'normal',\n" +
    "                                    event:'click',\n" +
    "                                    toggleable:true\n" +
    "                                });\n" +
    "                </script>";
                            details_report_id++;
                        }
                        logHTML+=getStyleFrom("\n<hr class=\"small_sep\"/>\n\n",null);
                    }
                    else if(logLine.equals(HUGE_SEPARATOR))
                    {
                        /* Si on arrive sur un HUGE_SEPARATOR alors soit on créer soit
                         * soit on ferme un tableau
                         */
                        inTab^=true;
                        if(inTab)
                        {
                            logHTML+="<TABLE class=\"infos_header\">\n";
                        }
                        else
                        {
                            logHTML+="</TABLE>\n";
                        }
                    }
                    else if(logLine.startsWith("## "))  // Tous les commentaires
                    {
                        logLine=logLine.substring(3);
                        if(logLine.endsWith(" ##"))
                        {
                            logLine=logLine.substring(0,logLine.indexOf(" ##"));
                        }
                        if(logLine.contains(":")) // Les attributs à valeur définie
                        {
                            String field=logLine.substring(0, logLine.indexOf(":"));
                            String value=logLine.substring(field.length()+1);
                            while(value.startsWith(" "))
                            {
                                value=value.substring(1);
                            }
                            while(value.endsWith(" "))
                            {
                                value=value.substring(0,value.length()-1);
                            }
                            if((value.startsWith("'")&&value.endsWith("'"))||
                                    (value.startsWith("[")&&value.endsWith("]")))
                            {
                                value=value.substring(1,value.length()-1);
                            }
                            if(inTab)
                            {
                                if(field.equalsIgnoreCase("Date de création")&&
                                        selectedFile.equals(logFile))
                                {
                                    lastArchived=Utils.parseDate(value);
                                }
                                logHTML+=
                                    "    <TR>\n" +
                                    "        <TD class=\"label\">"+field+":</TD>\n" +
                                    "        <TD class=\"fields\">"+value+"</TD>\n" +
                                    "    </TR>\n";
                            }
                            else
                            {
                                logHTML+=getStyleFrom("COMMENT: Field: "+field+", value: "+value+"<br/>\n");
                            }
                        }
                        else    // Les commentaires simples
                        {
                            if(inTab)
                            {
                                logHTML+=
                                    "    <TR>\n" +
                                    "        <TD class=\"label\" colspan=\"2\">\n"+
                                    logLine+
                                    "        </TD>\n" +
                                    "    </TR>\n";
                            }
                            else
                            {
                                logHTML+=getStyleFrom("COMMENT: "+logLine+"<br/>\n");
                            }
                        }
                    }
                    // INFO
                    else if(logLine.length()>21&&logLine.substring(22).startsWith(INFO_TAG+": "))
                    {
                        logHTML+=getStyleFrom(logLine.substring(24+INFO_TAG.length()), INFO_TAG, logLine.substring(0, 21));
                    }
                    // WARINING
                    else if(logLine.length()>21&&logLine.substring(22).startsWith(WARNING_TAG+": "))
                    {
                        logHTML+=getStyleFrom(logLine.substring(24+WARNING_TAG.length()), WARNING_TAG, logLine.substring(0, 21));
                    }
                    // ERROR
                    else if(logLine.length()>21&&logLine.substring(22).startsWith(ERROR_TAG+": "))
                    {
                        logHTML+=getStyleFrom(logLine.substring(24+ERROR_TAG.length()), ERROR_TAG, logLine.substring(0, 21));
                    }
                    else if(logLine.startsWith("\tCause: ")) // Causes d'erreurs
                    {
                        logHTML+=
    "                "+logLine+"<br/>\n" +
    "                Détails de l'erreur: \n" +
    "                <span id=\"details_report_"+details_report_id+"\" class=\"ui-inplace ui-hidden-container\">\n" +
    "                    <span id=\"details_report_"+details_report_id+"_display\" class=\"ui-inplace-display\" style=\"background:none;display:inline\">\n" +
    "                        <a href=\"#\" class=\"blankLink\" title=\"Afficher les détails de l'erreur\">Afficher</a>\n" +
    "                    </span><br/>\n" +
    "                    <span id=\"details_report_"+details_report_id+"_content\" class=\"ui-inplace-content\" style=\"display:none\">\n" +
    "                        <a href=\"#\" class=\"blankLink\" name=\"details_report_"+details_report_id+"_hide\" title=\"Masquer les détails\"\n" +
    "                            onclick=\"details_report_"+details_report_id+"_js.hide();\" >Masquer</a>\n<br/>\n";
                        inDetails=true;
                    }
                    else if(logLine.startsWith("\tObjet: \"")) // Descriptions d'objets
                    {
                        logHTML+=
    "                Détails de l'objet: \n" +
    "                <span id=\"details_report_"+details_report_id+"\" class=\"ui-inplace ui-hidden-container\">\n" +
    "                    <span id=\"details_report_"+details_report_id+"_display\" class=\"ui-inplace-display\" style=\"background:none;display:inline\">\n" +
    "                        <a href=\"#\" class=\"blankLink\" title=\"Afficher les détails de l'objet\">Afficher</a>\n" +
    "                    </span><br/>\n" +
    "                    <span id=\"details_report_"+details_report_id+"_content\" class=\"ui-inplace-content\" style=\"display:none\">\n" +
    "                        <a href=\"#\" class=\"blankLink\" name=\"details_report_"+details_report_id+"_hide\" title=\"Masquer les détails\"\n" +
    "                            onclick=\"details_report_"+details_report_id+"_js.hide();\" >Masquer</a>\n<br/>\n" +
    "                            "+logLine.substring(9)+"\n<br/>";
                            
                        inDetails=true;
                    }
                    // États d'objets
                    else if(logLine.startsWith("\t[INSIDE] ")||logLine.startsWith("\t[DELETED]"))
                    {
                        
                        if(inDetails)
                        {
                            logHTML+=
    "                    </span>\n" +
    "                </span>\n" +
    "                <script id=\"details_report_"+details_report_id+"_s\" type=\"text/javascript\">\n" +
    "                    PrimeFaces.cw('Inplace','details_report_"+details_report_id+"_js',\n" +
    "                                {id:'details_report_"+details_report_id+"',\n" +
    "                                    effect:'slide',\n" +
    "                                    effectSpeed:'normal',\n" +
    "                                    event:'click',\n" +
    "                                    toggleable:true\n" +
    "                                });\n" +
    "                </script>";
                            details_report_id++;
                        }
                        logHTML+=logLine.substring(10).replaceAll("''","'")+"<br/>\n\r";
                    }
                    else // Non reconnu
                    {
                        logHTML+=logLine+"<br/>\n";
                    }
                    logLine="";
                }
                else if(c=='\r')
                {
                    // Rien car '\r\n' dans le log
                }
                else // Ajouter caractère à la ligne
                {
                    logLine+=(char)c;
                }
            }
        }
        catch (FileNotFoundException ex)
        {
            displayError("Impossible de trouver le fichier journal", ex);
        }
        catch (UnsupportedEncodingException ex)
        {
            displayError("Le type d'encodage du fichier journal est incorrect", ex);
        }
        catch (IOException ex)
        {
            displayError("Erreur (I/O) lors de la lecture du fichier journal", ex);
        }
        finally
        {
            if(reader!=null)
            {
                try
                {
                    reader.close();
                }
                catch (IOException ex)
                {
                    displayError("Erreur lors de la fermeture du fichier journal", ex);
                }
            }
        }
        return logHTML;
    }
    
    /**
     * Démarre le journal vers le fichier dont le nom (path) est donné
     * @param fileName {@link String} - Chemin du fichier
     */
    public static void start(String fileName)
    {
        ApplicationLogger.fileName=fileName;
        logFile=new File(Utils.getResourcesPath()+logPath+fileName+".log");
        selectedFile=logFile;
        displayInfo("Début d'enregistrement du journal");
        if(!startWrite())
        {
            displayError("Impossible d'écrire dans le journal",null);
        }
    }
    
    /**
     * Renvoi la liste des fichiers présents dans le dossier de journal sans
     * compter le journal courant
     * @return {@link List}<{@link File}> - La liste des fichiers dans le dossier
     * du journal sans compter le journal courant
     */
    public static List<File> getFilesInPath()
    {
        String path=Utils.getResourcesPath()+logPath;
        List<File> files=Files.getFilesInPath(path);
        if(files.contains(logFile))
        {
            files.remove(logFile);
        }
        List<File> fs=new ArrayList<File>(files);
        for(File f:fs)
        {
            if(!f.getName().startsWith(fileName))
            {
                files.remove(f);
            }
        }
        Collections.sort(files, Collections.reverseOrder());
        return files;
    }
    
    /**
     * Renvoi le format <code>Journal du dd:MM:yyyy à HH:mm:ss</code>
     * à partir du nom du fichier
     * @param fileName {@link String} - Nom du fichier
     * @return {@link String} - <code>Journal du dd:MM:yyyy à HH:mm:ss</code>
     */
    public static String getFileNameAndDate(String fileName)
    {
        int fileNameLen=ApplicationLogger.fileName.length();
        if(fileName.length()!=fileNameLen+24||
                !fileName.startsWith(ApplicationLogger.fileName))
        {
            return fileName;
        }
        String result="Journal du ";
        int year=Integer.valueOf(fileName.substring(fileNameLen+1,fileNameLen+5));
        int month=Integer.valueOf(fileName.substring(fileNameLen+6,fileNameLen+8));
        int day=Integer.valueOf(fileName.substring(fileNameLen+9,fileNameLen+11));
        int h=Integer.valueOf(fileName.substring(fileNameLen+12,fileNameLen+14));
        int m=Integer.valueOf(fileName.substring(fileNameLen+15,fileNameLen+17));
        int s=Integer.valueOf(fileName.substring(fileNameLen+18,fileNameLen+20));
        SimpleDateFormat format=new SimpleDateFormat("dd MMM yyyy à HH:mm:ss");
        Calendar cal=Calendar.getInstance();
        cal.set(year, (month-1), day, h, m, s);
        return result+=format.format(cal.getTime());
    }
    
    /**
     * Archive le journal courant et va le vider
     * @param auto {@link Boolean} - Définit si l'archivage est automatique
     * ou non
     * @return {@link Boolean boolean} - Vrai si l'archive s'est correctement
     * déroulée et que le nouveau journal s'est correctement mis en route
     */
    public static synchronized boolean archive(boolean auto)
    {
        if(logFile.length()<MAX_LOGFILE_SIZE&&auto)
        {
            return false;
        }
        if(auto)
        {
            writeWarning("Archivage automatique du journal");
        }
        stopWrite();
        SimpleDateFormat format=new SimpleDateFormat("_yyyy_MM_dd_HH_mm_ss");
        String todayString=format.format(lastArchived);
        File archiveFile=new File(Utils.getResourcesPath()+logPath+
                fileName+todayString+".log");
        boolean ok=true;
        try
        {
            Files.copy(logFile, archiveFile);
            writeInfo("Archivage du journal");
        }
        catch (IOException ex)
        {
            displayError("Impossible de créer une archive du journal courant", ex);
            ok=false;
        }
        if(ok)
        {
            FileWriter fooWriter;
            try
            {
                fooWriter = new FileWriter(logFile, false);
                fooWriter.close();
            }
            catch (IOException ex)
            {
                displayError("Impossible de vider l'ancien journal", ex);
                ok=false;
            }
        }
        if(ok)
        {
            writeInfo("Création d'un nouveau journal");
            ok=startWrite();
        }
        if(!ok)
        {
            Utils.displayMessage(FacesMessage.SEVERITY_ERROR,
                    "Journal non archivé", "Le journal n'a pas pu être archivé");
            return false;
        }
        lastArchived=Calendar.getInstance().getTime();
        Utils.displayMessage(FacesMessage.SEVERITY_INFO,
                "Journal archivé", "Le journal a bien été archivé");
        return true;
    }
    
    /**
     * Démarre les écritures dans le journal
     * @return {@link Boolean boolean} - Vrai si aucune erreur
     */
    public static boolean startWrite()
    {
        boolean created=false;
        
        if(!logFile.exists())
        {
            displayInfo("Création du fichier '"+
                    logFile.getAbsolutePath()+"'");
            if(Files.createIfNotExists(logFile,false))
            {
                displayInfo("Fichier '"+
                        logFile.getAbsolutePath()+"' créé");
                created=true;
            }
            else
            {
                displayError("Le fichier '"+logFile.getAbsolutePath()+"' n'a pa pu être créé",null);
                return false;
            }
        }
        try
        {
            FileOutputStream fileWriter=new FileOutputStream(logFile, true);
            Writer writer=new BufferedWriter(new OutputStreamWriter(fileWriter,"UTF8"));
            out=new PrintWriter(writer);
        }
        catch (FileNotFoundException ex)
        {
            displayError("Le fichier journal n'a pas pu être trouvé", ex);
            return false;
        }
        catch (UnsupportedEncodingException ex)
        {
            displayError("L'encodage du fichier journal n'a pas été supporté", ex);
            return false;
        }
        catch (IOException ex)
        {
            displayError("Le fichier n'est pas accessible en écriture", ex);
            return false;
        }
        
        if(logFile.canRead()&&logFile.canWrite())
        {
            if(created||logFile.length()==0)
            {
                addHeader();
            }
//            try
//            {
//                int n=Integer.parseInt("STRING");
//            }
//            catch (NumberFormatException ex)
//            {
//                writeError("Un test d'erreur", ex);
//            }
            write("\r\n"+BIG_SEPARATOR);
            writeInfo("Début des écritures dans le journal");
            write(SEPARATOR+"\r\n");
            return true;
        }
        return false;
    }
    
    /**
     * Arrête les écritures dans le journal
     * @return {@link Boolean boolean} - Vrai si aucune erreur
     */
    public static boolean stopWrite()
    {
        try
        {
            write("\r\n"+SEPARATOR);
            writeInfo("Fin des écritures dans le journal");
            write(BIG_SEPARATOR);
            out.close();
            return true;
        }
        catch (Exception ex)
        {
            return false;
        }
    }
    
    /**
     * Stoppe le journal
     */
    public static void destroy()
    {
        ApplicationLogger.stopWrite();
        ApplicationLogger.displayInfo("Fin d'enregistrement du journal");
    }
    
    public static void addSmallSep()
    {
        write(SMALL_SEPARATOR);
    }
    
    public static void addSep()
    {
        write(SEPARATOR);
    }
    
    public static void addBigSep()
    {
        write(BIG_SEPARATOR);
    }
    
    public static void addHugegSep()
    {
        write(HUGE_SEPARATOR);
    }
    
    public static void writeInfo(String message)
    {
        write(message, INFO_TAG);
    }
    
    public static void writeWarning(String message)
    {
        write(message, WARNING_TAG);
    }
    
    public static void writeError(String message, Exception ex)
    {
        write(SMALL_SEPARATOR);
        write(message, ERROR_TAG, ex);
        write(SMALL_SEPARATOR);
    }
    
    public static void write(String message)
    {
        write(message, null, null);
    }
    
    public static void write(String message, String style)
    {
        write(message, style, null);
    }
    
    /**
     * Écrit dans le fichier journal le message donné dans le style donné
     * @param message {@link String} - Message à écrire
     * @param style {@link String} - Importance de l'information
     * @param ex {@link Exception} - Erreur eventuelle à afficher en détail
     */
    public static void write(String message, String style, Exception ex)
    {
        if(lastLine==null||(lastLine!=null&&!lastLine.equals(message)))
        {
            String add=(style!=null?(style.equals(INFO_TAG)?info():
                        style.equals(WARNING_TAG)?warning():
                        style.equals(ERROR_TAG)?error():""):"");
            String before=lastStyle!=null?lastStyle:"";
            for(int i=before.length();i<26;i++)
            {
                before=" "+before;
            }
            if(nbLastLine>0)
            {
//                out.append(before+": Même ligne "+nbLastLine+" fois\r\n");
//                out.flush();
                displayInfo(before+": Même ligne "+nbLastLine+" fois\n");
            }
            if(style!=null)
            {
                add=date()+add;
                lastStyle=style;
            }
            out.append(add+message.replaceAll("'", "''")+"\r\n");
            out.flush();
            if(ex!=null)
            {
                out.append("\tCause: "+ex.getClass().getName()+": "+ex.getLocalizedMessage()+":\r\n");
                out.flush();
                for(StackTraceElement e:ex.fillInStackTrace().getStackTrace())
                {
                    out.append("\t"+e.toString()+"\r\n");
                    out.flush();
                }
            }
            if(style!=null)
            {
                if(style.equals(ERROR_TAG))
                {
                    displayError(message, ex);
                }
                else if(style.equals(WARNING_TAG))
                {
                    displayWarning(message);
                }
                else if(style.equals(INFO_TAG))
                {
                    displayInfo(message);
                }
            }
            lastLine=message;
            nbLastLine=0;
        }
        else
        {
            nbLastLine++;
            out.flush();
        }
    }
    
    public static void displayInfo(String message)
    {
        ApplicationLogger.log.log(Level.INFO, message);
    }
    
    public static void displayWarning(String message)
    {
        ApplicationLogger.log.log(Level.WARNING, message);
    }
    
    public static void displayError(String message, Exception ex)
    {
        ApplicationLogger.log.log(Level.SEVERE, message, ex);
    }
}