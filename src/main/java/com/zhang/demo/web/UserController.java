package com.zhang.demo.web;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zhang.demo.model.User;
import com.zhang.demo.service.UserService;
import com.zhang.demo.validator.UserFormValidator;

@Controller
public class UserController {

	private final Logger logger = LoggerFactory.getLogger(UserController.class);

	@Autowired
	private UserService userService;

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String index(Model model) {

		logger.debug("index()");

		return "redirect:/users";
	}

	 @InitBinder
	 protected void initBinder(WebDataBinder binder) {
	 	binder.setValidator(new UserFormValidator());
	 }

	@RequestMapping(value = "/users", method = RequestMethod.POST)
	public String saveOrUpdateUser(Model model, @ModelAttribute("userForm") @Validated User user, BindingResult result) {

		logger.debug("saveOrUpdateUser() : {}", user);

		if (result.hasErrors()) {
			populateDefaultModel(model);

			return "users/userform";
		}
		populateDefaultModel(model);
		return "users/userform";
	}

	// show user
	@RequestMapping(value = "/users/{id}", method = RequestMethod.GET)
	public String showUser(@PathVariable("id") int id, Model model) {

		logger.debug("showUser() : {}", id);

		User user = userService.findById(id);

		model.addAttribute("user", user);
		return "users/show";
	}



	@RequestMapping(value = "/users", method = RequestMethod.GET)
	public String showAllUsers(Model model) {

		logger.debug("showAllUsers()");

		List<User> users = new ArrayList<User>();

		User user1 = new User();
		user1.setId(1);
		user1.setEmail("1@gamil.com");
		user1.setName("zhang");
		user1.setFramework(Arrays.asList(new String[] { "Spring", "Spring Boot", "MyBaits" }));

		users.add(user1);

		User user2 = new User();
		user2.setId(1);
		user2.setEmail("2@gamil.com");
		user2.setName("wang");
		user2.setFramework(Arrays.asList(new String[] { "Bootstrap", "Angular Js"}));

		users.add(user2);

		model.addAttribute("users", users);

		return "users/userlist";

	}

	@RequestMapping(value = "/users/add", method = RequestMethod.GET)
	public String showAddUserForm(Model model) {

		logger.debug("showAddUserForm()");

		User user = new User();

		// set default value
		user.setName("mkyong123");
		user.setEmail("test@gmail.com");
		user.setAddress("abc 88");
		user.setNewsletter(true);
		//user.setSex("M");
		user.setFramework(Arrays.asList("Spring MVC", "Bootstrap"));
		//user.setSkill(new ArrayList<String>(Arrays.asList("Struts1", "Hibernate")));
		user.setCountry("SG");
		//user.setNumber(1);

		model.addAttribute("userForm", user);

		populateDefaultModel(model);

		return "users/userform";

	}

	private void populateDefaultModel(Model model) {
		List<Integer> numbers = new ArrayList<Integer>();
		numbers.add(1);
		numbers.add(2);
		numbers.add(3);
		numbers.add(4);
		numbers.add(5);
		model.addAttribute("numberList", numbers);

		List<String> frameworksList = new ArrayList<String>();
		frameworksList.add("Spring MVC");
		frameworksList.add("Struts");
		frameworksList.add("Spring boot");
		frameworksList.add("Bootstrap");
		frameworksList.add("AngularJS");
		model.addAttribute("frameworkList", frameworksList);

		Map<String,String> javaSkill = new LinkedHashMap<String,String>();
		javaSkill.put("Hibernate", "Hibernate");
		javaSkill.put("Spring", "Spring");
		javaSkill.put("Mybatis", "Mybatis");
		javaSkill.put("Struts1", "Struts");
		model.addAttribute("javaSkills", javaSkill);

		Map<String, String> country = new LinkedHashMap<String, String>();
		country.put("CN", "China");
		country.put("SG", "Singapore");
		country.put("JP", "Japan");
		country.put("US", "United Stated");
		model.addAttribute("countryList", country);
	}

}