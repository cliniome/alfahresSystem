package com.degla.validators;

import com.degla.system.SpringSystemBridge;
import com.degla.system.SystemService;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

/**
 * Created by snouto on 21/07/15.
 */
@FacesValidator("com.delga.validators.EmployeeIDValidator")
public class EmployeeIDValidator implements Validator {

    private SystemService systemService;


    @PostConstruct
    public void onInit()
    {
        try
        {
            setSystemService(SpringSystemBridge.services());

        }catch (Exception s)
        {
            s.printStackTrace();
        }
    }

    @Override
    public void validate(FacesContext facesContext, UIComponent uiComponent, Object value)
            throws ValidatorException {


            //Check for the inserted Employee ID
            String employeeID = "";
            if(value != null) employeeID = value.toString();

            boolean exists = systemService.getEmployeeService().employeeIDExists(employeeID);

            if(exists)
            {
                //Create an exception message
                FacesMessage msg = new FacesMessage("Duplicate Employee ID/Number is not allowed","Duplicate Employee ID/Number is not allowed");
                msg.setSeverity(FacesMessage.SEVERITY_ERROR);
                //Throw a validator Exception
                throw new ValidatorException(msg);
            }



    }

    public SystemService getSystemService() {
        return systemService;
    }

    public void setSystemService(SystemService systemService) {
        this.systemService = systemService;
    }
}
