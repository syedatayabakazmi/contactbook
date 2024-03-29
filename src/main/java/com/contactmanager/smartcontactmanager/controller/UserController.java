package com.contactmanager.smartcontactmanager.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.contactmanager.smartcontactmanager.entities.Contact;
import com.contactmanager.smartcontactmanager.entities.User;
import com.contactmanager.smartcontactmanager.helper.Message;
import com.contactmanager.smartcontactmanager.repo.ContactRepository;
import com.contactmanager.smartcontactmanager.repo.UserRepository;

import jakarta.servlet.http.HttpSession;





@Controller
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ContactRepository contactRepository;

	@ModelAttribute
	private void commondata(Model model, Principal principal) {
		String userName=principal.getName();
		System.out.println(userName);
		
		User user=this.userRepository.getUserByUserName(userName);
		System.out.println(user);
		
		model.addAttribute("user",user);
	}
	
	@RequestMapping("/user_dashboard")
	public String dashboard(Model model, Principal principal) {
		model.addAttribute("title","User Dashboard - Smart Contact Manager");
		return "normal/user_dashboard";
	}
	
	@GetMapping("/addcontact")
	public String addcontact(Model model) {
		model.addAttribute("title","Add Contact - Smart Contact Manager");
		model.addAttribute("contact", new Contact());
		return "normal/addcontact";
	}
	
	@PostMapping("/process-contact")
	public String processContact(@ModelAttribute Contact contact,@RequestParam("profileImage") MultipartFile file, Principal principal,HttpSession session) {
		
		try {
		String name=principal.getName();
		User user=this.userRepository.getUserByUserName(name);
		
		if(file.isEmpty()) {
			System.out.println("file is empty!");
			contact.setImageUrl("Default.png");
		}
		else {
			contact.setImageUrl(file.getOriginalFilename());
			File savefile= new ClassPathResource("static/img").getFile();
			Path path=Paths.get(savefile.getAbsolutePath()+File.separator+file.getOriginalFilename());
			Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
			System.out.println("file uploaded!!");

		}
		
		contact.setUser(user);
		user.getContacts().add(contact);
		this.userRepository.save(user);
		System.out.println(contact);
		System.out.println("added");
		session.setAttribute("message", new Message("contact Successfully Added", "success"));
		}catch (Exception e) {
			session.setAttribute("message", new Message("Something went wrong"+e.getMessage(), "danger"));
			
			System.out.println("error"+e.getMessage());
			e.printStackTrace();
		}
		
		
return "normal/addcontact";
	}
	
	@GetMapping("/showcontact/{page}")
	public String showcontact(@PathVariable("page") Integer page,Model model, Principal principal) {
		model.addAttribute("title","View Contact - Smart Contact Manager");
	
		String userName=principal.getName();
		User user=this.userRepository.getUserByUserName(userName);
		
		Pageable pageable=PageRequest.of(page, 8);
		Page<Contact> contacts=this.contactRepository.findContactByUser(user.getId(), pageable);
		
		model.addAttribute("contacts",contacts);
		model.addAttribute("currentpage", page);
		model.addAttribute("totalpage", contacts.getTotalPages());
		return "normal/showcontact";
	}
	
	
	@RequestMapping("/contact/{cid}")
	public String contactdetail(@PathVariable("cid") Integer cid, Model model, Principal principal) {
		System.out.println(cid);
	Optional<Contact> contactoptional=	this.contactRepository.findById(cid);
	Contact contact=contactoptional.get();
	
String	userName=principal.getName();
User user=this.userRepository.getUserByUserName(userName);

	if(user.getId()==contact.getUser().getId()) {
		model.addAttribute("contact",contact);
		model.addAttribute("title", contact.getName());
	}
	
		return "normal/contactdetail";
	}
	
	
	@GetMapping("/delete/{cid}")
	public String deletecontact(@PathVariable("cid") Integer cid, Model model, HttpSession session, Principal principal) {
	Optional<Contact>	contactoptional=this.contactRepository.findById(cid);
	Contact contact=contactoptional.get();
	
	String userName=principal.getName();
	User user=this.userRepository.getUserByUserName(userName);
	user.getContacts().remove(contact);
	this.userRepository.save(user);
	
		
		session.setAttribute("message", new Message("contact Successfully Deleted", "success"));
		
		return "redirect:/user/showcontact/0";
	}
	
	
	@PostMapping("/updatecontact/{cid}")
	public String updatecontact(@PathVariable("cid") Integer cid, Model model, HttpSession session) {
	Optional<Contact>	contactoptional=this.contactRepository.findById(cid);
	Contact contact=contactoptional.get();
	
	model.addAttribute("title", "update contact");
		model.addAttribute("contact", contact);
		
		return "normal/updatecontact";
	}
	
	@PostMapping("/process-update/{cid}")
	public String processUpdate(@PathVariable("cid") Integer cid,@ModelAttribute Contact contact, @RequestParam("profileImage") MultipartFile file, HttpSession session, Principal principal) {
	    try {
	    	
	    	Optional<Contact>	contactoptional=this.contactRepository.findById(cid);
	    	Contact oldcontact=contactoptional.get();
	       
	            if (!file.isEmpty()) {
	            	
	            	File deleteFile = new ClassPathResource("static/img").getFile();
	            	File file1=new File(deleteFile, oldcontact.getImageUrl());
	            	file1.delete();
	            	
	                File saveFile = new ClassPathResource("static/img").getFile();
	                Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());
	                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
	                contact.setImageUrl(file.getOriginalFilename());
	            } else {
	                contact.setImageUrl(oldcontact.getImageUrl());
	            }
	            User user = this.userRepository.getUserByUserName(principal.getName());
	            contact.setUser(user);
	            this.contactRepository.save(contact);
	            session.setAttribute("message", new Message("contact Successfully updated", "success"));
	        
	    } catch (Exception e) {
	        e.printStackTrace();
	        // Handle the exception appropriately, such as logging the error or returning an error message
	    }
	   
	     return "redirect:/user/contact/" + contact.getCid();
	}

	
	@GetMapping("/profile")
	public String userprofile(Model model, Principal principal) {
		model.addAttribute("title","User Profile - Smart Contact Manager");
		return "normal/profile";
	}
	
	
	@PostMapping("/updateuser/{id}")
	public String updateuser(@PathVariable("id") Integer id, Model model, HttpSession session) {
	Optional<User>	useroptional=this.userRepository.findById(id);
	User user=useroptional.get();
	
	model.addAttribute("title", "update user");
		model.addAttribute("user", user);
		
		return "normal/updateuser";
	}
	
	
	

	@PostMapping("/process-userupdate/{id}")
	public String processuserUpdate(@PathVariable("id") Integer id, @Validated @ModelAttribute("user") User user, @RequestParam("profileImage") MultipartFile file, HttpSession session, Principal principal) {
	    try {
	        Optional<User> userOptional = this.userRepository.findById(id);
	        User oldUser = userOptional.orElseThrow(() -> new RuntimeException("User not found")); // Throws exception if user is not found

	        if (!file.isEmpty()) {
	            // Delete the old image file if it exists
	            String imageUrl = oldUser.getImageUrl();
	            if (imageUrl != null) {
	                File deleteFile = new ClassPathResource("static/img").getFile();
	                File fileToDelete = new File(deleteFile, imageUrl);
	                if (fileToDelete.exists()) {
	                    fileToDelete.delete();
	                }
	            }

	            // Save the new image file
	            File saveFile = new ClassPathResource("static/img").getFile();
	            Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());
	            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
	            user.setImageUrl(file.getOriginalFilename());
	        } else {
	            user.setImageUrl(oldUser.getImageUrl());
	        }

	        this.userRepository.save(user);
	        session.setAttribute("message", new Message("User successfully updated", "success"));
	    } catch (Exception e) {
	        e.printStackTrace();
	        // Handle the exception appropriately, such as logging the error or returning an error message
	    }

	    return "redirect:/user/profile";
	}

	@GetMapping("/setting")
	public String setting(Model model) {
		model.addAttribute("title","Setting - Smart Contact Manager");
		
		return "normal/setting";
	}
	
	
	@PostMapping("/change-password")
	public String changepassword(@RequestParam("oldpassword") String oldpassword, @RequestParam("newpassword") String newpassword, Principal principal
			,HttpSession session) {
		String userName=principal.getName();
		User user=this.userRepository.getUserByUserName(userName);
		
		if(this.bCryptPasswordEncoder.matches(oldpassword, user.getPassword())) {
			user.setPassword(this.bCryptPasswordEncoder.encode(newpassword));
			this.userRepository.save(user);
			 session.setAttribute("message", new Message("password successfully changed!!", "success"));
		}
		else {
			 session.setAttribute("message", new Message("please enter correct old password!!", "danger"));
			 return "redirect:/user/setting";
		}
		
		return "redirect:/user/user_dashboard";
	}
	
}
