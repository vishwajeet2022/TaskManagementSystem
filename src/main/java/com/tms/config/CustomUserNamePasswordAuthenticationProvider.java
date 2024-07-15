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
