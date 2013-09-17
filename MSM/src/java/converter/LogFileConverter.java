/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package converter;

import bean.log.ApplicationLogger;
import converter.struct.FileConverter;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

/**
 *
 * @author Brian GOHIER
 */
@ManagedBean(name = "logFileConverter")
@RequestScoped
public class LogFileConverter extends FileConverter
{

    public LogFileConverter()
    {
        super(ApplicationLogger.getFilesInPath());
    }
}
