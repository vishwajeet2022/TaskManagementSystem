package com.tms.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.tms.model.Authority;
import com.tms.model.TMSUser;
import com.tms.repository.IUserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

	@Autowired
	IUserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		// TODO Auto-generated method stub
		String password = null;
		List<GrantedAuthority> authorties = null;
		Optional<TMSUser> optional = userRepository.findByEmail(email);
		if (optional.isPresent()) {
			TMSUser tmsUser = optional.get();
			password = tmsUser.getPassword();
			authorties = new ArrayList<>();
			authorties.add(new SimpleGrantedAuthority(tmsUser.getRole().name()));
			for (Authority authortity : tmsUser.getAuthorities()) {
				authorties.add(new SimpleGrantedAuthority(authortity.getAuthorityCode().name()));
			}
			return new User(email, password, authorties);

		} else {
			throw new UsernameNotFoundException("User Details not found for user : " + email);

		}

	}

}
