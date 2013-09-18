/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import bean.Files;
import bean.Utils;
import bean.log.ApplicationLogger;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

/**
 *
 * @author Brian GOHIER
 */
@SessionScoped
@ManagedBean(name="fileUploadController")
public class FileUploadController
{
//    private FilePath filePath;
    private String defaultPath=null;
    
//    @EJB
//    private FilePathFacade filePathFacade;
    
    public FileUploadController()
    {
        Files.createIfNotExists(new File(Utils.getResourcesPath()+
                Utils.getUploadsPath()), true);
    }
    
//    public FilePath getFilePath()
//    {
//        return this.filePath;
//    }
    
//    public void setFilePath(FilePath filePath)
//    {
//        if(filePath!=null)
//        {
//            this.defaultPath=Utils.getResourcesPath()+Utils.getUploadsPath()+
//                    filePath.getFilePath()+File.separator;
//            /**
//             * [
//             * Va vider le répertoire temporaire si il n'est pas vide
//             */
//            if(new File(this.defaultPath).exists()&&
//                    new File(this.defaultPath).isDirectory()&&
//                    this.defaultPath.endsWith(FilePath.TEMP_FOLDER+File.separator))
//            {
//                if(new File(this.defaultPath).listFiles()!=null&&
//                        new File(this.defaultPath).listFiles().length>0)
//                {
//                    String entityName=filePath.getFilePath().substring(0,
//                            filePath.getFilePath().indexOf(File.separator));
//                    ApplicationLogger.writeError("Deleting temp folder"+
//                            (entityName==null?"":(" for entity \""+entityName+"\"")), null);
//                    for(File f:new File(this.defaultPath).listFiles())
//                    {
//                        if(!f.delete())
//                        {
//                            ApplicationLogger.writeError("Impossible de supprimer "
//                                    + "le fichier \""+f.getName()+"\"", null);
//                        }
//                    }
//                }
//            }
//            /**
//             * ]
//             */
//        }
//    }
    
    public void handleFileUpload(FileUploadEvent event)
    {
        UploadedFile file = event.getFile();
        if(this.defaultPath==null)
        {
            ApplicationLogger.writeError("Erreur lors de l'envoi du fichier \""+
                    file.getFileName()+"\" sur le serveur: "
                    + "le chemin défini est incorrect", null);
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Echec de l'envoi", "Le fichier \""+file.getFileName()+
                    " n'a été envoyé sur le serveur car le "
                    + "chemin défini est incorrect");
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return;
        }
        /**
         * [
         * Va créer les répertoires et sous-répertoires s'ils n'existent pas
         */
        String stringPath=this.defaultPath.substring(0,
                this.defaultPath.indexOf(File.separator)+1);
        while(!stringPath.equals(this.defaultPath))
        {
            stringPath=this.defaultPath.substring(0,
                    this.defaultPath.indexOf(File.separator,
                    stringPath.length()+1)+1);
            Files.createIfNotExists(new File(stringPath), true);
        }
        /**
         * ]
         */
        String newFile = this.defaultPath+file.getFileName();
        try
        {
            if(Files.copy(file.getInputstream(), new File(newFile),
                    file.getFileName()))
            {
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
                        "Fichier envoyé", "Le fichier \""+file.getFileName()+
                        " a été envoyé sur le serveur");
                FacesContext.getCurrentInstance().addMessage(null, msg);
                ApplicationLogger.writeWarning("Fichier \""+
                        file.getFileName()+"\" envoyé sur le serveur");
            }
            else
            {
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "Erreur envoi", "Erreur lors de l'envoi du fichier \""+
                        file.getFileName()+"\" sur le serveur");
                FacesContext.getCurrentInstance().addMessage(null, msg);
                ApplicationLogger.writeError("Erreur lors de l'envoi du fichier \""+
                        file.getFileName()+"\" sur le serveur", null);
            }
        }
        catch (IOException ex)
        {
            ApplicationLogger.writeError("Erreur lors de l'envoi du fichier \""+
                    file.getFileName()+"\" sur le serveur", ex);
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "Erreur envoi", "Erreur lors de l'envoi du fichier \""+
                        file.getFileName()+"\" sur le serveur");
                FacesContext.getCurrentInstance().addMessage(null, msg);
        }
        
    }
    
    public boolean isEmptyFolder()
    {
        if(this.defaultPath==null)
        {
            return true;
        }
        File parent=new File(this.defaultPath);
        return Files.isEmptyFolder(parent);
    }
    
    public String getDeleteAllMessage()
    {
        return "Vous êtes sur le point de supprimer tous les fichiers "
                + "du serveur, êtes-vous certain(e) de vouloir continuer?";
    }
    
    public String getDeleteMessage(File file)
    {
        return "Vous êtes sur le point de supprimer le fichier \""
                +file.getName().replaceAll("'", "`")+"\" "
                + "du serveur, êtes-vous certain(e) de vouloir continuer?";
    }
    
    public void deleteFile(File file)
    {
        this.deletePath(file, false);
    }
    
    public void deleteFolder(File folder)
    {
        this.deletePath(folder, true);
    }
    
    public void deleteAllFiles()
    {
        File parent=new File(this.defaultPath);
        if(parent.exists()&&parent.isDirectory()&&parent.listFiles()!=null)
        {
            for(File file:parent.listFiles())
            {
                this.deleteFile(file);
            }
        }
        else
        {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Erreur suppression", "Impossible de supprimer le contenu "
                    + "du répertoire \""+parent.getPath()+"\"");
            FacesContext.getCurrentInstance().addMessage(null, msg);
            ApplicationLogger.writeError("Impossible de supprimer le contenu "
                    + "du répertoire \""+parent.getPath()+"\"", null);
        }
    }
    
    public void deletePath(File path, boolean isFolder)
    {
        String pathType=isFolder?"répertoire":"fichier";
        String pathTypeUpper=pathType.substring(0, 1).toUpperCase()+
                pathType.substring(1, pathType.length());
        try
        {
            if(Files.deletePath(path, isFolder))
            {
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
                        pathTypeUpper+" supprimé", "Le "+pathType+" \""+path.getName()+
                        "\" a été supprimé du serveur");
                FacesContext.getCurrentInstance().addMessage(null, msg);
                ApplicationLogger.writeWarning("Supression du "+pathType+" \""
                        +path.getPath()+"\"");
            }
            else
            {
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "Erreur suppression", "Impossible de supprimer le "+pathType+" \""
                        +path.getPath()+"\"");
                FacesContext.getCurrentInstance().addMessage(null, msg);
                ApplicationLogger.writeError("Impossible de supprimer le "+pathType+" \""
                        +path.getPath()+"\"", null);
            }
        }
        catch (FileNotFoundException ex)
        {
            ApplicationLogger.writeError("Impossible de localiser le "+pathType+" \""
                    +path.getPath()+"\"", null);
        }
    }
}
