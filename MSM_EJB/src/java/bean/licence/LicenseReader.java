/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bean.licence;

import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

/**
 *
 * @author nemo
 */
@Named(value = "license")
@Singleton
@Startup
@ApplicationScoped
public class LicenseReader
{
    private static final String license_file="license/LICENSE";
    
    public LicenseReader()
    {
    }
    
    public String getLicenseString()
    {
        return license_file;
    }
}
