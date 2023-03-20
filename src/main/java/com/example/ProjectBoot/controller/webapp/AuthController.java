package com.example.ProjectBoot.controller.webapp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.example.ProjectBoot.bean.dto.UserRegistrationDto;
import com.example.ProjectBoot.dto.Page;
import com.example.ProjectBoot.entity.User;
import com.example.ProjectBoot.service.UserService;

@Controller
@RequestMapping("/auth")
public class AuthController {
	
	@Autowired
	private UserService userService;

	private Page setPage(String title) {
		Page page = new Page();
		page.setTitle(title);
		page.setTemplate("th_"+title.toLowerCase());
		return page;
	}
	
	@GetMapping("/login")
	public ModelAndView login(Model model) {
		model.addAttribute("page",setPage("Login"));
		return new ModelAndView("th_layout");
	}
	
	@GetMapping("/register")
	public ModelAndView register(Model model) {
		model.addAttribute("page",setPage("Register"));
		return new ModelAndView("th_layout");
	}
	
	@PostMapping("/register")
	public ModelAndView register(Model model, UserRegistrationDto registerUser) {
		String registrationError = null;
		if(!registerUser.getPassword().equals(registerUser.getPasswordConfirm())) {
			registrationError = "Passwords must match";
		} else if(userService.findByUsername(registerUser.getUsername()) != null) {
			registrationError = "Username is already used.";
		} else if(userService.findByEmail(registerUser.getEmail()) != null) {
			registrationError = "Email is already used.";
		}
		if(registrationError != null) {
			model.addAttribute("registrationError",registrationError);
			model.addAttribute("page",setPage("Register"));
			return new ModelAndView("th_layout");
		}
		User user = new User();
		user.setUsername(registerUser.getUsername());
		user.setEmail(registerUser.getEmail());
		user.setFullName(registerUser.getFullName());
		user.setPassword(registerUser.getPassword());
		userService.registerNewUserAccount(user); 
		model.addAttribute("page",setPage("Login"));
		return new ModelAndView("th_layout");
	}

}
