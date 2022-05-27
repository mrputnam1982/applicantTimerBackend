package com.mikep.applicantTimer.Validators;

import com.mikep.applicantTimer.Models.Customer;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
@Component
public class UserValidator implements Validator {
    @Override
    public boolean supports(Class<?> aClass) {
        return Customer.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        Customer customer = (Customer)o;
//        if(client.getPassword().length() < 8) {
//            errors.rejectValue("password", "Length must be at least 8 characters");
//        }
        if(!customer.getPassword().equals(customer.getConfirmPassword())) {
            errors.rejectValue("confirmPassword", "Match", "Passwords must match");
        }
    }
}
