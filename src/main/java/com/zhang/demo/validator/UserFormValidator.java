package com.zhang.demo.validator;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.zhang.demo.model.User;

public class UserFormValidator implements Validator {

	@Override
	public boolean supports(Class<?> clazz) {
		return User.class.equals(clazz);

	}

	@Override
	public void validate(Object target, Errors errors) {
		User user = (User) target;

		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "NotEmpty.userForm.name");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "country", "NotEmpty.userForm.country");
		//ValidationUtils.rejectIfEmptyOrWhitespace(errors, "skill", "NotEmpty.userForm.skill");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "sex", "NotEmpty.userForm.sex");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "number", "NotEmpty.userForm.number");

		if (user.getSkill() == null || user.getSkill().size() < 2) {
			errors.rejectValue("skill", "Valid.userForm.skill");
		}

		if(user.getFramework() == null || user.getFramework().size()==0){
			errors.rejectValue("framework", "Valid.userForm.framework");
		}
	}

}
