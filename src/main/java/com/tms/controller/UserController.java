package com.tms.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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

	@GetMapping("/users")
	public ResponseEntity<?> getAllUsers() {
		List<TMSUser> allUsers = tmsApplicationService.getAllUsers();
		if (allUsers != null && !allUsers.isEmpty())
			return new ResponseEntity<>(allUsers, HttpStatus.OK);
		else
			throw new BusinessException(List.of(new ErrorModel(HttpStatus.NOT_FOUND.value(), "USER NOT FOUND")));
	}

	@PostMapping("/users")
	public ResponseEntity<?> createuser(@Valid @RequestBody(required = true) TMSUser tmsUser) {
		TMSUser user = tmsApplicationService.createUser(tmsUser);
		return new ResponseEntity<>(user, HttpStatus.CREATED);
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
		tmsApplicationService.deleteUser (userId);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@PutMapping("users/{userId}")
	public ResponseEntity<?> updateUser(@PathVariable(required = true) Long userId,
			@Valid @RequestBody(required = true) TMSUser user) {
		TMSUser updatedUser = tmsApplicationService.updateUser(userId,user);
		return new ResponseEntity<>(updatedUser, HttpStatus.OK);
	}
}
