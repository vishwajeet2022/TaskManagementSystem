package com.tms.config;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/*
 * IF YOU DONT WANT CUSTOM AUTHENTICAN PROVIDER THEN 
 * 1. MAKE /api/login public
 * 2. create a simple service which does thhe same authentication 
 * 3.in the controller you can call the auth service and the username password will be in requestbody instead of header
 */
@Component
public class CustomUserNamePasswordAuthenticationProvider implements AuthenticationProvider {

	UserDetailsService userDetailsService;
	PasswordEncoder passwordEncoder;
	

	public CustomUserNamePasswordAuthenticationProvider(UserDetailsService userDetailsService2,
			PasswordEncoder passwordEncoder) {
		super();
		this.userDetailsService = userDetailsService2;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		UsernamePasswordAuthenticationToken authTOken = null;
		if (authentication != null) {
			String name = authentication.getName();
			String password = authentication.getCredentials().toString();
			UserDetails userDetails = userDetailsService.loadUserByUsername(name);
			if (passwordEncoder.matches(password, userDetails.getPassword())) {
				authTOken = new UsernamePasswordAuthenticationToken(name, password, userDetails.getAuthorities());
			} else
				throw new BadCredentialsException("Incorrect Password");
		}
		return authTOken;

	}

	@Override
	public boolean supports(Class<?> authentication) {
		return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
	}

}
