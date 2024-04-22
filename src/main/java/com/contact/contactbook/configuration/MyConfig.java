package com.contact.contactbook.configuration;

import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration.WebMvcAutoConfigurationAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class MyConfig {
	
	@Bean
	public UserDetailsService getUserDetailsService() {
		return new UserDetailServiceImpl();
	}

	
	@Bean
	public BCryptPasswordEncoder passwordEncoder()
	
	{
		return new BCryptPasswordEncoder();
	}
	
	 @Bean
	    public DaoAuthenticationProvider authenticationProvider() {
	        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

	        authProvider.setUserDetailsService(this.getUserDetailsService() );
	        authProvider.setPasswordEncoder(passwordEncoder());

	        return authProvider;
	    }

	 
	 @Bean
	    public AuthenticationManager authenticationManagerBean(AuthenticationConfiguration Configuration) throws Exception {
	        return Configuration.getAuthenticationManager();
	    }

	   @Bean
	    public SecurityFilterChain filterChain(HttpSecurity http, AuthenticationManager authenticationManager) throws Exception {
	        http.csrf().disable().cors().disable()
	            .authorizeRequests(authorizeRequests -> authorizeRequests
	                .requestMatchers("/user/**", "/admin/**").authenticated() // Requires authentication for paths starting with /user/ and /admin/
	                .requestMatchers("/").permitAll() // Allow access to the root URL without authentication
	                .requestMatchers(HttpMethod.OPTIONS).permitAll() // Allow access to OPTIONS requests without authentication
	                .anyRequest().permitAll() // Permit all other requests
	            )
	            .formLogin() // Configure form-based login
	                .loginPage("/login") // Custom login page URL
	                .permitAll() // Allow access to login page without authentication
	                .and()
	            .logout() // Configure logout
	                .permitAll(); // Allow access to logout URL without authentication

	        return http.build();
	    }


}