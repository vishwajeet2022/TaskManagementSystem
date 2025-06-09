package com.tms.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class CustomUserNamePasswordAuthenticationProvider implements AuthenticationProvider {

	UserDetailsService userDetailsService;
	PasswordEncoder passwordEncoder;

	@Autowired
	public void setUserDetailsService(UserDetailsService userDetailsService) {
		this.userDetailsService = userDetailsService;
	}

	@Autowired
	public PasswordEncoder getPasswordEncoder() {
		return passwordEncoder;
	}

	public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
		this.passwordEncoder = passwordEncoder;
	}

	public CustomUserNamePasswordAuthenticationProvider(UserDetailsService userDetailsService,
			PasswordEncoder passwordEncoder) {
		super();
		this.userDetailsService = userDetailsService;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		UsernamePasswordAuthenticationToken authToken = null;
		if (authentication != null) {
			String name = authentication.getName();
			String password = authentication.getCredentials().toString();
			UserDetails userDetails = userDetailsService.loadUserByUsername(name);
			if (passwordEncoder.matches(password, userDetails.getPassword())) {
				authToken = new UsernamePasswordAuthenticationToken(name, password, userDetails.getAuthorities());
			} else
				throw new BadCredentialsException("Incorrect Password");
		}
		return authToken;

	}

	//determine if the current authentication provider supports handling UsernamePasswordAuthenticationToken or its subclasses.
	@Override
	public boolean supports(Class<?> authentication) {
		return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
	}

}
