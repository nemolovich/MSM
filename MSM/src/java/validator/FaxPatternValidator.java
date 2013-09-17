/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

/**
 *
 * @author Brian GOHIER
 */
@FacesValidator("faxPatternValidator")
@ManagedBean
@RequestScoped
public class FaxPatternValidator implements Validator
{
    private final static String PHONE_PATTERN = "^\\(?(\\d{2})\\)?([. ]?(\\d{2})){3}?[. ]?(\\d{2})$";
     
    private final static Pattern PHONE_COMPILED_PATTERN = Pattern.compile(PHONE_PATTERN);

    public FaxPatternValidator() {
    }

    @Override
    public void validate(FacesContext context, UIComponent component,
        Object value) throws ValidatorException
    {
        if (value == null || "".equals((String)value))
        {
            return;
        }
        
        Matcher matcher = PHONE_COMPILED_PATTERN.matcher((String)value);
         
        if (!matcher.matches())
        {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Numéro de fax invalide",
                    "Numéro de fax invalide (format 01.23.45.67.89)");
            throw new ValidatorException(msg);
        }
    }
    
}
