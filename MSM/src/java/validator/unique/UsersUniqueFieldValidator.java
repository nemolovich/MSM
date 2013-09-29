/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package validator.unique;

import bean.facade.UsersFacade;
import entity.Users;
import javax.ejb.EJB;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import validator.unique.struct.UniqueFieldValidator;

/**
 *
 * @author Brian GOHIER
 */
@FacesValidator("userUniqueFieldValidator")
public class UsersUniqueFieldValidator extends UniqueFieldValidator<Users, UsersFacade>implements Validator
{
    @EJB
    private UsersFacade usersFacade;
    
    public UsersUniqueFieldValidator()
    {
        super(Users.class);
    }
    
    @Override
    public void validate(FacesContext context, UIComponent component, Object value)
            throws ValidatorException
    {
        super.entityFacade = this.usersFacade;
        super.validate(context, component, value);
    }
}