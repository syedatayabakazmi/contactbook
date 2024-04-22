package com.contact.contactbook.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.contact.contactbook.entities.User;
import com.contact.contactbook.helper.Message;
import com.contact.contactbook.repo.UserRepository;

import jakarta.servlet.http.HttpSession;


@Controller
public class HomeController {
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@RequestMapping("/")
	public String home(Model model) {
		model.addAttribute("title","Home - Smart Contact Manager");
		return "home";
	}

	@RequestMapping("/about")
	public String about(Model model) {
		model.addAttribute("title","About - Smart Contact Manager");
		return "about";
	}
	
	@RequestMapping("/login")
	public String login(Model model) {
		model.addAttribute("title","Login - Smart Contact Manager");
		return "login";
	}
	
	
	
	@RequestMapping("/register")
	public String register(Model model) {
		model.addAttribute("title","Register - Smart Contact Manager");
		model.addAttribute("user",new User());
		return "register";
	}
	
	@RequestMapping(value = "/do_register", method=RequestMethod.POST)
	public String registerUser(@Validated @ModelAttribute("user") User user,BindingResult result1 , @RequestParam(value="agreement", defaultValue = "false") boolean agreement,
			@RequestParam("newpassword") String newpassword, Model model, HttpSession session) {
		
		try {
			if(!agreement) {
				throw new Exception("you have not agreed terms and conditions");
			}
		if(!user.getPassword().equals(newpassword)) {
			throw new Exception("passowrd does not match");
			
		}
		
		if(result1.hasErrors()) {
			System.out.println("error"+result1);
			model.addAttribute("user",user);
			return "register";
		}
		
			user.setEnabled(true);
			user.setRole("UserRole");
			user.setPassword(passwordEncoder.encode(user.getPassword()));
			 
			User result=this.userRepository.save(user);
			
			System.out.println("Agreement"+agreement);
			System.out.println("user"+result);
			model.addAttribute("user",new User());
			session.setAttribute("message", new Message("Successfully registered", "alert-success"));
			
			return "register";
			
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("user",user);
			session.setAttribute("message", new Message("Something went wrong"+e.getMessage(), "alert-danger"));
			
			return "register";
			
		}
		
	}
	
	@GetMapping("/test")
	@ResponseBody
	public String test() {
		User user=new User();
		user.setName("tayaba");
		userRepository.save(user);
		return "working";
	}
}
