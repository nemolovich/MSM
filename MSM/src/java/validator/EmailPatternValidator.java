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
@FacesValidator("emailPatternValidator")
@ManagedBean
@RequestScoped
public class EmailPatternValidator implements Validator
{    
    private final static String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
     
    private final static Pattern EMAIL_COMPILED_PATTERN = Pattern.compile(EMAIL_PATTERN);

    public EmailPatternValidator()
    {
    }
    
    @Override
    public void validate(FacesContext context, UIComponent component, Object value)
            throws ValidatorException
    {
        if (value == null || "".equals((String)value))
        {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Mail invalide","Le champs 'mail' ne peut être vide");
            throw new ValidatorException(msg);
        }
        
        Matcher matcher = EMAIL_COMPILED_PATTERN.matcher((String)value);
         
        if (!matcher.matches())
        {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Mail invalide","Le format de l'adresse mail est invalide");
            throw new ValidatorException(msg);
        }
    }
}