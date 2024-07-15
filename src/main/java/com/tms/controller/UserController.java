package com.tms.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.tms.Service.TMSApplicationService;
import com.tms.exception.BusinessException;
import com.tms.exception.ErrorModel;
import com.tms.model.TMSUser;

import jakarta.validation.Valid;

@RestController
public class UserController {

	@Autowired
	TMSApplicationService tmsApplicationService;

	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	PasswordEncoder passwordEncoder;

	@GetMapping("/users")
	public ResponseEntity<?> getAllUsers() {
		List<TMSUser> allUsers = tmsApplicationService.getAllUsers();
		if (allUsers != null && !allUsers.isEmpty())
			return new ResponseEntity<>(allUsers, HttpStatus.OK);
		else
			throw new BusinessException(List.of(new ErrorModel(HttpStatus.NOT_FOUND.value(), "USER NOT FOUND")));
	}

	
	@GetMapping("/users/{userId}")
	public ResponseEntity<?> getUser(@PathVariable(required = true) Long userId) {
		TMSUser userDetails = tmsApplicationService.getUser(userId);
		if (userDetails != null)
			return new ResponseEntity<>(userDetails, HttpStatus.OK);
		else
			throw new BusinessException(List.of(new ErrorModel(HttpStatus.NOT_FOUND.value(), "USER NOT FOUND")));
	}

	@DeleteMapping("/users/{userId}")
	public ResponseEntity<?> deleteUser(@PathVariable(required = true) Long userId) {
		tmsApplicationService.deleteUser(userId);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@PutMapping("users/{userId}")
	public ResponseEntity<?> updateUser(@PathVariable(required = true) Long userId,
			@Valid @RequestBody(required = true) TMSUser user) {
		if (!StringUtils.hasText(user.getPassword()))
			throw new BusinessException(List.of(new ErrorModel(HttpStatus.BAD_REQUEST.value(), "Pasword is Required")));
		TMSUser updatedUser = tmsApplicationService.updateUser(userId, user);
		return new ResponseEntity<>(updatedUser, HttpStatus.OK);
	}
}
