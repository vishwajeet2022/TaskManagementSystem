package com.tms;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.tms.model.Authority;
import com.tms.model.Authority.AuthorityCode;
import com.tms.repository.IAuthorityRepository;

@Component
public class DataLoader implements CommandLineRunner {

	@Autowired
	IAuthorityRepository authorityRepository;

	@Override
	@Transactional
	public void run(String... args) throws Exception {
		// TODO Auto-generated method stub
		AuthorityCode[] authorityCodes = AuthorityCode.values();
		List<Authority> dbAuthorties = authorityRepository.findAll();
		for (AuthorityCode code : authorityCodes) {
			boolean isAuthPresent = false;
			for (Authority x : dbAuthorties) {
				if (x.getAuthorityCode() == code) {
					isAuthPresent = true;
					break;
				}
			}
			if (isAuthPresent == false) {
				Authority authority = new Authority();
				authority.setAuthorityCode(code);
				authorityRepository.save(authority);
			}

		}

	}

}
