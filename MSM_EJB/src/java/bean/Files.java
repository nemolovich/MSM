/*
 * Remplace la classe "Files" créée en Java 1.7 mais qui n'est pas complète
 * pour le 1.6
 */
package bean;

import bean.log.ApplicationLogger;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

/**
 *
 * @author Brian GOHIER
 */
@Named(value="files")
@Singleton
@Startup
@ApplicationScoped
public class Files
{
    /**
     * Renvoi le caractère de séparation
     * @return #{@link String} - Caractère de séparation
     */
    public static String separator()
    {
        return File.separator;
    }
    
    /**
     * Copie un fichier depuis les ressources du serveur sur le site web
     * diffusé et retourne le lien vers ce fichier
     * @param file {@link File} - Fichier à récupérer depuis les ressources
     * @param filePath {@link FilePath} - Chemin vers le fichier depuis le
     * dossier de ressources et de téléchargements
     * @return {@link String} - Le lien de téléchargement du fichier
     */
//    public static String getFileLink(File file, FilePath filePath)
//    {
//        File from=new File(Utils.getResourcesPath()+Utils.getUploadsPath()+
//                filePath.getFilePath()+File.separator+file.getName());
//        File to=new File(Utils.getRealPath()+Utils.getUploadsPath()+
//                filePath.getFilePath()+File.separator+file.getName());
//        try
//        {
//            if(!Files.copy(from, to))
//            {
//                ApplicationLogger.writeError("Erreur lors de la diffusion du fichier \""+
//                        to.getAbsolutePath()+"\" sur le serveur", null);
//            }
//        }
//        catch (IOException ex)
//        {
//            ApplicationLogger.writeError("Impossible de diffuser le fichier \""+
//                    to.getAbsolutePath()+"\" sur le serveur", ex);
//        }
//        return ("/"+Utils.APPLICATION_NAME+"/"+Utils.getUploadsPath()+
//                filePath.getFilePath()+"/"+file.getName())
//                    .replaceAll("\\\\", "/");
//    }
    
    /**
     * Copie un fichier suivant son flux vers un fichier de destination
     * @param is {@link InputStream} - Le flux du fichier source
     * @param to {@link File} - Le fichier de destination
     * @param fileName {@link String} - Nom du fichier
     * @return {@link Boolean boolean} - Vrai si la copie s'est correctement
     * déroulée
     * @throws IOException
     */
    public static boolean copy(InputStream is, File to, String fileName)
            throws IOException
    {
        FileOutputStream fos=null;
        try
        {
            fos = new FileOutputStream(to);
            int BUFFER_SIZE = 80000;
            byte[] buffer = new byte[BUFFER_SIZE];
            int a;
            while(true)
            {
                a = is.read(buffer);
                if(a<0)
                {
                    break;
                }
                fos.write(buffer, 0, a);
                fos.flush();
            }
        }
        catch(IOException ex)
        {
            throw ex;
        }
        finally
        {
            try
            {
                if(fos!=null)
                {
                        fos.close();
                }
                if(is!=null)
                {
                    is.close();
                }
            }
            catch(IOException ex)
            {
                ApplicationLogger.displayError("Erreur lors de la fermeture des "
                        + "flux pour le fichier \""+fileName+"\"", ex);
                return false;
            }
        }
        return true;
    }
    
    /**
     * Copie un fichier source vers la destination donnée
     * @param from {@link File} - Fichier source
     * @param to {@link File} - Fichier de destination
     * @return {@link Boolean boolean} - Vrai si la copie s'est correctement
     * déroulée
     * @throws IOException
     * @see #copy(java.io.InputStream, java.io.File, java.lang.String) 
     */
    public static boolean copy(File from, File to)
            throws IOException
    {
        InputStream is = new FileInputStream(from);
        if(!to.getParentFile().exists())
        {
            to.getParentFile().mkdirs();
        }
        return Files.copy(is, to, from.getName());
    }
    
    /**
     * Renvoi la taille d'un fichier au format texte suivant sa taille
     * @param size {@link long} - La taille du fichier
     * @return {@link String} - La taille au format texte
     */
    public String getStringSize(long size)
    {
        String s=String.format("%d o",size);
        double maxCoef=0.6;
        if(((float)size)/1000000000>=maxCoef)
        {
            s=String.format("%.2f Go",((float)size)/1000000000);
        }
        else if(((float)size)/1000000>=maxCoef)
        {
            s=String.format("%.2f Mo",((float)size)/1000000);
        }
        else if(((float)size)/1000>=maxCoef)
        {
            s=String.format("%.2f Ko",((float)size)/1000);
        }
        return s;
    }
    
    /**
     * Créer un fichier ou un dossier s'il n'existe pas
     * @param path {@link String} - Chemin vers le fichier ou dossier
     * @param isFolder {@link Boolean boolean} - Précise s'il s'agit d'un
     * fichier ou d'un dossier
     * @return {@link Boolean boolean} - Renvoi vrai si le fichier ou dossier
     * a bien été créé
     */
    public static boolean createIfNotExists(File path, boolean isFolder)
    {
        if(path.exists())
        {
            return true;
        }
        else
        {
            try
            {
                if(!path.getParentFile().exists())
                {
                    if(!path.getParentFile().mkdirs())
                    {
                        ApplicationLogger.displayError("Impossible de créer les "
                                + "dossiers parents \""+
                                path.getParentFile().getName()+"\"", null);
                    }
                }
                if(!isFolder&&path.createNewFile()||
                    isFolder&&path.mkdir())
                {
                    return !isFolder&&path.isFile()||isFolder&&path.isDirectory();
                }
                else
                {
                    ApplicationLogger.displayError("Impossible de créer le "+(isFolder?"dossier":
                            "fichier")+" nommé \""+path.getName()+"\"", null);
                }
            }
            catch(IOException ex)
            {
                ApplicationLogger.displayError("Erreur lors de la création du "+(isFolder?"dossier":
                        "fichier")+" nommé \""+path.getName()+"\"", ex);
            }
            return false;
        }
    }
    
    /**
     * Déplace un fichier ou un dossier et son contenu vers une destination
     * donnée
     * @param from {@link File} - Fichier ou dossier source
     * @param to {@link File} - Fichier ou dossier de destination
     * @return {@link Boolean boolean} - Vrai si tout s'est correctement déroulé
     */
    public static boolean move(File from, File to) throws IOException
    {
        if(from.isFile())
        {
            Files.copy(from, to);
            return true;
        }
        else if(from.isDirectory())
        {
            Files.createIfNotExists(to, true);
            if(from.listFiles()!=null&&from.listFiles().length>0)
            {
                for(File file:from.listFiles())
                {
                    Files.copy(file, new File(to.getPath()+File.separator+
                            file.getName()));
                }
            }
            if(Files.deleteContent(from))
            {
                if(Files.deleteFolder(from))
                {
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * Renvoi la liste des fichiers présent dans un dossier donné
     * @param path {@link String} - Le chemin dans lequel rechercher
     * @return {@link List}<{@link File}> - La liste des fichiers présents
     * dans le dossier donné
     */
    public static List<File> getFilesInPath(String path)
    {
        List<File> files = new ArrayList<File>();
        File[] list = new File(path).listFiles();
        if(list!=null)
        {
            for(int i=0;i<list.length;i++)
            {
                if(list[i].isFile())
                {
                    files.add(list[i]);
                }
            }
        }
        return files;
    }
    
    /**
     * Permet de savoir si un dossier est vide ou s'il n'existe pas
     * @param folder {@link File} - Dossier à rechercher
     * @return {@link Boolean boolean} - Vrai si le dossier n'existe pas
     * ou s'il est vide
     */
    public static boolean isEmptyFolder(File folder)
    {
        return (!folder.exists()||folder.isDirectory())&&
                ((folder.listFiles()!=null&&folder.listFiles().length==0)||
                    folder.listFiles()==null);
    }
    
    /**
     * Supprime le contenu d'un dossier
     * @param folder {@link File} - Le dossier d'où supprimer le contenu
     * @return {@link Boolean boolean} - Vrai si tout le contenu a été
     * correctement supprimé
     * @throws FileNotFoundException 
     */
    public static boolean deleteContent(File folder)
            throws FileNotFoundException
    {
        if(folder.exists()&&folder.isDirectory()&&folder.listFiles()!=null)
        {
            for(File file:folder.listFiles())
            {
                Files.deleteFile(file);
            }
            return true;
        }
        return false;
    }
    
    /**
     * Supprime un fichier
     * @see #deletePath(java.io.File, boolean) 
     * @param file {@link File} - Fichier à supprimer
     * @return {@link Boolean boolean} - Vrai si le fichier a bien été
     * supprimé
     * @throws FileNotFoundException 
     */
    public static boolean deleteFile(File file)
            throws FileNotFoundException
    {
        return Files.deletePath(file, false);
    }
    
    /**
     * Supprime un dossier
     * @see #deletePath(java.io.File, boolean) 
     * @param folder {@link File} - Dossier à supprimer
     * @return {@link Boolean boolean} - Vrai si le dossier a bien été
     * supprimé
     * @throws FileNotFoundException 
     */
    public static boolean deleteFolder(File folder)
            throws FileNotFoundException
    {
        return Files.deletePath(folder, true);
    }
    
    /**
     * Supprime un fichier ou un dossier donné
     * @param path {@link File} - Fichier ou dossier à supprimer
     * @param isFolder {@link Boolean boolean} - Définit s'il s'agit d'un
     * dossier
     * @return {@link Boolean boolean} - Vrai si le fichier ou le dossier a
     * bien été supprimé
     * @throws FileNotFoundException 
     */
    public static boolean deletePath(File path, boolean isFolder)
            throws FileNotFoundException
    {
        if(path.exists())
        {
            if(path.delete())
            {
                return true;
            }
            else
            {
                return false;
            }
        }
        else
        {
            throw new FileNotFoundException(isFolder?"Répertoire":"Fichier"+
                    " \""+path.getPath()+"\" non trouvé");
        }
    }
}