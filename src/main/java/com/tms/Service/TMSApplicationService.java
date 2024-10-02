package com.tms.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tms.exception.BusinessException;
import com.tms.exception.ErrorModel;
import com.tms.exception.IErrorMessageConstants;
import com.tms.model.Authority;
import com.tms.model.TMSUser;
import com.tms.model.TMSUser.Role;
import com.tms.model.Task;
import com.tms.repository.IAuthorityRepository;
import com.tms.repository.ITaskRepository;
import com.tms.repository.IUserRepository;

@Service
public class TMSApplicationService {

	@Autowired
	IAuthorityRepository authorityRepository;
	@Autowired
	IUserRepository userRepository;
	@Autowired
	ITaskRepository taskRepository;
	@Autowired
	PasswordEncoder passwordEncoder;

	@Transactional
	public Task createTask(Task taskTo) {
		taskTo.setTaskId(null);
		TMSUser userDetails = getUserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
		taskTo.setTaskOwner(userDetails);
		return taskRepository.save(taskTo);
	}

	@PostFilter("filterObject!=null?filterObject.taskOwner.email  == authentication.name:true")
	public List<Task> getAllTasks() {
		List<Task> allTasks = taskRepository.findAll();
		return allTasks;
	}

	@PostAuthorize("returnObject!=null?returnObject.taskOwner.email == authentication.name:true")
	public Task getTask(Long taskId) {
		Task task = null;
		Optional<Task> optionalTask = taskRepository.findById(taskId);
		if (optionalTask.isPresent()) {
			task = optionalTask.get();
		}
		return task;
	}

	@Transactional
	@PostAuthorize("returnObject.taskOwner.email == authentication.name")
	public Task updateTaskDetails(Long taskId, Task taskInputTo) {
		Task taskTOUpdate = getTask(taskId);
		if (taskTOUpdate != null)
			updateAndValidateTaskFields(taskInputTo, taskTOUpdate);
		else
			throw new BusinessException(
					List.of(new ErrorModel(HttpStatus.NOT_FOUND.value(), IErrorMessageConstants.TASK_NOT_FOUND)));
		return taskRepository.save(taskTOUpdate);
	}

	@Transactional
	public void deleteTask(Long taskId) {
		Task task = getTask(taskId);
		List<ErrorModel> errors = new ArrayList<>();
		if (task != null) {
			if (!task.getTaskOwner().getEmail()
					.equals(SecurityContextHolder.getContext().getAuthentication().getPrincipal()))
				throw new AccessDeniedException("Access Denied");
			taskRepository.delete(task);
		} else {
			errors.add(new ErrorModel(HttpStatus.NOT_FOUND.value(), IErrorMessageConstants.TASK_NOT_FOUND));
		}
		if (!errors.isEmpty()) {
			throw new BusinessException(errors);
		}

	}

	public void updateAndValidateTaskFields(Task taskInputTo, Task taskTOUpdate) {
		List<ErrorModel> errors = new ArrayList<>();
		boolean isChanged = false;
		if (taskTOUpdate != null) {
			if ((taskTOUpdate == null && taskInputTo != null) || (taskTOUpdate != null && taskInputTo == null)
					|| (!taskTOUpdate.getTaskDescription().equals(taskInputTo.getTaskDescription()))) {
				taskTOUpdate.setTaskDescription(taskInputTo.getTaskDescription());
				isChanged = true;
			}
			if (!taskTOUpdate.getTaskSubject().equals(taskInputTo.getTaskSubject())) {
				taskTOUpdate.setTaskSubject(taskInputTo.getTaskSubject());
				isChanged = true;
			}
			if (taskTOUpdate.getTaskDueDate().compareTo(taskInputTo.getTaskDueDate()) != 0) {
				taskTOUpdate.setTaskDueDate(taskInputTo.getTaskDueDate());
				isChanged = true;
			}
			if (taskTOUpdate.getTaskPriority() != taskInputTo.getTaskPriority()) {
				taskTOUpdate.setTaskPriority(taskInputTo.getTaskPriority());
				isChanged = true;
			}
			if (!taskTOUpdate.getTaskStatus().equals(taskInputTo.getTaskStatus())) {
				taskTOUpdate.setTaskStatus(taskInputTo.getTaskStatus());
				isChanged = true;
			}
		} else {
			errors.add(new ErrorModel(HttpStatus.NOT_FOUND.value(), IErrorMessageConstants.TASK_NOT_FOUND));
		}
		if (isChanged == false)
			errors.add(new ErrorModel(HttpStatus.BAD_REQUEST.value(), IErrorMessageConstants.NO_CHANGE_REQUESTED));
		if (!errors.isEmpty())
			throw new BusinessException(errors);
	}

	/************ USER FUNCTIONALTIES **************/

	@PostAuthorize("hasRole('ADMIN')")
	public List<TMSUser> getAllUsers() {
		List<TMSUser> allUsers = userRepository.findAll();
		if (allUsers != null && !allUsers.isEmpty())
			return allUsers;
		else
			throw new BusinessException(
					List.of(new ErrorModel(HttpStatus.NOT_FOUND.value(), IErrorMessageConstants.USER_NOT_FOUND)));

	}

	@PostAuthorize("hasRole('ADMIN') or returnObject.email==authentication.name")
	public TMSUser getUser(Long userId) {
		TMSUser tmsUser = null;
		Optional<TMSUser> optionalUser = userRepository.findById(userId);
		if (optionalUser.isPresent()) {
			tmsUser = optionalUser.get();
		}
		return tmsUser;
	}

	@Transactional
	public TMSUser createUser(TMSUser user) {
		validateUserEmail(user);
		validateUserAuthorities(user);
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		user.setRole(Role.ROLE_USER);
		userRepository.save(user);
		return user;
	}

	private void validateUserAuthorities(TMSUser user) {
		List<Authority> authorities = authorityRepository.findAll();
		user.setAuthorities(authorities);
	}

	public TMSUser updateUser(Long userId, TMSUser userInput) {
		TMSUser userToBeUpdated = this.getUser(userId);
		if (userToBeUpdated != null)
			validateUserUpdates(userInput, userToBeUpdated);
		else
			throw new BusinessException(
					List.of(new ErrorModel(HttpStatus.NOT_FOUND.value(), IErrorMessageConstants.USER_NOT_FOUND)));
		userInput = userRepository.save(userToBeUpdated);
		return userInput;
	}

	private void validateUserEmail(TMSUser user) {
		if (userRepository.findByEmail(user.getEmail()).isPresent()) {
			throw new BusinessException(
					List.of(new ErrorModel(HttpStatus.CONFLICT.value(), IErrorMessageConstants.EMAIL_ALREADY_EXIST)));
		}
	}

	private void validateUserUpdates(TMSUser userInput, TMSUser userToBeUpdated) {
		boolean isChanged = false;
		List<ErrorModel> errors = new ArrayList<>();
		if (!(SecurityContextHolder.getContext().getAuthentication().getPrincipal().equals(userToBeUpdated.getEmail())
				|| SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
						.anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN")))) {
			throw new AccessDeniedException("Access Denied");
		}
		if (userToBeUpdated != null) {
			if (!userToBeUpdated.getEmail().equals(userInput.getEmail())) {
				validateUserEmail(userInput);
				userToBeUpdated.setEmail(userInput.getEmail());
				isChanged = true;
			}
			if (!userToBeUpdated.getName().equals(userInput.getName())) {
				userToBeUpdated.setName(userInput.getName());
				isChanged = true;
			}

			if (!passwordEncoder.matches(userInput.getPassword(), userToBeUpdated.getPassword())) {
				userToBeUpdated.setPassword(passwordEncoder.encode(userInput.getPassword()));
				isChanged = true;
			}
			if (SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
					.anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"))) {
				if (!userInput.getRole().equals(userToBeUpdated.getRole())) {
					userToBeUpdated.setRole(userInput.getRole());
					isChanged = true;
				}
				if (!userInput.getAuthorities().equals(userToBeUpdated.getAuthorities())) {
					userToBeUpdated.setAuthorities(userInput.getAuthorities());
					isChanged = true;
				}
			}

			if (isChanged == false) {
				errors.add(new ErrorModel(HttpStatus.BAD_REQUEST.value(), IErrorMessageConstants.NO_CHANGE_REQUESTED));
			}
		}
		if (!errors.isEmpty())
			throw new BusinessException(errors);
	}


	@Transactional
	public void deleteUser(Long userId) {

		TMSUser user = this.getUser(userId);
		if (user != null) {
			if (!user.getEmail().equals(SecurityContextHolder.getContext().getAuthentication().getPrincipal()))
				throw new AccessDeniedException("Access Denied");
			userRepository.delete(user);
		} else
			throw new BusinessException(
					List.of(new ErrorModel(HttpStatus.NOT_FOUND.value(), IErrorMessageConstants.USER_NOT_FOUND)));
	}

	public TMSUser getUserByEmail(String email) {
		Optional<TMSUser> user = userRepository.findByEmail(email);
		if (user.isPresent())
			return user.get();
		else
			return null;

	}

}
