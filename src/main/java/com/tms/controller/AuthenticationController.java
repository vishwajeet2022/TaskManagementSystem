package com.tms.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.tms.exception.BusinessException;
import com.tms.exception.ErrorModel;
import com.tms.model.TMSUser;
import com.tms.service.TMSApplicationService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
public class AuthenticationController {
	@Autowired
	TMSApplicationService tmsApplicationService;

	@PostMapping("/api/auth/login")
	public ResponseEntity<?> login(HttpServletRequest request) {
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		TMSUser userByEmail = tmsApplicationService.getUserByEmail(username);
		return new ResponseEntity<>(userByEmail, HttpStatus.OK);
	}

	@PostMapping("/api/auth/register")
	public ResponseEntity<?> register(@Valid @RequestBody(required = true) TMSUser tmsUser) {
		if (!StringUtils.hasText(tmsUser.getPassword()))
			throw new BusinessException(
					List.of(new ErrorModel(HttpStatus.BAD_REQUEST.value(), "Paasword is Required")));
		TMSUser user = tmsApplicationService.createUser(tmsUser);
		return new ResponseEntity<>(user, HttpStatus.CREATED);
	}

}
