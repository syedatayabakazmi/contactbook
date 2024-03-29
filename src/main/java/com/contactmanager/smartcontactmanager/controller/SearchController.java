package com.contactmanager.smartcontactmanager.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.contactmanager.smartcontactmanager.entities.Contact;
import com.contactmanager.smartcontactmanager.entities.User;
import com.contactmanager.smartcontactmanager.repo.ContactRepository;
import com.contactmanager.smartcontactmanager.repo.UserRepository;

@RestController
public class SearchController {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ContactRepository contactRepository;
	
	
	@GetMapping("/search/{query}")
	public ResponseEntity<?> search(Principal principal, @PathVariable("query") String query){
		User user=this.userRepository.getUserByUserName(principal.getName());
		
		List<Contact> contacts=this.contactRepository.findByNameContainingAndUser(query, user);
		
		return ResponseEntity.ok(contacts);
	}
}
