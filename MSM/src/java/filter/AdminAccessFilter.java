/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package filter;

import bean.Utils;
import filter.struct.AccessFilter;
import java.util.Arrays;

/**
 *
 * @author Brian GOHIER
 */
public class AdminAccessFilter extends AccessFilter
{
    public AdminAccessFilter()
    {
        super(Arrays.asList(Utils.ADMIN_RIGHTS), "Administrateur");
    }   
}
