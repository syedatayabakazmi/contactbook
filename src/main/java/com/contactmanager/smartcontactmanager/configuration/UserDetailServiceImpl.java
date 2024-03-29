package com.contactmanager.smartcontactmanager.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.contactmanager.smartcontactmanager.entities.User;
import com.contactmanager.smartcontactmanager.repo.UserRepository;

public class UserDetailServiceImpl implements UserDetailsService {

	@Autowired
	private UserRepository userRepository;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		  
		User user=userRepository.getUserByUserName(username);
		
		if(user==null) {
			throw new UsernameNotFoundException("could not found user");
		}
		
		CustomUserDetails customUserDetails= new CustomUserDetails(user);
		return customUserDetails;
	}

}
